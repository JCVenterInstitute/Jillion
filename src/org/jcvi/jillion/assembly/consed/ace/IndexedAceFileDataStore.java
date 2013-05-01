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
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback.AceFileVisitorMemento;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
/**
 * {@code IndexedAceFileDataStore} is an implementation of 
 * {@link AceFileContigDataStore} that only stores an {@link AceFileVisitorMemento}s
 * to the various contigs contained
 * inside the ace file.  Calls to {@link AceContig#getRead(String)} may
 * cause part of the ace file to be re-parsed in order to retrieve
 * any missing information.  
 * <p/>
 * This allows large files to provide random 
 * access without taking up much memory.  The down side is each contig
 * must be re-parsed each time and the ace file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
final class IndexedAceFileDataStore implements AceFileContigDataStore{
	
	private final Map<String, AceFileVisitorMemento> mementos;
	private final List<WholeAssemblyAceTag> wholeAssemblyTags;
    private final List<ConsensusAceTag> consensusTags;
    private final List<ReadAceTag> readTags;
   
    private final AceHandler parser;
    private final long totalNumberOfReads;
    
    private volatile boolean closed=false;
    
    public static AceFileContigDataStore create(File aceFile, DataStoreFilter filter) throws IOException{
    	if(filter==null){
    		throw new NullPointerException("filter can not be null");
    	}
    	AceHandler parser = AceFileParser.create(aceFile);
    	VisitorBuilder visitorBuilder = new VisitorBuilder(filter);
    	parser.accept(visitorBuilder);
    	return new IndexedAceFileDataStore(visitorBuilder, parser);
    }
    
    private IndexedAceFileDataStore(VisitorBuilder builder, AceHandler parser){    	
    	this.parser = parser;
    	this.mementos = builder.mementos;
    	this.wholeAssemblyTags = builder.wholeAssemblyTags;
    	this.consensusTags = builder.consensusTags;
    	this.readTags = builder.readTags;
    	this.totalNumberOfReads = builder.totalNumberOfReads;
    }
	
    
    private void checkNotYetClosed(){
    	if(closed){
    		throw new DataStoreClosedException("closed");
    	}
    }
    
    
	@Override
	public void close() throws IOException {
		closed=true;		
	}
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		checkNotYetClosed();
		return DataStoreStreamingIterator.create(this, mementos.keySet().iterator());
	}

	@Override
	public AceContig get(String id) throws DataStoreException {
		//contains does the check not closed so we don't have to
		if(!contains(id)){
			return null;
		}
		AceFileVisitorMemento memento = mementos.get(id);
		SingleAceFileVisitor singleAceFileVisitor = new SingleAceFileVisitor();
		try{
			parser.accept(singleAceFileVisitor, memento);
			return singleAceFileVisitor.getContig();
		}catch(IOException e){
			throw new DataStoreException("error re-parsing contig "+ id ,e);
		}
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		checkNotYetClosed();
		return mementos.containsKey(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		checkNotYetClosed();
		return mementos.size();
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public StreamingIterator<AceContig> iterator() throws DataStoreException {
		checkNotYetClosed();
		//TODO should we just use a large datastore iterator?
		return new DataStoreIterator<AceContig>(this);
	}

	@Override
	public long getNumberOfTotalReads() throws DataStoreException {
		checkNotYetClosed();
		return totalNumberOfReads;
	}

	@Override
	public StreamingIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator()
			throws DataStoreException {
		checkNotYetClosed();
		return DataStoreStreamingIterator.create(this, wholeAssemblyTags.iterator());
	}

	@Override
	public StreamingIterator<ReadAceTag> getReadTagIterator()
			throws DataStoreException {
		checkNotYetClosed();
		return DataStoreStreamingIterator.create(this, readTags.iterator());
	}

	@Override
	public StreamingIterator<ConsensusAceTag> getConsensusTagIterator()
			throws DataStoreException {
		checkNotYetClosed();
		return DataStoreStreamingIterator.create(this, consensusTags.iterator());
	}






	private static class VisitorBuilder implements AceFileVisitor{
		private final List<WholeAssemblyAceTag> wholeAssemblyTags = new ArrayList<WholeAssemblyAceTag>();
        private final List<ConsensusAceTag> consensusTags = new ArrayList<ConsensusAceTag>();
        private final List<ReadAceTag> readTags = new ArrayList<ReadAceTag>();
        
        private final DataStoreFilter filter;

        private long totalNumberOfReads;
        
        Map<String, AceFileVisitorMemento> mementos;
        
		public VisitorBuilder(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
			int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfContigs);
			mementos = new LinkedHashMap<String, AceFileVisitorCallback.AceFileVisitorMemento>(capacity);
		}

		@Override
		public AceContigVisitor visitContig(AceFileVisitorCallback callback,
				String contigId, int numberOfBases, int numberOfReads,
				int numberOfBaseSegments, boolean reverseComplemented) {
			if(filter.accept(contigId)){
				mementos.put(contigId, callback.createMemento());
				totalNumberOfReads +=numberOfReads;
			}
			//always skip
			return null;
		}

		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			 readTags.add(new ReadAceTag(id, type, creator, creationDate, 
	                    Range.of(gappedStart,gappedEnd), isTransient));			
		}

		@Override
		public AceConsensusTagVisitor visitConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			//only visit tags for contigs we care about
			if(filter.accept(id)){
				return new AbstractAceConsensusTagVisitor(id, type, creator, 
									gappedStart, gappedEnd, 
									creationDate, isTransient) {
					
					@Override
					protected void visitConsensusTag(ConsensusAceTag consensusTag) {
						consensusTags.add(consensusTag);						
					}
				};
			}
			//if we don't care about contig then skip it
			return null;
		}

		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			 wholeAssemblyTags.add(new WholeAssemblyAceTag(type, creator, creationDate, data.trim()));
	    }

		@Override
		public void visitEnd() {
			//no-op			
		}

		@Override
		public void halted() {
			//no-op		
		}        
        
	}
	
	public static final class SingleAceFileVisitor implements AceFileVisitor {

		private AceContig contig;
		
		public final AceContig getContig() {
			return contig;
		}

		@Override
		public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
			//no-op
		}

		@Override
		public AceContigVisitor visitContig(final AceFileVisitorCallback callback,
				String contigId, int numberOfBases, int numberOfReads,
				int numberOfBaseSegments, boolean reverseComplemented) {
			//assume first contig we see is the one we want
			return new AbstractAceContigBuilderVisitor(contigId, numberOfBases, numberOfReads) {
				
				@Override
				protected void visitContig(AceContigBuilder builder) {
					contig = builder.build();
					callback.haltParsing();
				}
			};
		}

		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			//no-op
		}

		@Override
		public AceConsensusTagVisitor visitConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			//always skip
			return null;
		}

		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			//no-op
		}

		@Override
		public void visitEnd() {
			//no-op
		}

		@Override
		public void halted() {
			//no-op
		}

	}
}
