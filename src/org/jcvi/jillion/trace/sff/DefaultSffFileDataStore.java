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
package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
/**
* {@code DefaultSffFileDataStore} creates {@link SffFileDataStore}
* instances that store all the {@link SffFlowgram}s
* in a Map.  This implementation is not very 
* memory efficient and therefore should not be used
* for large sff files.
* @author dkatzel
*/
class DefaultSffFileDataStore {

	private DefaultSffFileDataStore(){
		//can not instantiate
	}
	/**
	 * Create a new {@link SffFileDataStore} by parsing
	 * the entire given sff file and include all
	 * the reads in the DataStore.
	 * @param sffFile the sff encoded file to parse.
	 * @return a new {@link SffFileDataStore} containing
	 * all the reads in the sff file; never null.
	 * @throws IOException if there is a problem
	 * parsing the file.
	 * @throws NullPointerException if either the sffFile is null.
	 */
	public static SffFileDataStore create(File sffFile) throws IOException{
		return create(sffFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Create a new {@link SffFileDataStore} by parsing
	 * the entire given sff file but include only
	 * the reads that are accepted by the given {@link DataStoreFilter}.
	 * @param sffFile the sff encoded file to parse.
	 * @param filter the {@link DataStoreFilter} to use
	 * to filter out any reads in the sff file; can not be null.
	 * @return a new {@link SffFileDataStore} containing
	 * only the reads accepted by the given filter; never null.
	 * @throws IOException if there is a problem
	 * parsing the file.
	 * @throws NullPointerException if either the sffFile or filter are null.
	 */
	public static SffFileDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter);
		SffFileParser parser = SffFileParser.create(sffFile);
		parser.accept(visitor);
		
		return visitor.builder.build();
	}
	
	
	/**
	 * {@link SffFileVisitor} implementation 
	 * that puts flowgrams into a datastore
	 * as each record is visited.
	 * @author dkatzel
	 *
	 */
	private static final class Visitor implements SffFileVisitor{
		private SffDataStoreBuilder builder;
		
		private final DataStoreFilter filter;
		
		
		public Visitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			builder = new SffDataStoreBuilder(header.getKeySequence(), header.getFlowSequence(), (int)header.getNumberOfReads());
			
		}

		@Override
		public SffFileReadVisitor visitRead(SffFileParserCallback callback,
				final SffReadHeader readHeader) {
			if(filter.accept(readHeader.getId())){
				return new SffFileReadVisitor(){

					@Override
					public void visitReadData(SffReadData readData) {
						 builder.addFlowgram(SffFlowgramImpl.create(readHeader, readData));
						
					}

					@Override
					public void visitEnd() {
						//no-op
						
					}
					
				};
			}
			return null;
		}

		@Override
		public void end() {
			//no-op
			
		}
		
	}
}
