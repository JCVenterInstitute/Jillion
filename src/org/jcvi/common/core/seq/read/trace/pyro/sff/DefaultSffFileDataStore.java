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
import java.util.Collections;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DefaultIncludeDataStoreFilter;
import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;

public final class DefaultSffFileDataStore {

	
	public static SffDataStore create(File sffFile) throws IOException{
		SffFileVisitorDataStoreBuilder builder = createVisitorBuilder();
		SffFileParser.parseSFF(sffFile, builder);
		return builder.build();
	}
	public static SffDataStore createDataStoreOfSingleRead(File sffFile, String readId) throws IOException{
		SffFileVisitorDataStoreBuilder builder = createVisitorBuilder(readId);
		SffFileParser.parseSFF(sffFile, builder);
		return builder.build();
	}
	public static SffDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		SffFileVisitorDataStoreBuilder builder = createVisitorBuilder(filter);
		SffFileParser.parseSFF(sffFile, builder);
		return builder.build();
	}
	public static SffFileVisitorDataStoreBuilder createVisitorBuilder() {
		return new DefaultSffFileVisitorDataStoreBuilder(AcceptingDataStoreFilter.INSTANCE);
	}
	public static SffFileVisitorDataStoreBuilder createVisitorBuilder(String singleReadId) {
		return new DefaultSffFileVisitorDataStoreBuilder(singleReadId);
	}
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
    	private boolean keepParsingFile=true;
    	private final boolean onlyOneReadToParse;
    	private DefaultSffFileVisitorDataStoreBuilder(String singleReadId){
    		this.filter=new DefaultIncludeDataStoreFilter(Collections.singleton(singleReadId));
    		onlyOneReadToParse=true;
    	}
    	private DefaultSffFileVisitorDataStoreBuilder(DataStoreFilter filter){
    		this.filter=filter;
    		onlyOneReadToParse=false;
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
		public synchronized SffDataStore build() {
			checkNotYetInitialized();
			initialized=true;
			return builder.build();
		}

		@Override
		public synchronized boolean visitCommonHeader(SffCommonHeader commonHeader) {
			checkNotYetInitialized();
			return true;
		}

		@Override
		public synchronized boolean visitReadHeader(SffReadHeader readHeader) {
			checkNotYetInitialized();
			this.currentReadHeader = readHeader;
	        boolean accept= filter.accept(readHeader.getId());
	        if(onlyOneReadToParse && accept){
	        	keepParsingFile=false;
	        }
	        return accept;
		}

		@Override
		public boolean visitReadData(SffReadData readData) {
			checkNotYetInitialized();
			 builder.addFlowgram(SffFlowgram.create(currentReadHeader, readData));
		     currentReadHeader=null;
			return keepParsingFile;
		}

		@Override
		public void visitFile() {
			
		}

		@Override
		public void visitEndOfFile() {
			
			
		}
    	
    }
}
