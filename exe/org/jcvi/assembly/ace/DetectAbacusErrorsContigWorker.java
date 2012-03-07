/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.ace.consed.ConsedNavigationWriter;
import org.jcvi.common.core.assembly.ace.consed.ConsensusNavigationElement;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;

/**
 * @author dkatzel
 *
 *
 */
public class DetectAbacusErrorsContigWorker {

    public static final double DEFAULT_GAP_PERCENTAGE = .5D;
    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file (required)")
                .longName("ace")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("c", "contig id to find errors for (required)")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to output file (if not used output printed to STDOUT)")
        .longName("out")
        .build());
        
        options.addOption(new CommandLineOptionBuilder("nav", "path to optional consed navigation file to see abacus errors easier in consed")
        .build());
        options.addOption(new CommandLineOptionBuilder("percent", "percentage expressed as a decimal 0 - 1 of the percentage of gaps vs non-gap " +
        		"characters per read in the region to be considered an abacus error default = "+ DEFAULT_GAP_PERCENTAGE)
        .build());
        options.addOption(CommandLineUtils.createHelpOption());     
        
        
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        try{
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            String contigId = commandLine.getOptionValue("c");
           
            final PrintWriter out = getOutputWriter(commandLine);
            final ConsedNavigationWriter consedNavWriter;
           
            File aceFile = new File(commandLine.getOptionValue("a"));
            if(commandLine.hasOption("nav")){
                //append to nav file since the parent grid util
                //is handling managing the files
                consedNavWriter = ConsedNavigationWriter.createPartial( 
                        new FileOutputStream(commandLine.getOptionValue("nav")));
            }else{
                consedNavWriter =null;
            }
            double percentGap = commandLine.hasOption("percent")? 
                    Double.parseDouble(commandLine.getOptionValue("percent"))
                    : DEFAULT_GAP_PERCENTAGE;
            final AbacusErrorFinder abacusErrorFinder = new AbacusErrorFinder(5,3,percentGap);
            try{
                AceContigDataStore datastore = IndexedAceFileDataStore.create(aceFile);
                Iterator<String> contigIds = datastore.getIds();
                while(contigIds.hasNext()){
                    String id = contigIds.next();
                    if(contigId.equals(id)){
                        try {
                            findErrorsIn(abacusErrorFinder, datastore.get(contigId), out,consedNavWriter);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        } 
                    }                                
                }
            }finally{
                IOUtil.closeAndIgnoreErrors(out,consedNavWriter);
            }
        }catch(ParseException e){
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }
        
       
    }
    private static void findErrorsIn(AbacusErrorFinder abacusErrorFinder,
            AceContig contig, PrintWriter out,ConsedNavigationWriter consedNavigationWriter) throws IOException {
        String contigId=contig.getId();
        out.println(contig.getId());
        List<Range> errorRanges = abacusErrorFinder.findAbacusErrors(contig);
       
        out.println("found "+ errorRanges.size() + " abacus errors");
        for(Range errorRange : errorRanges){
            Range residueBasedRange = errorRange;
            if(consedNavigationWriter !=null){
                consedNavigationWriter.writeNavigationElement(new ConsensusNavigationElement(contigId, errorRange, "CA abacus error"));
            }
            out.printf("abacus error range : %s%n", residueBasedRange);
            
        }
    }
    /**
     * @param commandLine
     * @return
     * @throws FileNotFoundException 
     */
    private static PrintWriter getOutputWriter(CommandLine commandLine) throws FileNotFoundException {
        if(commandLine.hasOption("o")){
            return new PrintWriter(new File(commandLine.getOptionValue("o")));
        }
        return new PrintWriter(System.out);
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "findAbacusErrors -a <ace file>", 
                
                "Parse an ace file and write out ungapped consensus coordinates of abacus assembly errors",
                options,
               "Created by Danny Katzel"
                  );
    }
}
