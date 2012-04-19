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
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.assembly.ace.AbstractAceContigBuilder;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.assembly.ctg.CtgFileWriter;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.io.idReader.IdReaderException;

/**
 * {@code Ace2Contig} parses a .ace file
 * and outputs a corresponding .contig file.
 * Optional parameters allow contigs to be included/excluded
 * from the output.
 * 
 * @author dkatzel
 *
 *
 */
public class Ace2Contig {

    /**
     * @param args
     * @throws IOException 
     * @throws IdReaderException 
     */
    public static void main(String[] args) throws IOException, IdReaderException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file")
                .longName("ace")
                .isRequired(true)
                .build());
        options.addOption(new CommandLineOptionBuilder("c", "output path to contig file to write")
                .longName("contig")        
                .isRequired(true)
                .build());
        CommandLineUtils.addIncludeAndExcludeDataStoreFilterOptionsTo(options);
       
        
        options.addOption(CommandLineUtils.createHelpOption());

        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File aceFile = new File(commandLine.getOptionValue("a"));
            File contigOutFile = new File(commandLine.getOptionValue("c"));
            File rootDir = contigOutFile.getAbsoluteFile().getParentFile();
            if(rootDir!=null){
                IOUtil.mkdirs(rootDir);
            }
            final CtgFileWriter writer = new CtgFileWriter(new FileOutputStream(contigOutFile));
            final DataStoreFilter filter = CommandLineUtils.createDataStoreFilter(commandLine);
            
            
            AceFileVisitor aceVisitor = new AbstractAceContigBuilder(){

                @Override
                protected void visitContig(AceContig contig) {
                    if(filter.accept(contig.getId())){
                        try {
                            writer.write(contig);
                        } catch (Exception e) {
                            throw new IllegalStateException("error writing contig "+ contig.getId(), e);
                        }
                    }
                }
            };
            try{
                AceFileParser.parse(aceFile, aceVisitor);
            }finally{
                IOUtil.closeAndIgnoreErrors(writer);
            }
            
        } catch (ParseException e) {            
            e.printStackTrace();
            printHelp(options);
            System.exit(1);
        }
        

    }

    
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "ace2Contig -ace <ace file> -contig <output contig> [OPTIONS]",
        		 
                
                "create a contig file from the given ace file.  " +
                "Please Note, if -i AND -e are specified, then " +
                "the exclude list will be applied to the include list",
                options,
                "Created by Danny Katzel");
    }
    
    

}
