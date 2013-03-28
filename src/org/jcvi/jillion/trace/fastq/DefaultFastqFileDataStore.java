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
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code DefaultFastqFileDataStore} is the default implementation
 * of {@link FastqDataStore} which stores
 * all {@link FastqRecord}s from a file in memory.  This is only recommended for small fastq
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeFastqFileDataStore
 *
 */
final class DefaultFastqFileDataStore{
	
	private DefaultFastqFileDataStore(){
		//can not instantiate
	}


	/**
	 * Create a new {@link FastqDataStore} instance for all the
	 * {@link FastqRecord}s that are contained in the given fastq file. All
	 * records in the file must have their qualities encoded in a manner that
	 * can be parsed by the given {@link FastqQualityCodec} (if provided).
	 * 
	 * @param fastqFile
	 *            the fastq file to parse, must exist and can not be null.
	 * @param qualityCodec
	 *            an optional {@link FastqQualityCodec} that should be used to
	 *            decode the fastq file. If this value is null, then the
	 *            datastore implementation will try to guess the codec used
	 *            which might have a performance penalty associated with it.
	 * @return a new {@link FastqDataStore} instance.
	 * @throws IOException
	 *             if there is a problem parsing the fastq file.
	 * @throws NullPointerException
	 *             if fastqFile is null.
	 */
   public static FastqDataStore create(File fastqFile, FastqQualityCodec qualityCodec) throws IOException{
	  return create(fastqFile, DataStoreFilters.alwaysAccept(), qualityCodec);
   }

	/**
	 * Create a new {@link FastqDataStore} instance for the {@link FastqRecord}s
	 * that are accepted by the given {@link DataStoreFilter} that are contained in
	 * the given fastq file. Any records that are not accepted by the filter
	 * will not be included in the returned {@link FastqDataStore}. All of those
	 * records must have their qualities encoded a manner that can be parsed by
	 * the given {@link FastqQualityCodec} (if provided).
	 * 
	 * @param fastqFile
	 *            the fastq file to parse, must exist and can not be null.
	 * @param filter
	 *            an instance of {@link DataStoreFilter} that can be used to filter
	 *            out some {@link FastqRecord}s from the datastore.
	 * @param qualityCodec
	 *            the {@link FastqQualityCodec} needed to parse the encoded
	 *            quality values in each record. If this value is null, then the
	 *            datastore implementation will try to guess the codec used
	 *            which might have a performance penalty associated with it.
	 * @return a new {@link FastqDataStore} instance containing only those
	 *         records that pass the filter.
	 * @throws IOException
	 *             if thre is a problem parsing the fastq file.
	 * @throws NullPointerException
	 *             if either fastqFile or filter is null.
	 */
   public static FastqDataStore create(File fastqFile, DataStoreFilter filter,FastqQualityCodec qualityCodec) throws IOException{
	   DefaultFastqFileDataStoreBuilderVisitor2 visitor = new DefaultFastqFileDataStoreBuilderVisitor2(qualityCodec,filter);
	   FastqFileParser.create(fastqFile).accept(visitor);

	   return visitor.build();
   }
    
	private static final class DefaultFastqFileDataStoreBuilderVisitor2 implements FastqVisitor, Builder<FastqDataStore> {
		private final DataStoreFilter filter;
		private final FastqQualityCodec qualityCodec;
		private final DefaultFastqDataStoreBuilder builder =new DefaultFastqDataStoreBuilder();

		public DefaultFastqFileDataStoreBuilderVisitor2(
				FastqQualityCodec qualityCodec, DataStoreFilter filter) {
			if(qualityCodec==null){
				throw new NullPointerException("quality codec can not be null");
			}
			if(filter==null){
				throw new NullPointerException("filter can not be null");
			}
			this.qualityCodec = qualityCodec;
			this.filter = filter;
		}

		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
				String id, String optionalComment) {
			if(!filter.accept(id)){
				return null;
			}
			return new AbstractFastqRecordVisitor(id,optionalComment, qualityCodec) {
				
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
