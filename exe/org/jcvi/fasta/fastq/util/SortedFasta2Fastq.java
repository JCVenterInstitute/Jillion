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
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaFileDataStoreBuilder;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordBuilder;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordWriter;
import org.jcvi.common.core.seq.fastx.fastq.FastqRecordWriterBuilder;
import org.jcvi.common.core.util.DateUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * {@code SortedFasta2Fastq} is a another fasta to fastq conversion tool
 * which makes the assumption that the records in the given seq and qual fasta files
 * are in the same order, this allows the converter to skip the time consuming
 * index step.
 * 
 * @author dkatzel
 *
 *
 */
public class SortedFasta2Fastq {
   
    private static final int DEFAULT_QUEUE_SIZE = 1000;

    
    
   
    /**
     * @param args
     * @throws InterruptedException 
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws InterruptedException, IOException, DataStoreException {
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
        //DEFAULT_QUEUE_SIZE
        options.addOption(new CommandLineOptionBuilder("b", 
        "buffer size, the number of records to buffer while we parse," +
        "this higher this number is, the faster we can convert, but the more memory it takes" +
        " (default is " +DEFAULT_QUEUE_SIZE+ ")")
        .isFlag(true)
       .build());
        options.addOption(new CommandLineOptionBuilder("o", 
                        "output fastq file")
                        .isRequired(true)
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
            final File qualFile = new File(commandLine.getOptionValue("q"));
            final File seqFile = new File(commandLine.getOptionValue("s"));

            final DataStoreFilter filter = CommandLineUtils.createDataStoreFilter(commandLine);
            
            final FastqQualityCodec fastqQualityCodec = useSanger? FastqQualityCodec.SANGER: FastqQualityCodec.ILLUMINA;
            final FastqRecordWriter writer = new FastqRecordWriterBuilder(new File(commandLine.getOptionValue("o")))
													.qualityCodec(fastqQualityCodec)
													.build();
            long startTime = System.currentTimeMillis();
            StreamingIterator<NucleotideSequenceFastaRecord> nucleotideIter=null;
            StreamingIterator<QualitySequenceFastaRecord> qualityIter =null;
            try{
            	nucleotideIter = new NucleotideSequenceFastaFileDataStoreBuilder(seqFile)
            						.hint(DataStoreProviderHint.OPTIMIZE_ITERATION)
            						.filter(filter)
            						.build()
            						.iterator();
            
            	qualityIter =  new QualitySequenceFastaFileDataStoreBuilder(qualFile)
				            			.hint(DataStoreProviderHint.OPTIMIZE_ITERATION)
				            			.filter(filter)
				            			.build()
				            			.iterator();
	            while(nucleotideIter.hasNext() && qualityIter.hasNext()){
	            	QualitySequenceFastaRecord qualityFasta = qualityIter.next();
	            	NucleotideSequenceFastaRecord seqFasta = nucleotideIter.next();
	            	if(!seqFasta.getId().equals(qualityFasta.getId())){
	                    throw new IllegalStateException(String.format(
	                            "seq and qual records are not in the same order: seq= %s qual = %s",
	                            seqFasta.getId(),
	                            qualityFasta.getId()));
	                    
	                }
	              //here we have a valid seq and qual
	                FastqRecord fastq = new FastqRecordBuilder(seqFasta.getId(), 
	                        seqFasta.getSequence(), qualityFasta.getSequence())
	                				.build();
	
	                writer.write(fastq);
	            }
	            if(nucleotideIter.hasNext()){
	            	throw new IllegalStateException("more seq records than qualities");
	            }
	            if(qualityIter.hasNext()){
	            	throw new IllegalStateException("more quality records than sequences");
	            }
            }finally{
            	IOUtil.closeAndIgnoreErrors(nucleotideIter, qualityIter,writer);
            }
           
           long endTime = System.currentTimeMillis();
           System.out.println(DateUtil.getElapsedTimeAsString(endTime - startTime));
           
            
           
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }

    }
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "sortedFasta2Fastq [OPTIONS] -s <seq file> -q <qual file> -o <fastq file>", 
                
                "Parse a  sorted seq and qual file (ids in same order) and write the results out a fastq file. "+
                "This version should be orders of magnitude faster than fasta2Fastq because no indexing is required "+
                "and the seq and qual files are parsed at the same time in different threads.",
                options,
               "Created by Danny Katzel"
                  );
    }
    


}
