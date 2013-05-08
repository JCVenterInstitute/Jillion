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
package org.jcvi.jillion.fasta.pos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;

public final class DefaultPositionFastaFileDataStore {
	
	private DefaultPositionFastaFileDataStore(){
		//can not instantiate
	}
	public static PositionFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		DefaultQualityFastaFileDataStoreBuilder builder = new DefaultQualityFastaFileDataStoreBuilder(filter);
		FastaFileParser.create(fastaFile).accept(builder);
    	return builder.build();
	}
	public static PositionFastaDataStore create(InputStream positionFastaInputStream, DataStoreFilter filter) throws IOException{
		DefaultQualityFastaFileDataStoreBuilder builder = new DefaultQualityFastaFileDataStoreBuilder(filter);
		FastaFileParser.create(positionFastaInputStream).accept(builder);
    	return builder.build();
	}
	public static PositionFastaDataStore create(File positionFastaFile) throws IOException{
		return create(positionFastaFile, DataStoreFilters.alwaysAccept());
	}
	public static PositionFastaDataStore create(InputStream positionFastaInputStream) throws IOException{
		return create(positionFastaInputStream, DataStoreFilters.alwaysAccept());
	}

	
	private static class DefaultQualityFastaFileDataStoreBuilder implements FastaVisitor, Builder<PositionFastaDataStore>{

		private final Map<String, PositionFastaRecord> fastaRecords = new LinkedHashMap<String, PositionFastaRecord>();
		
		private final DataStoreFilter filter;
		
		public DefaultQualityFastaFileDataStoreBuilder(DataStoreFilter filter){
			this.filter = filter;
		}
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				final String id, String optionalComment) {
			if(!filter.accept(id)){
				return null;
			}
			return new AbstractPositionSequenceFastaRecordVisitor(id,optionalComment){

				@Override
				protected void visitRecord(
						PositionFastaRecord fastaRecord) {
					fastaRecords.put(id, fastaRecord);
					
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
		public PositionFastaDataStore build() {
			return DataStoreUtil.adapt(PositionFastaDataStore.class,fastaRecords);
		}
		
	}
}
