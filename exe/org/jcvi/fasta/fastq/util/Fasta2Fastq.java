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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fasta.FastaFileVisitor;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaFileDataStoreBuilder;
import org.jcvi.common.core.seq.trace.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.trace.fastq.FastqRecordWriter;
import org.jcvi.common.core.seq.trace.fastq.FastqRecordWriterBuilder;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

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
                        "should encode output fastq file in SANGER fastq file format (default is ILLUMINA 1.3+)")
                        .isFlag(true)
                       .build());
        
        options.addOption(new CommandLineOptionBuilder("o", 
                        "output fastq file")
                        .isRequired(true)
                        .build());
        options.addOption(new CommandLineOptionBuilder("tempDir", "temp directory")
                                        .build());
        options.addOption(CommandLineUtils.createHelpOption());
        OptionGroup group = new OptionGroup();
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOptionGroup(group);
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            if(commandLine.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            
            boolean useSanger = commandLine.hasOption("sanger");

            final DataStoreFilter filter = CommandLineUtils.createDataStoreFilter(commandLine);
           
            final FastqQualityCodec fastqQualityCodec = useSanger? FastqQualityCodec.SANGER: FastqQualityCodec.ILLUMINA;
          
            File qualFile = new File(commandLine.getOptionValue("q"));
            
            final QualitySequenceFastaDataStore qualityDataStore = new QualitySequenceFastaFileDataStoreBuilder(qualFile)
															            .filter(filter)
															            .build();
            
            File seqFile = new File(commandLine.getOptionValue("s"));
            final FastqRecordWriter writer = new FastqRecordWriterBuilder(new File(commandLine.getOptionValue("o")))
            								.qualityCodec(fastqQualityCodec)
            								.build();
            
            FastaFileVisitor visitor = new AbstractFastaVisitor() {
                
                @Override
                public boolean visitRecord(String id, String comment, String entireBody) {
                    try {
                        if(filter.accept(id)){
                            QualitySequence qualities =qualityDataStore.get(id).getSequence();
                            if(qualities ==null){
                                throw new IllegalStateException("no quality values for "+ id);
                            }
                            writer.write(id, 
                                   new NucleotideSequenceBuilder(entireBody).build(), qualities,comment);
    
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException("error getting quality data for "+ id);
                    }
                    return true;
                    
                }
            };
            FastaFileParser.parse(seqFile, visitor);
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
