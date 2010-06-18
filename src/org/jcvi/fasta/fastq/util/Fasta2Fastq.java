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

package org.jcvi.fasta.fastq.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.cli.CommandLineOptionBuilder;
import org.jcvi.cli.CommandLineUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.AbstractFastaVisitor;
import org.jcvi.fasta.FastaParser;
import org.jcvi.fasta.FastaVisitor;
import org.jcvi.fasta.QualityFastaH2DataStore;
import org.jcvi.fasta.fastq.DefaultFastQRecord;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.FastQRecord;
import org.jcvi.fasta.fastq.FastQUtil;
import org.jcvi.fasta.fastq.SangerFastQQualityCodec;
import org.jcvi.fasta.fastq.illumina.IlluminaFastQQualityCodec;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.TemporaryDirectoryFileServer;

/**
 * @author dkatzel
 *
 *
 */
public class Fasta2Fastq {

    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("s", 
                                    "input sequence FASTA file")
                        .longName("sequence")
                        .build());
        options.addOption(new CommandLineOptionBuilder("q", 
                     "input quality FASTA file")
                    .longName("quality")
                    .build());
        options.addOption(new CommandLineOptionBuilder("sanger", 
                        "should encode output fastq file in SANGER fastq file (default is ILLUMINA 1.3+)")
                        .isFlag(true)
                       .build());
        
        options.addOption(new CommandLineOptionBuilder("o", 
                        "output fastq file")
                        .isRequired(true)
                        .build());
        
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            boolean useSanger = commandLine.hasOption("sanger");
            RunLengthEncodedGlyphCodec qualityCodec = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;
            final FastQQualityCodec fastqQualityCodec = useSanger? new SangerFastQQualityCodec(qualityCodec): new IlluminaFastQQualityCodec(qualityCodec);
        
            //parse nucleotide data to temp file
            TemporaryDirectoryFileServer tempDir = DirectoryFileServer.createTemporaryDirectoryFileServer();
     
            H2QualityDataStore h2DataStore = new H2QualityDataStore(tempDir.createNewFile("h2Qualities"));
            File qualFile = new File(commandLine.getOptionValue("q"));
            final QualityFastaH2DataStore qualityDataStore = new QualityFastaH2DataStore(qualFile, h2DataStore);
            
            File seqFile = new File(commandLine.getOptionValue("s"));
            final PrintWriter writer = new PrintWriter(commandLine.getOptionValue("o"));
            
            FastaVisitor visitor = new AbstractFastaVisitor() {
                
                @Override
                public void visitRecord(String id, String comment, String entireBody) {
                    try {
                        EncodedGlyphs<PhredQuality> qualities =qualityDataStore.get(id);
                        if(qualities ==null){
                            throw new IllegalStateException("no quality values for "+ id);
                        }
                        FastQRecord fastq = new DefaultFastQRecord(id, 
                                new DefaultNucleotideEncodedGlyphs(entireBody.replaceAll("\\s+", "")), qualities);

                        writer.print(FastQUtil.encode(fastq, fastqQualityCodec));
                    } catch (DataStoreException e) {
                        throw new IllegalStateException("error getting quality data for "+ id);
                    }
                    
                }
            };
            FastaParser.parseFasta(seqFile, visitor);
            writer.close();
            IOUtil.closeAndIgnoreErrors(qualityDataStore);
            
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }

    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "fasta2Fastq [OPTIONS] -s <seq file> -q <qual file> -o <fastq file>", 
                
                "Parse a  seq and qual file and write the results out a fastq file",
                options,
               "Created by Danny Katzel"
                  );
    }

}
