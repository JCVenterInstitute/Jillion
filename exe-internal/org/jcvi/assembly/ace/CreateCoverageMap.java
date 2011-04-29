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
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.assembly.coverage.writer.SequenceCoverageWriter;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.ReadWriteFileServer;

/**
 * @author dkatzel
 *
 *
 */
public class CreateCoverageMap {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption(CommandLineUtils.createHelpOption());
        options.addOption(new CommandLineOptionBuilder("a", "path to ace file to examine")
                .longName("ace")
                .isRequired(true)
                .build());
        
        options.addOption(new CommandLineOptionBuilder("o", "path to output directory of where to put png files")
        .longName("out")
        .isRequired(true)
        .build());
        
        options.addOption(new CommandLineOptionBuilder("p", "file prefix of all output png files " +
        		"the format of the files will be <prefix>.<contigId>.coverage.png")
        .longName("prefix")
        .isRequired(true)
        .build());

        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }

        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File aceFile = new File(commandLine.getOptionValue("a"));
            final String prefix = commandLine.getOptionValue("p");
            final ReadWriteFileServer outputDir = DirectoryFileServer.createReadWriteDirectoryFileServer(new File(commandLine.getOptionValue("o")));
        
            AceFileVisitor visitor = new AbstractAceContigBuilder(){

                @Override
                protected void visitContig(AceContig contig) {
                    String id = contig.getId();
                    try {
                        File outputPng = outputDir.createNewFile(String.format("%s.%s.coverage.png", prefix,id));
                        SequenceCoverageWriter<AcePlacedRead> sequenceCoverageWriter = new SequenceCoverageWriter<AcePlacedRead>(outputPng,"ungapped Sequence Coverage of " +id);
                        
                        sequenceCoverageWriter.write(contig);
                        sequenceCoverageWriter.close();
                    } catch (IOException e) {
                        throw new IllegalStateException("error creating new output png file",e);
                    }
                    
                }
                
            };
            
            AceFileParser.parseAceFile(aceFile, visitor);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp(options);
            System.exit(1);
        }
    }

    /**
     * @param options
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "createCoverageMap -a <ace file> -o <output dir> -p <prefix>", 
                
                "read the given ace file and create coverage map png files for each contig",
                options,
                "Created by Danny Katzel");
    }

}
