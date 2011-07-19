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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaVisitor;
import org.jcvi.common.core.seq.nuc.fasta.AbstractNucleotideFastaVisitor;
import org.jcvi.common.core.seq.nuc.fasta.DefaultNucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.nuc.fasta.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;

/**
 * @author dkatzel
 *
 *
 */
public class ReverseComplimentFasta {

    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("fasta", "path to fasta file")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to output reverse complimented file")
        .longName("out")
        .isRequired(true)
        .build());
        
        options.addOption(CommandLineUtils.createHelpOption());
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            File fastaFile = new File(commandLine.getOptionValue("fasta"));
            File outputFile = new File(commandLine.getOptionValue("o"));
            final OutputStream out = new FileOutputStream(outputFile);
            FastaVisitor visitor = new AbstractNucleotideFastaVisitor() {
                
                @Override
                protected boolean visitFastaRecord(
                        NucleotideSequenceFastaRecord fastaRecord) {
                    List<NucleotideGlyph> revcompliment =NucleotideGlyph.reverseCompliment(
                            fastaRecord.getValue().decode());
                    try {
                        out.write(new DefaultNucleotideSequenceFastaRecord(
                                fastaRecord.getId(),
                                fastaRecord.getComment(),
                                revcompliment).toString().getBytes());
                    } catch (IOException e) {
                       throw new IllegalStateException("error writing to output fasta",e);
                    }
                    return true;
                }
            };
            FastaParser.parseFasta(fastaFile, visitor);
            IOUtil.closeAndIgnoreErrors(out);
            
        } catch (ParseException e) {
            e.printStackTrace();
            printHelp(options);
            System.exit(1);
        }
        
        
        

    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "reverseCompliment -fasta <input fasta> -o <output fasta>", 
                
                "read in a fasta file and output a new fasta file which contains " +
                "all the same records, except they are reverse complimented",
                options,
                "Created by Danny Katzel");
        
    }

}
