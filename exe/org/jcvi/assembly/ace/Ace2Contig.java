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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.contig.ContigFileWriter;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.fastX.ExcludeFastXIdFilter;
import org.jcvi.fastX.FastXFilter;
import org.jcvi.fastX.IncludeFastXIdFilter;
import org.jcvi.fastX.NullFastXFilter;
import org.jcvi.io.IOUtil;
import org.jcvi.io.idReader.DefaultFileIdReader;
import org.jcvi.io.idReader.IdReader;
import org.jcvi.io.idReader.IdReaderException;
import org.jcvi.io.idReader.StringIdParser;

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
        
        options.addOption(new CommandLineOptionBuilder("i", "optional file of ids to include")
                            .build());
        options.addOption(new CommandLineOptionBuilder("e", "optional file of ids to exclude")
                            .build());
        
        
        options.addOption(CommandLineUtils.createHelpOption());

        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File aceFile = new File(commandLine.getOptionValue("a"));
            final ContigFileWriter writer = new ContigFileWriter(new FileOutputStream(commandLine.getOptionValue("c")));
            final File idFile;
            final FastXFilter filter;
            if(commandLine.hasOption("i")){
                idFile =new File(commandLine.getOptionValue("i"));
                Set<String> includeList=parseIdsFrom(idFile);
                if(commandLine.hasOption("e")){
                    Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue("e")));
                    includeList.removeAll(excludeList);
                }
                filter = new IncludeFastXIdFilter(includeList);
                
            }else if(commandLine.hasOption("e")){
                idFile =new File(commandLine.getOptionValue("e"));
                filter = new ExcludeFastXIdFilter(parseIdsFrom(idFile));
            }else{
                filter = NullFastXFilter.INSTANCE;
            }
            
            
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
                AceFileParser.parseAceFile(aceFile, aceVisitor);
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
                "the exclude list will be applied to the include be included in the options, not both",
                options,
                "Created by Danny Katzel");
    }
    
    private static Set<String> parseIdsFrom(final File idFile)   throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new StringIdParser());
        Set<String> ids = new HashSet<String>();
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }

}
