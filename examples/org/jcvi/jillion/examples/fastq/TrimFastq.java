/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.examples.fastq;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaCollectors;
import org.jcvi.jillion.trace.fastq.*;
import org.jcvi.jillion.trace.fastq.FastqFileReader.Results;
import org.jcvi.jillion.trim.BwaQualityTrimmer;
import org.jcvi.jillion.trim.Trimmer;

public class TrimFastq {

	public static void main(String[] args) throws IOException, DataStoreException {
		File fastqFile = new File("/path/to/input.fastq");
		File outputFile = new File("/path/to/output.fastq");
		
		long minLength = 30; // or whatever size you want
		
		
		//trim_4_0(fastqFile, outputFile, minLength);
		//trim_5_2(fastqFile, outputFile, minLength);
//		trim_5_3(fastqFile, outputFile, minLength);
		
	}
	private static void trim_5_3_2(File fastqFile, File outputFile,
								 long minLength)
			throws IOException, DataStoreException {
		Set<String> ids = new HashSet<>();
		//populate ids Set for ids to include

		Trimmer<FastqRecord> bwaTrimmer = BwaQualityTrimmer.createFor(PhredQuality.valueOf(20));

		try(   Results parsedFastqs = FastqFileReader.read(fastqFile);

			   FastqWriter writer = new FastqWriterBuilder(outputFile)
					   // use same codec as input which was autoDetected
					   .qualityCodec(parsedFastqs.getCodec()).build();
		){


			parsedFastqs.records()
					.filter(fastq -> fastq.getLength() >= minLength)
					.filter(fastq -> ids.contains(fastq.getId()))

					.map(fastq-> {
						Range trimRange = bwaTrimmer.trim(fastq);

						if (trimRange.getLength() >= minLength) {
							return fastq.trim(trimRange);
						}
						return null;
					})
					.filter(Objects::nonNull)
					.collect(FastqCollectors.write(writer));

		}//streams, writer and datastores will autoclose when end of scope reached.

	}
	 private static void trim_5_3(File fastqFile, File outputFile,
	            long minLength)
	            throws IOException, DataStoreException {
	     Set<String> ids = new HashSet<>();
	     //populate ids Set for ids to include
	     
	        Trimmer<FastqRecord> bwaTrimmer = BwaQualityTrimmer.createFor(PhredQuality.valueOf(20));
	        
	        try(   Results parsedFastqs = FastqFileReader.read(fastqFile);
	                
	                FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                        // use same codec as input which was autoDetected
                                                        .qualityCodec(parsedFastqs.getCodec()).build();
	                ){
	                        
	                        //uses Jillion's custom ThrowingStream which has methods
	                        //that can throw exceptions since our writer will throw an IOException
	                        parsedFastqs.records()
	                              .filter(fastq -> fastq.getLength() >= minLength)
	                              .filter(fastq -> ids.contains(fastq.getId()))
	                              .throwingForEach(fastq -> {
                	                            Range trimRange = bwaTrimmer.trim(fastq);
                	                            
                	                            if (trimRange.getLength() >= minLength) {
                	                                writer.write(fastq, trimRange);
                	                            }                   
                	                        });
	                        
	                }//streams and datastores will autoclose when end of scope reached.
	    }
	 
	 private static void trim_5_3adapter(File fastqFile, File outputFile,
                 long minLength)
                 throws IOException, DataStoreException {
             
             Trimmer<FastqRecord> bwaTrimmer = BwaQualityTrimmer.createFor(PhredQuality.valueOf(20));
             
             Function<FastqRecord, FastqRecord> trimAdapter = (fastq) ->{
                 Range trimRange = bwaTrimmer.trim(fastq);
                 
                 if(trimRange.getLength() < minLength){
                     return null;
                 }
                 return fastq.toBuilder().trim(trimRange).build();
             };
             
             try(   Results parsedFastqs = FastqFileReader.read(fastqFile);
                     
                     FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                     // use same codec as input which was autoDetected
                                                     .qualityCodec(parsedFastqs.getCodec())
                                                     .adapt(trimAdapter)
                                                     .build();
                     ){
                             
                             //uses Jillion's custom ThrowingStream which has methods
                             //that can throw exceptions since our writer will throw an IOException
                             parsedFastqs.records()
                                   .filter(fastq -> fastq.getLength() >= minLength)   
                                   .throwingForEach(fastq -> writer.write(fastq)) ;
                             
                     }//streams and datastores will autoclose when end of scope reached.
         }
	
    private static void trim_5_2(File fastqFile, File outputFile,
            long minLength)
            throws IOException, DataStoreException {
        
        BwaQualityTrimmer bwaTrimmer = new BwaQualityTrimmer(PhredQuality.valueOf(20));
        
        try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
							.hint(DataStoreProviderHint.ITERATION_ONLY)
							.filterRecords(fastq -> fastq.getLength() >= minLength)
							.build();
				
			FastqWriter writer  = new FastqWriterBuilder(outputFile)
			                                //use same codec as input which was autoDetected
							.qualityCodec(datastore.getQualityCodec())
							.build();
			StreamingIterator<FastqRecord> iter = datastore.iterator();
		){
			//our datastore filtered out anything 
                        //that didn't meet our length requirements 
                        //so it's safe to write everything in the stream.
		        //uses Jillion's custom ThrowingStream which has methods
		        //that can throw exceptions since our writer will throw an IOException
                        while( iter.hasNext()){
                            FastqRecord fastq = iter.next();
           
                            Range trimRange = bwaTrimmer.trim(fastq.getQualitySequence());
                            if (trimRange.getLength() >= minLength) {
                                writer.write(fastq, trimRange);
                            }			
			}
			
		}//streams and datastores will autoclose when end of scope reached.
    }
    private static void trim_4_0(File fastqFile, File outputFile,
            long minLength)
            throws IOException, DataStoreException {
        
        BwaQualityTrimmer bwaTrimmer = new BwaQualityTrimmer(PhredQuality.valueOf(20));
        
        try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
                                                        .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                        .build();
                                
                        FastqWriter writer  = new FastqWriterBuilder(outputFile)
                                                        .qualityCodec(FastqQualityCodec.ILLUMINA) //can't tell what codec is in datastore
                                                        .build();
                        StreamingIterator<FastqRecord> iter = datastore.iterator();
                ){
                        
                        while( iter.hasNext()){
                            FastqRecord fastq = iter.next();
           
                            Range trimRange = bwaTrimmer.trim(fastq.getQualitySequence());
                            
                            
                            if (trimRange.getLength() < minLength) {
                                continue;
                            }            
                            //this doesn't compile anymore because 5.3 made this class into an interface
                            //but kept here for comparison purposes
                          /*  FastqRecord trimmedFastq = new FastqRecordBuilder(fastq.getId(),
                                                            new NucleotideSequenceBuilder(fastq.getNucleotideSequence())
                                                                    .trim(trimRange)
                                                                    .build(),
                                                            new QualitySequenceBuilder(fastq.getQualitySequence())
                                                                    .trim(trimRange)
                                                                    .build())
                                                            .comment(fastq.getComment())
                                                            .build();
                            
                            writer.write(trimmedFastq);
                            */
                        }
                        
                }//streams and datastores will autoclose when end of scope reached.
    }
    

}
