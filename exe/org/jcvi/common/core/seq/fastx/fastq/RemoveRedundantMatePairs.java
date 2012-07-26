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

package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public class RemoveRedundantMatePairs {

    /**
     * @param args
     * @throws IOException 
     * @throws DataStoreException 
     */
    public static void main(String[] args) throws IOException, DataStoreException {
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("mate1", "path to mate 1 file")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("mate2", "path to mate 1 file")
            .isRequired(true)
            .build());
        options.addOption(new CommandLineOptionBuilder("o", "path to output dir")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("prefix", "output prefix to name new files.  New files will look like <prefix>_1.fastq and <prefix>_2.fastq")
                    .isRequired(true)
                    .build());
        options.addOption(new CommandLineOptionBuilder("n", "number of bases to compare for redundancy")
        .isRequired(true)
        .build());
        options.addOption(new CommandLineOptionBuilder("sanger", 
                "input fastq file is encoded as a SANGER fastq file (default is ILLUMINA 1.3+)")
                    .isFlag(true)
                       .build());
        options.addOption(new CommandLineOptionBuilder("s", "expected number of non-redundant mates (used to preallocate hash)")
        .isRequired(true)
        .build());
        
        options.addOption(CommandLineUtils.createHelpOption());
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            
            int expectedSize = Integer.parseInt(commandLine.getOptionValue("s"));
            FastqQualityCodec qualityCodec =commandLine.hasOption("sanger")?
                                                FastqQualityCodec.SANGER:
                                                FastqQualityCodec.ILLUMINA;
            
            File mate1 = new File(commandLine.getOptionValue("mate1"));
            File mate2 = new File(commandLine.getOptionValue("mate2"));
            
            int comparisonRangeLength = Integer.parseInt(commandLine.getOptionValue("n"));
            Range comparisonRange=Range.createOfLength(comparisonRangeLength);
            StreamingIterator<FastqRecord> mate1Iterator=null;
            StreamingIterator<FastqRecord> mate2Iterator  =null;
            File outputDir = new File(commandLine.getOptionValue("o"));
            outputDir.mkdirs();
            String prefix = commandLine.getOptionValue("prefix");
            
            File nonRedundantMate1 = new File(outputDir,prefix+"_1.fastq");
            File nonRedundantMate2 = new File(outputDir,prefix+"_2.fastq");
            
            FileOutputStream out1 = new FileOutputStream(nonRedundantMate1);
            FileOutputStream out2 = new FileOutputStream(nonRedundantMate2);
            Set<NucleotideSequence> nonRedundantSet = new HashSet<NucleotideSequence>(expectedSize+1,1F);
            MatePairIterator iter =null;
          
            try{
            	  iter= new MatePairIterator(mate1, mate2, qualityCodec);
	            while(iter.hasNext()){
	            	MatePair matePair =iter.next();
	               
	                NucleotideSequence seq = matePair.getConcatenatedSequence(comparisonRange);

	                if(!nonRedundantSet.contains(seq)){
	                	 FastqRecord forward = matePair.getForward();
	 	                FastqRecord reverse = matePair.getReverse();
	                    out1.write(forward.toFormattedString(qualityCodec).getBytes(IOUtil.UTF_8));
	                    out2.write(reverse.toFormattedString(qualityCodec).getBytes(IOUtil.UTF_8));
	                    nonRedundantSet.add(seq);
	                }
	            }
	           
	            System.out.println("final number of mates seen ="+ iter.getRecordCounter());
	            System.out.println("num mates written ="+ nonRedundantSet.size());
            }finally{
            	IOUtil.closeAndIgnoreErrors(iter);
            	out1.close();
                out2.close();
               
            }
            
        } catch (ParseException e) {
            printHelp(options);
            System.exit(1);
        }
        
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "remove redundant mates [OPTIONS] -mate1 <fastq> -mate2 <fastq> -o <output dir> -prefix <prefix>", 
                
                "scan large fastq mate files and remove any redundant pairs",
                options,
               "Created by Danny Katzel"
                  );
    }
    
    private static class MismatchedMateFiles extends RuntimeException{

        private static final long serialVersionUID = -4501169214579449464L;

        public MismatchedMateFiles(long recordsSeen){
            super(String.format("input mate files have different number of records, both should have %d", recordsSeen));
        }
    }
    private static final class MatePair{
    	FastqRecord forward,reverse;

		private MatePair(FastqRecord forward, FastqRecord reverse) {
			this.forward = forward;
			this.reverse = reverse;
		}

		public FastqRecord getForward() {
			return forward;
		}

		public FastqRecord getReverse() {
			return reverse;
		}
    	
		public NucleotideSequence getConcatenatedSequence(Range subRangePerMate){
			NucleotideSequenceBuilder builder =new NucleotideSequenceBuilder((int)subRangePerMate.getLength()*2);
            builder.append(forward.getSequence().asList(subRangePerMate));
            builder.append(reverse.getSequence().asList(subRangePerMate));
            return builder.build();
		}
    	
    }
    private static final class MatePairIterator implements StreamingIterator<MatePair>{
    	private final StreamingIterator<FastqRecord> mate1Iterator;
    	private final StreamingIterator<FastqRecord> mate2Iterator;
    	private long recordCounter=0L;
    	public MatePairIterator(File fastq1, File fastq2, FastqQualityCodec qualityCodec) throws FileNotFoundException, DataStoreException{
    		mate1Iterator = LargeFastqFileDataStore.create(fastq1, qualityCodec).iterator();
        	mate2Iterator = LargeFastqFileDataStore.create(fastq2, qualityCodec).iterator();
        
    	}
    	
    	
		public long getRecordCounter() {
			return recordCounter;
		}


		@Override
		public synchronized boolean hasNext() {
			if(mate1Iterator.hasNext() && mate2Iterator.hasNext()){
				return true;
			}
			if((mate1Iterator.hasNext() && !mate2Iterator.hasNext()) || 
					(!mate1Iterator.hasNext() && mate2Iterator.hasNext())){
				throw new MismatchedMateFiles(recordCounter);
			}
			return false;
		}

		@Override
		public void close() throws IOException {
			IOUtil.closeAndIgnoreErrors(mate1Iterator, mate2Iterator);
		}

		@Override
		public synchronized MatePair next() {
			MatePair pair= new MatePair(mate1Iterator.next(), mate2Iterator.next());
			recordCounter++;
			if(recordCounter%100000==0){
                System.out.println("mates seen = "+recordCounter);
            }
			return pair;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove() not allowed on closeable iterator");
			
		}
    	
    }
}
