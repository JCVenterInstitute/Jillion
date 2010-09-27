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
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.io.IOUtil;

/**
 * {@code ConvertGappedFasta2UngappedFasta} converts a gapped
 * nucleotide fasta file into an ungapped
 * fasta file.
 * @author dkatzel
 *
 *
 */
public class ConvertGappedFasta2UngappedFasta {

    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        Options options = new Options();
        
        options.addOption(new CommandLineOptionBuilder("i", "input gapped fasta")
                    .isRequired(true)
                    .build());
        
        options.addOption(new CommandLineOptionBuilder("o", "output ungapped fasta")
                    .isRequired(true)
                    .build());
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File fastaFile = new File(commandLine.getOptionValue("i"));
            File outputFile = new File(commandLine.getOptionValue("o"));
            final PrintWriter output = new PrintWriter(outputFile);
            FastaVisitor visitor = new AbstractNucleotideFastaVisitor() {

                @Override
                protected void visitNucleotideFastaRecord(
                        NucleotideSequenceFastaRecord fastaRecord) {
                    NucleotideSequenceFastaRecord ungapped =
                        new DefaultEncodedNucleotideFastaRecord(fastaRecord.getIdentifier(), fastaRecord.getComments(),
                                fastaRecord.getValues().decodeUngapped());
                    output.print(ungapped);
                    
                }
                
                
            };
            FastaParser.parseFasta(fastaFile, visitor);
            IOUtil.closeAndIgnoreErrors(output);
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }

    }

    /**
     * @param options
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "gappedFasta2ungapped -i <gapped fasta file> -o <ungapped fata file>", 
                
                "convert a gapped fasta file into an ungapped version" +
                " (ids, comments and fasta record order is maintained)",
                options,
                "Created by Danny Katzel");
        
    }

}
