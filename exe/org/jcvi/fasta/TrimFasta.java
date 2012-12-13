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

package org.jcvi.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fasta.FastaFileVisitor;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordBuilder;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public class TrimFasta {

    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        Options options = new Options();
        options.addOption(CommandLineUtils.createHelpOption());
        options.addOption(new CommandLineOptionBuilder("f", "input untrimmed fasta")
                        .longName("fasta")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("l", "left trim point file")
                        .isRequired(true)
                        .longName("left")
                        .build());
        options.addOption(new CommandLineOptionBuilder("r", "right trim point file")
                .isRequired(true)
                .longName("right")
                .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to output trimmed fasta")
                    .isRequired(true)
                    .longName("out")
                    .build());
        
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File fastaFile = new File(commandLine.getOptionValue("f"));
            File outputFile = new File(commandLine.getOptionValue("o"));
            
            File leftTrimFile = new File(commandLine.getOptionValue("l"));
            File rightTrimFile = new File(commandLine.getOptionValue("r"));
            
            final Map<String, Integer> leftTrimPoints = parseTrimPoints(leftTrimFile);
            final Map<String, Integer> rightTrimPoints = parseTrimPoints(rightTrimFile);
            
            final NucleotideSequenceFastaRecordWriter writer = new NucleotideSequenceFastaRecordWriterBuilder(outputFile)
            													.build();
            FastaFileVisitor visitor = new AbstractFastaVisitor() {
				
				@Override
				protected boolean visitRecord(String id, String comment, String entireBody) {
					NucleotideSequenceBuilder builder =new NucleotideSequenceBuilder(entireBody);
                    long untrimmedLength = builder.getLength();
                    Integer l = leftTrimPoints.get(id);
                    Integer r = rightTrimPoints.get(id);
                    Range trimRange = Range.of(l ==null? 0:l, 
                                                    untrimmedLength -1 -(r==null?0:r));
                    
                    try {
                    	NucleotideSequence trimmedSequence = builder
                    										.trim(trimRange)
                    										.build();
                        writer.write(id, trimmedSequence, comment);
                    } catch (IOException e) {
                       throw new IllegalStateException("error writing to output fasta",e);
                    }
                    return true;
                }
            };
             
            FastaFileParser.parse(fastaFile, visitor);
            IOUtil.closeAndIgnoreErrors(writer);
            
        } catch (ParseException e) {
            e.printStackTrace();
            printHelp(options);
            System.exit(1);
        }

    }
    
    private static Map<String, Integer> parseTrimPoints(File trimPointFile) throws FileNotFoundException{
        Map<String, Integer> trimPoint = new HashMap<String, Integer>();
        Scanner scanner = new Scanner(trimPointFile);
        while(scanner.hasNext()){            
            trimPoint.put(scanner.next(), scanner.nextInt());
        }
        scanner.close();
        return trimPoint;
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "trimFasta -f <input fasta> -l <left trim pts> -r <right trim pts> -o <output fasta>", 
                
                "read in a fasta file and output a new fasta file which contains " +
                "all the same records, except they are trimmed using the left and right trim points" +
                " given by the other input files",
                options,
                "Created by Danny Katzel");
        
    }

}
