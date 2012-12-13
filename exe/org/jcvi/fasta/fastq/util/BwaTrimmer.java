package org.jcvi.fasta.fastq.util;


import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.trace.fastq.FastqDataStore;
import org.jcvi.common.core.seq.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.common.core.seq.trace.fastq.FastqQualityCodec;
import org.jcvi.common.core.seq.trace.fastq.FastqRecord;
import org.jcvi.common.core.seq.trace.fastq.FastqRecordBuilder;
import org.jcvi.common.core.seq.trace.fastq.FastqRecordWriter;
import org.jcvi.common.core.seq.trace.fastq.FastqRecordWriterBuilder;
import org.jcvi.common.core.seq.trace.fastq.FastqUtil;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.qual.trim.BwaQualityTrimmer;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;

public class BwaTrimmer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws DataStoreException 
	 */
	public static void main(String[] args) throws IOException, DataStoreException {
		Options options = new Options();
		options.addOption(new CommandLineOptionBuilder("q", "quality threshold value (required)")
							.isRequired(true)
							.build());
		
		options.addOption(new CommandLineOptionBuilder("in", "input fastq file (required)")
							.isRequired(true)
							.build());
		options.addOption(new CommandLineOptionBuilder("out", "output fastq file (required)")
						.isRequired(true)
						.build());
		
		options.addOption(new CommandLineOptionBuilder("m", "minimum trimmed sequence length. (required) " +
				"This is the minimum sequence length a trimmed sequence can be in order to be written to the output file.")
							.isRequired(true)
							.build());
		
		options.addOption(new CommandLineOptionBuilder("z", "quality encoding, a value of 33 is sanger, a value of 64 is illumina.  " +
				"If this value is not specified, then the quality encoding will be auto-detected which adds a slight performance penalty.")
						.build());
		
		options.addOption(CommandLineUtils.createHelpOption());
		
		if(CommandLineUtils.helpRequested(args)){
			printHelp(options);
			System.exit(0);
		}
		CommandLine commandLine=null;
		try {
			commandLine = CommandLineUtils.parseCommandLine(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		long start = System.currentTimeMillis();
		File fastqFile = new File(commandLine.getOptionValue("in"));
		File outputFile = new File(commandLine.getOptionValue("out"));
		int minLength = Integer.parseInt(commandLine.getOptionValue("m"));
		
		PhredQuality threshold = PhredQuality.valueOf(Integer.parseInt(commandLine.getOptionValue("q")));
		FastqQualityCodec codec = getQualityCodec(commandLine, fastqFile);
		FastqDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
										.hint(DataStoreProviderHint.OPTIMIZE_ITERATION)
										.qualityCodec(codec)
										.build();
				
		BwaQualityTrimmer trimmer = new BwaQualityTrimmer(threshold);
		IOUtil.mkdirs(outputFile.getParentFile());
		FastqRecordWriter fastqWriter = new FastqRecordWriterBuilder(outputFile)
									.build();
		StreamingIterator<FastqRecord> iter=null;
		try{
			iter = datastore.iterator();
			while(iter.hasNext()){
				FastqRecord next =iter.next();
				
				if(next.getNucleotideSequence().getLength() < minLength){
					continue;
				}
				Range trimRange = trimmer.trim(next.getQualitySequence());
				
				if(trimRange.getLength() < minLength){					
					continue;
				}
				FastqRecord trimmedSequence = new FastqRecordBuilder(next.getId(),
						new NucleotideSequenceBuilder(next.getNucleotideSequence())
							.trim(trimRange)
							.build(),
						new QualitySequenceBuilder(next.getQualitySequence())
							.trim(trimRange)
							.build())
				.comment(next.getComment())
				.build();
				
				fastqWriter.write(trimmedSequence);

			}
			long end = System.currentTimeMillis();
			System.out.println("took = "+ (end-start)/1000 + " seconds");
		}finally{
			IOUtil.closeAndIgnoreErrors(fastqWriter,iter,datastore);
		}
	}

	private static FastqQualityCodec getQualityCodec(CommandLine commandLine,
			File fastqFile) throws IOException {
		if(commandLine.hasOption("z")){
			int value = Integer.parseInt(commandLine.getOptionValue("z"));
			if(value == 33){
				return FastqQualityCodec.SANGER;
			}
			if(value == 64){
				return FastqQualityCodec.ILLUMINA;
			}
			throw new IllegalArgumentException("invalid z value : " + value);
		}
		return FastqUtil.guessQualityCodecUsed(fastqFile);
	}

	private static void printHelp(Options options) {
		 HelpFormatter formatter = new HelpFormatter();
	        formatter.printHelp( "bwaTrimmer [OPTIONS] -in <fastq file> -out <fastq file> -q <quality threshold> -m <min trim length>", 
	                
	                "Java Common implementation of TrimBWAstyle.pl which will trim a " +
	                "fastq file using the algorithm used in bwa.",
	                options,
	                "Created by Danny Katzel"
	                  );
		
	}

}

