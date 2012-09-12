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
package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil.ClipPointsType;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreStreamingIterator;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.Builder;
import org.jcvi.common.core.util.MapUtil;
import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code IndexedAceFileDataStore} is an implementation of 
 * {@link AceFileContigDataStore} that only stores an index containing
 * byte offsets to the various contigs contained
 * inside the ace file. Furthermore, each {@link AceContig}
 * in this datastore will not store all the underlying read
 * data in memory either.  Calls to {@link AceContig#getRead(String)} may
 * cause part of the ace file to be re-parsed in order to retreive
 * any missing information.  
 * <p/>
 * This allows large files to provide random 
 * access without taking up much memory.  The down side is each contig
 * must be re-parsed each time and the ace file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
public final class IndexedAceFileDataStore implements AceFileContigDataStore{
    private final Map<String, Range> indexFileRange;
    private final File file;
    private final long totalNumberOfReads;
    private final List<WholeAssemblyAceTag> wholeAssemblyTags;
    private final List<ConsensusAceTag> consensusTags;
    private final List<ReadAceTag> readTags;
    
    private volatile boolean  closed=false;
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
    
    private IndexedAceFileDataStore(File file, Map<String, Range> indexFileRange,
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
        InputStream inputStream=null;
        try {
        	IndexedAceFileContig.IndexedContigVisitorBuilder visitorBuilder = new IndexedAceFileContig.IndexedContigVisitorBuilder(range.getBegin(), file);
            inputStream = IOUtil.createInputStreamFromFile(file,(int)range.getBegin(), (int)range.getLength());
            AceFileParser.parse(inputStream, visitorBuilder);
            return visitorBuilder.build();
        } catch (Exception e) {
            throw new DataStoreException("error trying to get contig "+ contigId,e);
        }finally{
            IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }

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
        /**
         * Consensus tags span multiple lines of the ace file so we need to build
         * up the consensus tags as we parse.
         */
        private DefaultConsensusAceTag.Builder consensusTagBuilder;
        
        public IndexedAceFileDataStoreBuilder(File aceFile){
            if(aceFile==null){
                throw new NullPointerException("ace file cannot be null");
            }           
            this.aceFile = aceFile;
        }
        
        @Override
        public synchronized void visitLine(String line) {        
            final int length = line.length();
            currentLineLength = length;
            currentFileOffset+=length;            
        }
        
        @Override
		public synchronized boolean shouldVisitContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
            currentContigId = contigId;
            currentStartOffset=currentFileOffset-currentLineLength;
            return true;
		}
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized AceFileContigDataStore build() {
            return new IndexedAceFileDataStore(aceFile, indexFileRange,
            		totalNumberOfReads,
            		wholeAssemblyTags,consensusTags,readTags);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void visitFile() {
            
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
		public void visitBeginContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
			totalNumberOfReads +=numberOfReads;
		}
		/**
        * {@inheritDoc}
        */
        @Override
        public void visitConsensusQualities() {
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitAssembledFromLine(String readId, Direction dir,
                int gappedStartOffset) {
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBaseSegment(Range gappedConsensusRange, String readId) {
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadHeader(String readId, int gappedLength) {
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitQualityLine(int qualLeft, int qualRight,
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
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBasesLine(String bases) {
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadTag(String id, String type, String creator,
                long gappedStart, long gappedEnd, Date creationDate,
                boolean isTransient) {
            readTags.add(new DefaultReadAceTag(id, type, creator, creationDate, 
                    Range.create(gappedStart,gappedEnd), isTransient));
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized boolean visitEndOfContig() {    
        	//
        	indexFileRange.put(currentContigId, Range.create(currentStartOffset, 
                    currentFileOffset-1));
            return true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBeginConsensusTag(String id, String type,
                String creator, long gappedStart, long gappedEnd,
                Date creationDate, boolean isTransient) {
            consensusTagBuilder = new DefaultConsensusAceTag.Builder(id, 
                    type, creator, creationDate, Range.create(gappedStart, gappedEnd), isTransient);

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
            wholeAssemblyTags.add(new DefaultWholeAssemblyAceTag(type, creator, creationDate, data.trim()));
            
        }
    }
    
    
    
    public static final class ReadVisitorBuilder extends AbstractAceFileVisitor implements Builder<AcePlacedRead>{

    	private final NucleotideSequence consensus;
    	private AcePlacedRead builtRead;
		public ReadVisitorBuilder(NucleotideSequence consensus) {
			this.consensus = consensus;
		}

		@Override
		public AcePlacedRead build() {
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
			builtRead= DefaultAcePlacedRead.createBuilder(consensus, readId, validBasecalls, offset, dir, validRange, phdInfo, ungappedFullLength)
					.build();
			
		}
		
		
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
