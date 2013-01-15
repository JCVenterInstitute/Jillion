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
 * Created on May 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.assembly.ace.consed.ConsedUtil;
import org.jcvi.jillion.assembly.ace.consed.ConsedUtil.ClipPointsType;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.internal.io.ByteBufferInputStream;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code IndexedAceFileDataStore} is an implementation of 
 * {@link AceFileContigDataStore} that only stores an index containing
 * byte offsets to the various contigs contained
 * inside the ace file. Furthermore, each {@link AceContig}
 * in this datastore will not store all the underlying read
 * data in memory either.  Calls to {@link AceContig#getRead(String)} may
 * cause part of the ace file to be re-parsed in order to retrieve
 * any missing information.  
 * <p/>
 * This allows large files to provide random 
 * access without taking up much memory.  The down side is each contig
 * must be re-parsed each time and the ace file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
final class IndexedAceFileDataStore{
	
	private static boolean allowMemoryMapping=true;
	/**
	 * For testing only. Turn on/off
	 * ability to return memory mapped instances.
	 * @param allowMemoryMapping
	 */
	static synchronized void allowMemoryMapping(boolean allowMemoryMapping ){
		IndexedAceFileDataStore.allowMemoryMapping = allowMemoryMapping;
	}
	
	static synchronized boolean allowMemoryMapping(){
		return allowMemoryMapping;
	}
    /**
     * Create a new empty {@link AceContigDataStoreBuilder}
     * that will create an {@link IndexedAceFileDataStore} 
     * once the builder has been built.  Only the 
     * given ace file should be used with to populate/index
     * the returned builder.
     * @param aceFile aceFile the aceFile to parse.  NOTE: 
     * the file isn't actually parsed in this method.  The builder
     * will only store a reference to this file for future
     * use when it needs to re-parse after indexing has occurred.
     * @return a new AceContigDataStoreBuilder, never null.
     * throws NullPointerException if aceFile is null.
     */
    public static AceContigDataStoreBuilder createBuilder(File aceFile){
        return new IndexedAceFileDataStoreBuilder(aceFile);
    }
    
    /**
     * Create a new empty {@link AceContigDataStoreBuilder}
     * that will create an {@link IndexedAceFileDataStore} 
     * once the builder has been built.  Only the 
     * given ace file should be used with to populate/index
     * the returned builder.
     * @param aceFile aceFile the aceFile to parse.  NOTE: 
     * the file isn't actually parsed in this method.  The builder
     * will only store a reference to this file for future
     * use when it needs to re-parse after indexing has occurred.
     * @param filter a {@link DataStoreFilter} that can be used
     * to include/exclude certain contigs can not be null. 
     * @return a new AceContigDataStoreBuilder, never null.
     * throws NullPointerException if aceFile is null.
     */
    public static AceContigDataStoreBuilder createBuilder(File aceFile, DataStoreFilter filter){
        return new IndexedAceFileDataStoreBuilder(aceFile, filter);
    }

    /**
     * Create a new {@link AceFileContigDataStore} instance
     * for the contigs in the given aceFile.
     * @param aceFile the aceFile to parse.
     * @return a new  a new {@link AceFileContigDataStore}
     * that only stores an index containing file offsets to the various contigs contained
     * inside the ace file. 
     * @throws IOException if there is a problem reading the ace file
     * @throws NullPointerException if aceFile is null.
     */
    public static AceFileContigDataStore create(File aceFile) throws IOException{
        AceContigDataStoreBuilder builder = createBuilder(aceFile);
        AceFileParser.parse(aceFile, builder);
        return builder.build();
    }
    
    /**
     * Create a new {@link AceFileContigDataStore} instance
     * for the contigs in the given aceFile.
     * @param aceFile the aceFile to parse.
     * @param filter a {@link DataStoreFilter} that can be used
     * to include/exclude certain contigs can not be null.
     * @return a new  a new {@link AceFileContigDataStore}
     * that only stores an index containing file offsets to the various contigs contained
     * inside the ace file. 
     * @throws IOException if there is a problem reading the ace file
     * @throws NullPointerException if aceFile is null.
     */
    public static AceFileContigDataStore create(File aceFile, DataStoreFilter filter) throws IOException{
        AceContigDataStoreBuilder builder = createBuilder(aceFile,filter);
        AceFileParser.parse(aceFile, builder);
        return builder.build();
    }
    
   
    
   
    /**
     * {@code IndexedAceFileDataStoreBuilder} is a {@link AceContigDataStoreBuilder}
     * that will keep track of offsets into the ace file
     * where each contig begins and ends.  This allows us to only store
     * a few numbers which can be used later to only read the
     * section of the ace file with the contig we want.
     * @author dkatzel
     *
     *
     */
    private static class IndexedAceFileDataStoreBuilder implements AceContigDataStoreBuilder{
        private Map<String, Range> indexFileRange;
        private final File aceFile;
        private long currentStartOffset;
        private int currentLineLength;
        private long currentFileOffset;
        private String currentContigId;
        private long totalNumberOfReads=0L;
        private final List<WholeAssemblyAceTag> wholeAssemblyTags = new ArrayList<WholeAssemblyAceTag>();
        private final List<ConsensusAceTag> consensusTags = new ArrayList<ConsensusAceTag>();
        private final List<ReadAceTag> readTags = new ArrayList<ReadAceTag>();
        
        private final DataStoreFilter filter;
       
        
        /**
         * Consensus tags span multiple lines of the ace file so we need to build
         * up the consensus tags as we parse.
         */
        private ConsensusAceTagBuilder consensusTagBuilder;
        
        public IndexedAceFileDataStoreBuilder(File aceFile){
           this(aceFile, DataStoreFilters.alwaysAccept());
        }
        
        public IndexedAceFileDataStoreBuilder(File aceFile, DataStoreFilter filter){
            if(aceFile==null){
                throw new NullPointerException("ace file cannot be null");
            } 
            if(filter==null){
                throw new NullPointerException("DataStoreFilter cannot be null");
            }
            this.aceFile = aceFile;
            this.filter =filter;
        }
        
        @Override
        public synchronized void visitLine(String line) {        
            final int length = line.length();
            currentLineLength = length;
            currentFileOffset+=length;            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized AceFileContigDataStore build() {
        	if(allowMemoryMapping && aceFile.length() <=Integer.MAX_VALUE){
        		try {
					return new MemoryMappedIndexedAceFileDataStore(aceFile, indexFileRange,
							totalNumberOfReads,
							wholeAssemblyTags,consensusTags,readTags);
				} catch (IOException e) {
					throw new IllegalStateException("error creating contig datastore",e);
				}
        	}
            return new LargeIndexedAceFileDataStore(aceFile, indexFileRange,
            		totalNumberOfReads,
            		wholeAssemblyTags,consensusTags,readTags);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitFile() {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitEndOfFile() {
            //no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitHeader(int numberOfContigs, int totalNumberOfReads) {
            indexFileRange = new LinkedHashMap<String, Range>(MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfContigs));
            
        }

     
        /**
         * {@inheritDoc}
         */
        @Override
		public synchronized BeginContigReturnCode visitBeginContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
        	currentContigId = contigId;
            currentStartOffset=currentFileOffset-currentLineLength;
            
            if(filter.accept(contigId)){
				totalNumberOfReads +=numberOfReads;
				return BeginContigReturnCode.VISIT_CURRENT_CONTIG;
            }else{
            	return BeginContigReturnCode.SKIP_CURRENT_CONTIG;
            }
		}
		/**
        * {@inheritDoc}
        */
        @Override
        public void visitConsensusQualities(QualitySequence ungappedConsensusQualities) {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitAlignedReadInfo(String readId, Direction dir,
                int gappedStartOffset) {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBaseSegment(Range gappedConsensusRange, String readId) {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public BeginReadReturnCode visitBeginRead(String readId, int gappedLength) {
        	return BeginReadReturnCode.VISIT_CURRENT_READ;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitQualityLine(int qualLeft, int qualRight,
                int alignLeft, int alignRight) {
        	 ClipPointsType clipPointsType = ConsedUtil.ClipPointsType.getType(qualLeft, qualRight, alignLeft, alignRight);
     		if(clipPointsType !=ClipPointsType.VALID){
     			//ignore read
     			totalNumberOfReads--;
     		}
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitTraceDescriptionLine(String traceName, String phdName,
                Date date) {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBasesLine(String bases) {
        	//no-op
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadTag(String id, String type, String creator,
                long gappedStart, long gappedEnd, Date creationDate,
                boolean isTransient) {
            readTags.add(new ReadAceTag(id, type, creator, creationDate, 
                    Range.of(gappedStart,gappedEnd), isTransient));
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized EndContigReturnCode visitEndOfContig() {    
        	if(filter.accept(currentContigId)){
        		indexFileRange.put(currentContigId, Range.of(currentStartOffset, 
        														currentFileOffset-1));
        	}
            return EndContigReturnCode.KEEP_PARSING;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBeginConsensusTag(String id, String type,
                String creator, long gappedStart, long gappedEnd,
                Date creationDate, boolean isTransient) {
            consensusTagBuilder = new ConsensusAceTagBuilder(id, 
                    type, creator, creationDate, Range.of(gappedStart, gappedEnd), isTransient);

        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitConsensusTagComment(String comment) {
            consensusTagBuilder.addComment(comment);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitConsensusTagData(String data) {
            consensusTagBuilder.appendData(data);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndConsensusTag() {
            consensusTags.add(consensusTagBuilder.build());
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitWholeAssemblyTag(String type, String creator,
                Date creationDate, String data) {
            wholeAssemblyTags.add(new WholeAssemblyAceTag(type, creator, creationDate, data.trim()));
            
        }
    }
    
    
    
    public static final class ReadVisitorBuilder extends AbstractAceFileVisitor implements Builder<AceAssembledRead>{

    	private final NucleotideSequence consensus;
    	private AceAssembledRead builtRead;
		public ReadVisitorBuilder(NucleotideSequence consensus) {
			this.consensus = consensus;
		}

		@Override
		public AceAssembledRead build() {
			return builtRead;
		}

		@Override
		protected void visitNewContig(String contigId,
				NucleotideSequence consensus, int numberOfBases,
				int numberOfReads, boolean isComplimented) {
			//no-op
			
		}

		@Override
		protected void visitAceRead(String readId,
				NucleotideSequence validBasecalls, int offset, Direction dir,
				Range validRange, PhdInfo phdInfo, int ungappedFullLength) {
			builtRead= DefaultAceAssembledRead.createBuilder(consensus, readId, validBasecalls, offset, dir, validRange, phdInfo, ungappedFullLength)
					.build();
			
		}

		@Override
		public BeginReadReturnCode visitBeginRead(String readId, int gappedLength) {
			super.visitBeginRead(readId, gappedLength);
			return builtRead==null?BeginReadReturnCode.VISIT_CURRENT_READ : BeginReadReturnCode.STOP_PARSING;
		}
		
		
	}

    
   
   
	
	static abstract class AbstractIndexedAceFileDataStoreImpl implements AceFileContigDataStore{
		  private final Map<String, Range> indexFileRange;
		    private final File file;
		    private final long totalNumberOfReads;
		    private final List<WholeAssemblyAceTag> wholeAssemblyTags;
		    private final List<ConsensusAceTag> consensusTags;
		    private final List<ReadAceTag> readTags;
		    
		    private volatile boolean  closed=false;
		
		protected AbstractIndexedAceFileDataStoreImpl(File file, Map<String, Range> indexFileRange,
	    		long totalNumberOfReads,
				List<WholeAssemblyAceTag> wholeAssemblyTags,
				List<ConsensusAceTag> consensusTags, List<ReadAceTag> readTags) {
	    	this.indexFileRange = indexFileRange;
	    	this.file = file;
			this.totalNumberOfReads = totalNumberOfReads;
			this.wholeAssemblyTags = wholeAssemblyTags;
			this.consensusTags = consensusTags;
			this.readTags = readTags;
	    }

	    private void assertNotYetClosed() throws DataStoreException{
	    	if(closed){
	    		throw new DataStoreException("datastore is closed");
	    	}
	    }
	    
	    @Override
	   	public long getNumberOfTotalReads() throws DataStoreException {
	    	assertNotYetClosed();
	   		return totalNumberOfReads;
	   	}
	   	@Override
	   	public StreamingIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator() throws DataStoreException {
	   		assertNotYetClosed();
	   		return IteratorUtil.createStreamingIterator(wholeAssemblyTags.iterator());
	   	}
	   	@Override
	   	public StreamingIterator<ReadAceTag> getReadTagIterator() throws DataStoreException {
	   		assertNotYetClosed();
	   		return IteratorUtil.createStreamingIterator(readTags.iterator());
	   	}
	   	@Override
	   	public StreamingIterator<ConsensusAceTag> getConsensusTagIterator() throws DataStoreException {
	   		assertNotYetClosed();
	   		return IteratorUtil.createStreamingIterator(consensusTags.iterator());
	   	}
	    
	    @Override
	    public boolean contains(String contigId) throws DataStoreException {
	    	assertNotYetClosed();
	        return indexFileRange.containsKey(contigId);
	    }

	    @Override
	    public AceContig get(String contigId) throws DataStoreException {
	    	assertNotYetClosed();
	        Range range = indexFileRange.get(contigId);
	        return get(contigId, range);
	    }
	    
	    protected final File getFile() {
			return file;
		}

		protected abstract AceContig get(String contigId, Range fileRange) throws DataStoreException;

	    @Override
	    public StreamingIterator<String> idIterator() {
	        return DataStoreStreamingIterator.create(this, indexFileRange.keySet().iterator());
	    }

	    @Override
	    public long getNumberOfRecords() throws DataStoreException {
	    	assertNotYetClosed();
	        return indexFileRange.size();
	    }

	    @Override
	    public void close() throws IOException {
	        closed=true;
	        
	    }


	    @Override
	    public StreamingIterator<AceContig> iterator() {
	    	StreamingIterator<AceContig> iter= new IndexedContigIterator(idIterator());
	    	return DataStoreStreamingIterator.create(this,iter);
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public boolean isClosed() {
	        return closed;
	    }
	    
	    private final class IndexedContigIterator implements StreamingIterator<AceContig>{

	    	private final StreamingIterator<String> idIterator;
	    	
			public IndexedContigIterator(StreamingIterator<String> idIterator) {
				this.idIterator = idIterator;
			}

			@Override
			public boolean hasNext() {
				return idIterator.hasNext();
			}

			@Override
			public void close() throws IOException {
				idIterator.close();
				
			}

			@Override
			public AceContig next() {
				String id = idIterator.next();
				try {
					return get(id);
				} catch (Throwable t) {
					IOUtil.closeAndIgnoreErrors(idIterator);
					throw new IllegalStateException("error getting contig"+ id,t);
				}
			}

			@Override
			public void remove() {
				idIterator.remove();
				
			}

	    }

		Range getIndexRangeFor(String id) {
			return indexFileRange.get(id);
		}
	}
	
	private static class MemoryMappedIndexedAceFileDataStore extends AbstractIndexedAceFileDataStoreImpl{
		private  MappedByteBuffer buffer;
		protected MemoryMappedIndexedAceFileDataStore(File file,
				Map<String, Range> indexFileRange, long totalNumberOfReads,
				List<WholeAssemblyAceTag> wholeAssemblyTags,
				List<ConsensusAceTag> consensusTags, List<ReadAceTag> readTags) throws IOException {
			super(file, indexFileRange, totalNumberOfReads, wholeAssemblyTags,
					consensusTags, readTags);
			FileChannel channel = new RandomAccessFile(file,"r").getChannel();
			try{
				buffer = channel.map(MapMode.READ_ONLY, 0, (int)file.length());
			}finally{
				channel.close();
			}
			
		}

		@Override
		protected synchronized AceContig get(String contigId,Range fileRange) throws DataStoreException {
			//change the position and limit 
			//so our byte buffer inputstream only reads what we want
			ByteBuffer copyOfBuffer = buffer.duplicate();
			copyOfBuffer.position((int)fileRange.getBegin());
			copyOfBuffer.limit((int)fileRange.getEnd());
			SingleAceContigBuilderVisitor builder = new SingleAceContigBuilderVisitor();
			InputStream in = new ByteBufferInputStream(copyOfBuffer);
			try {
				AceFileParser.parse(in, builder);
			} catch (IOException e) {
				 throw new DataStoreException("error trying to get contig "+ contigId,e);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);				
			}
            return builder.getContig();
		}

		@Override
		public void close() throws IOException {
			super.close();
			//not sure if I need to null out
			//the memory mapped buffer but
			//this will make eligible to get garbage collected
			//which I think is the only way to free the 
			//virtual memory
			buffer=null;
		}
		
		
	}
	
	private static class LargeIndexedAceFileDataStore extends AbstractIndexedAceFileDataStoreImpl{

		protected LargeIndexedAceFileDataStore(File file,
				Map<String, Range> indexFileRange, long totalNumberOfReads,
				List<WholeAssemblyAceTag> wholeAssemblyTags,
				List<ConsensusAceTag> consensusTags, List<ReadAceTag> readTags) {
			super(file, indexFileRange, totalNumberOfReads, wholeAssemblyTags,
					consensusTags, readTags);
		}

		@Override
		protected AceContig get(String contigId,Range range) throws DataStoreException {
			File file = getFile();
			InputStream inputStream=null;
	        try {
	        	inputStream = IOUtil.createInputStreamFromFile(file,range.getBegin(), (int)range.getLength());
	            SingleAceContigBuilderVisitor builder = new SingleAceContigBuilderVisitor();
	            AceFileParser.parse(inputStream, builder);
	            return builder.getContig();
	        } catch (Exception e) {
	            throw new DataStoreException("error trying to get contig "+ contigId,e);
	        }finally{
	            IOUtil.closeAndIgnoreErrors(inputStream);
	        }
		}
		
	}
  
}
