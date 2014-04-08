/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamRecordFlags;
import org.jcvi.jillion.sam.SamWriter;
import org.jcvi.jillion.sam.SamFileWriterBuilder;
import org.jcvi.jillion.sam.SortOrder;
import org.jcvi.jillion.sam.attribute.InvalidAttributeException;
import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.header.ReadGroup;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamVersion;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;

public class Fastq2Sam {

	public static void main(String[] args) throws IOException, DataStoreException, InvalidAttributeException {
		//File fastqFile = new File("/path/to/fastq");
		File fastqFile = new File("/usr/local/scratch/dkatzel/fastq2sam/entero_RVENT_49161_final.fastq");
		File outputFile = new File("/usr/local/scratch/dkatzel/fastq2sam/entero_RVENT_49161_final.jillion.sorted.bam");
		
		SamHeader header = new SamHeader.Builder()
							.setVersion(new SamVersion(1, 5))
							/*.addComment("created by Jillion")
							.addProgram(new SamProgram.Builder("Fastq2Sam")
											.setVersion("1.0")
											.build())
											*/
							.addReadGroup(new ReadGroup.Builder("A")
												.setSampleOrPoolName("entero,RVENT,49161")
												.build())
							.build();
		
		//File outputFile = new File("/path/to/bam.or.sam");
		SamWriter samWriter = new SamFileWriterBuilder(outputFile, header)
									.reSortBy(SortOrder.QUERY_NAME)
									.setTempRootDir(new File("/usr/local/scratch/dkatzel/fastq2sam/tmpDir"))
									.build();
		
		StreamingIterator<FastqRecord> iter=null;
		FastqDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
											.qualityCodec(FastqQualityCodec.SANGER)
											.hint(DataStoreProviderHint.ITERATION_ONLY)
											.build();
		try{
			iter = datastore.iterator();
			while(iter.hasNext()){
				FastqRecord fastq = iter.next();
				SamRecord samRecord =new SamRecord.Builder(header)
											.setFlags(
													EnumSet.of(SamRecordFlags.READ_UNMAPPED,
															    SamRecordFlags.HAS_MATE_PAIR,
															    SamRecordFlags.FIRST_MATE_OF_PAIR,
															    SamRecordFlags.SECOND_MATE_OF_PAIR,
																SamRecordFlags.MATE_UNMAPPED))
											.setQueryName(fastq.getId())
											.setQualities(fastq.getQualitySequence())
											.setSequence(fastq.getNucleotideSequence())
											.addAttribute(new SamAttribute(ReservedSamAttributeKeys.READ_GROUP, "A"))
											.build();
				
				samWriter.writeRecord(samRecord);
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter, datastore, samWriter);
		}
	}

}
