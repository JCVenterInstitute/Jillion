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
package org.jcvi.jillion.examples.sam;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamRecordFlags;
import org.jcvi.jillion.sam.SamWriter;
import org.jcvi.jillion.sam.SamFileWriterBuilder;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.attribute.InvalidAttributeException;
import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
import org.jcvi.jillion.sam.header.SamProgramBuilder;
import org.jcvi.jillion.sam.header.SamReadGroupBuilder;
import org.jcvi.jillion.sam.header.SamVersion;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;

public class Fastq2Sam {

	public static void main(String[] args) throws IOException, DataStoreException, InvalidAttributeException {
		File fastqFile = new File("/path/to/fastq");
		File outputFile = new File("/path/to/output/out.bam");
		
		SamHeader header = new SamHeaderBuilder()
							.setVersion(new SamVersion(1, 5))
							.addComment("created by Jillion")
							.addProgram(new SamProgramBuilder("Fastq2Sam")
											.setVersion("1.0")
											.build())
											
							.addReadGroup(new SamReadGroupBuilder("A")
										.setSampleOrPoolName("mySample_Name")
										.build())
							.build();
		
		try(    SamWriter samWriter = new SamFileWriterBuilder(outputFile, header)
								.reSortBy(SortOrder.QUERY_NAME)
								//if tempdir not set, then system tmp is used
								.setTempRootDir(new File("/my/alternate/tmpDir"))
								.build();
		
		       
		        FastqDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
								.qualityCodec(FastqQualityCodec.SANGER)
								.hint(DataStoreProviderHint.ITERATION_ONLY)
								.build();
		        
		        StreamingIterator<FastqRecord> iter=datastore.iterator();
		){
			while(iter.hasNext()){
                            FastqRecord fastq = iter.next();
                            SamRecord samRecord = new SamRecord.Builder(header)
                                    //mark everything as unmapped initially
                                    //but you can use other SamRecordFlags as well to say it mapped
                                    //or to provide info about its mate etc
                                    .setFlags(
                                            EnumSet.of(SamRecordFlags.READ_UNMAPPED))
                                    .setQueryName(fastq.getId())
                                    .setQualities(fastq.getQualitySequence())
                                    .setSequence(fastq.getNucleotideSequence())
                                    .addAttribute(
                                            new SamAttribute(
                                                    ReservedSamAttributeKeys.READ_GROUP, "A"))
                                    .build();

				samWriter.writeRecord(samRecord);
			}
		}
	}

}
