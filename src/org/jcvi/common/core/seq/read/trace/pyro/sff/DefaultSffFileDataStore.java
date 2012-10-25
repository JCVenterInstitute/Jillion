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
/*
 * Created on Nov 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStore;
/**
 {@code DefaultSffFileDataStore} creates {@link FlowgramDataStore}
 * instances that store all the {@link Flowgram}s
 * in a Map.  This implementation is not very 
 * memory efficient and therefore should not be used
 * for large sff files.
 * @author dkatzel
 */
public final class DefaultSffFileDataStore {

	private DefaultSffFileDataStore(){
		//can not instantiate
	}
	/**
	 * Create a new {@link FlowgramDataStore} by parsing
	 * the entire given sff file and include all
	 * the reads in the DataStore.
	 * @param sffFile the sff encoded file to parse.
	 * @return a new {@link FlowgramDataStore} containing
	 * all the reads in the sff file; never null.
	 * @throws IOException if there is a problem
	 * parsing the file.
	 * @throws NullPointerException if either the sffFile is null.
	 */
	public static FlowgramDataStore create(File sffFile) throws IOException{
		SffFileVisitorDataStoreBuilder builder = createVisitorBuilder();
		SffFileParser.parse(sffFile, builder);
		return builder.build();
	}
	/**
	 * Create a new {@link FlowgramDataStore} by parsing
	 * the entire given sff file but include only
	 * the reads that are accepted by the given {@link DataStoreFilter}.
	 * @param sffFile the sff encoded file to parse.
	 * @param filter the {@link DataStoreFilter} to use
	 * to filter out any reads in the sff file; can not be null.
	 * @return a new {@link FlowgramDataStore} containing
	 * only the reads accepted by the given filter; never null.
	 * @throws IOException if there is a problem
	 * parsing the file.
	 * @throws NullPointerException if either the sffFile or filter are null.
	 */
	public static FlowgramDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		SffFileVisitorDataStoreBuilder builder = createVisitorBuilder(filter);
		SffFileParser.parse(sffFile, builder);
		return builder.build();
	}
	/**
	 * Create a new {@link SffFileVisitorDataStoreBuilder}
	 * that needs to be populated and built.
	 * @return a new instance of {@link SffFileVisitorDataStoreBuilder};
	 * never null.
	 */
	public static SffFileVisitorDataStoreBuilder createVisitorBuilder() {
		return new DefaultSffFileVisitorDataStoreBuilder(DataStoreFilters.alwaysAccept());
	}
	/**
	 * Create a new {@link SffFileVisitorDataStoreBuilder}
	 * with the given {@link DataStoreFilter}
	 * that needs to be populated and built.
	 * Any {@link Flowgram}s added to this builder that are 
	 * not accepted by the filter will be ignored
	 * and not included in the {@link FlowgramDataStore}
	 * when it is built via {@link SffFileVisitorDataStoreBuilder#build()}.
	 * @param filter the {@link DataStoreFilter} to use
	 * to filter out any reads in the sff file; can not be null.
	 * @return a new instance of {@link SffFileVisitorDataStoreBuilder};
	 * never null.
	 * @throws NullPointerException if filter is null.
	 */
	public static SffFileVisitorDataStoreBuilder createVisitorBuilder(DataStoreFilter filter) {
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		return new DefaultSffFileVisitorDataStoreBuilder(filter);
	}
	
 
    private static final class DefaultSffFileVisitorDataStoreBuilder implements SffFileVisitorDataStoreBuilder{

    	private boolean initialized=false;
    	private final DefaultSffDataStoreBuilder builder = new DefaultSffDataStoreBuilder();
    	private SffReadHeader currentReadHeader;
    	private final DataStoreFilter filter;
    	
    	private DefaultSffFileVisitorDataStoreBuilder(DataStoreFilter filter){
    		this.filter=filter;
    	}
    	private synchronized void checkNotYetInitialized(){
    		if(initialized){
    			throw new IllegalStateException("datastore already initialized");
    		}
    	}
		@Override
		public SffFileVisitorDataStoreBuilder addFlowgram(Flowgram flowgram) {
			checkNotYetInitialized();
			builder.addFlowgram(flowgram);
			return this;
		}

		@Override
		public synchronized FlowgramDataStore build() {
			checkNotYetInitialized();
			initialized=true;
			return builder.build();
		}

		@Override
		public synchronized CommonHeaderReturnCode visitCommonHeader(SffCommonHeader commonHeader) {
			checkNotYetInitialized();
			return CommonHeaderReturnCode.PARSE_READS;
		}

		@Override
		public synchronized ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
			checkNotYetInitialized();
			this.currentReadHeader = readHeader;
	        boolean accept= filter.accept(readHeader.getId());
	        return accept?ReadHeaderReturnCode.PARSE_READ_DATA:ReadHeaderReturnCode.SKIP_CURRENT_READ;
		}

		@Override
		public ReadDataReturnCode visitReadData(SffReadData readData) {
			checkNotYetInitialized();
			 builder.addFlowgram(SffFlowgram.create(currentReadHeader, readData));
		     currentReadHeader=null;
			return ReadDataReturnCode.PARSE_NEXT_READ;
		}

		@Override
		public void visitFile() {
			//no-op
		}

		@Override
		public void visitEndOfFile() {
			//no-op			
		}
    	
    }
}
