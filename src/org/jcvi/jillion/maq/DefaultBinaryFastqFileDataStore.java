/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.maq;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.io.IOUtil.Endian;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.internal.trace.fastq.DefaultFastqDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.AbstractFastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor;
/**
 * {@code DefaultBinaryFastqFileDataStore} is the default implementation
 * of {@link FastqDataStore} which stores
 * all {@link FastqRecord}s from a file in memory.  This is only recommended for small fastq
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeBinaryFastqFileDataStore
 *
 */
final class DefaultBinaryFastqFileDataStore{
	
	private DefaultBinaryFastqFileDataStore(){
		//can not instantiate
	}



	/**
	 * Create a new {@link FastqDataStore} instance for the {@link FastqRecord}s
	 * that are accepted by the given {@link DataStoreFilter} that are contained in
	 * the given binary fastq file. Any records that are not accepted by the filter
	 * will not be included in the returned {@link FastqDataStore}.
	 * 
	 * @param bfq
	 *            the binary fastq file to parse, must exist and can not be null.
	 * @param filter
	 *            an instance of {@link DataStoreFilter} that can be used to filter
	 *            out some {@link FastqRecord}s from the datastore.
	 * @return a new {@link FastqDataStore} instance containing only those
	 *         records that pass the filter.
	 * @throws IOException
	 *             if thre is a problem parsing the fastq file.
	 * @throws NullPointerException
	 *             if either fastqFile or filter is null.
	 */
   public static FastqDataStore create(File bfq, DataStoreFilter filter, Endian endian) throws IOException{
	   DefaultFastqFileDataStoreBuilderVisitor2 visitor = new DefaultFastqFileDataStoreBuilderVisitor2(filter);
	   BinaryFastqFileParser.create(bfq, endian).parse(visitor);

	   return visitor.build();
   }
    
	private static final class DefaultFastqFileDataStoreBuilderVisitor2 implements FastqVisitor, Builder<FastqDataStore> {
		private final DataStoreFilter filter;
		private final DefaultFastqDataStoreBuilder builder =new DefaultFastqDataStoreBuilder();

		public DefaultFastqFileDataStoreBuilderVisitor2( DataStoreFilter filter) {
			
			if(filter==null){
				throw new NullPointerException("filter can not be null");
			}
			this.filter = filter;
		}

		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
				String id, String optionalComment) {
			if(!filter.accept(id)){
				return null;
			}
			//we don't use the quality codec but we need to give it something...
			return new AbstractFastqRecordVisitor(id,optionalComment, FastqQualityCodec.ILLUMINA) {
				
				@Override
				protected void visitRecord(FastqRecord record) {
					builder.put(record);					
				}
			};
		}

		@Override
		public void visitEnd() {
			//no-op
		}
		@Override
		public void halted() {
			//no-op			
		}
		@Override
		public FastqDataStore build() {
			return builder.build();
		}

	}
}
