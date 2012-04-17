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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code IndexedAceFileDataStore} is an implementation of 
 * {@link AceContigDataStore} that only stores an index containing
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
public final class IndexedAceFileDataStore implements AceContigDataStore{
    private final IndexedFileRange indexFileRange;
    private final File file;
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
     * @return a new AceContigDataStoreBuilder, never null.
     * throws NullPointerException if aceFile is null.
     */
    public static AceContigDataStoreBuilder createBuilder(File aceFile, IndexedFileRange indexFileRange){
        return new IndexedAceFileDataStoreBuilder(aceFile,indexFileRange);
    }
    /**
     * Create a new {@link AceContigDataStore} instance
     * for the contigs in the given aceFile.
     * @param aceFile the aceFile to parse.
     * @return a new  a new {@link AceContigDataStore}
     * that only stores an index containing file offsets to the various contigs contained
     * inside the ace file. 
     * @throws IOException if there is a problem reading the ace file
     * @throws NullPointerException if aceFile is null.
     */
    public static AceContigDataStore create(File aceFile) throws IOException{
        AceContigDataStoreBuilder builder = createBuilder(aceFile);
        AceFileParser.parseAceFile(aceFile, builder);
        return builder.build();
    }
    public static AceContigDataStore create(File aceFile, IndexedFileRange indexFileRange) throws IOException{
        AceContigDataStoreBuilder builder = createBuilder(aceFile,indexFileRange);
        AceFileParser.parseAceFile(aceFile, builder);
        return builder.build();
    }
    
    private IndexedAceFileDataStore(File file, IndexedFileRange indexFileRange){
        this.indexFileRange = indexFileRange;
        this.file = file;
    }
    
    private IndexedAceFileDataStore(File file){
        this(file, new DefaultIndexedFileRange());
    }

    
    @Override
    public boolean contains(String contigId) throws DataStoreException {
        return indexFileRange.contains(contigId);
    }

    @Override
    public AceContig get(String contigId) throws DataStoreException {
        Range range = indexFileRange.getRangeFor(contigId);
        InputStream inputStream=null;
        try {
        	IndexedAceFileContig.IndexedContigVisitorBuilder visitorBuilder = new IndexedAceFileContig.IndexedContigVisitorBuilder(range.getBegin(), file);
            inputStream = IOUtil.createInputStreamFromFile(file,range);
            AceFileParser.parseAceFile(inputStream, visitorBuilder);
            return visitorBuilder.build();
        } catch (Exception e) {
            throw new DataStoreException("error trying to get contig "+ contigId,e);
        }finally{
            IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }

    @Override
    public CloseableIterator<String> idIterator() {
        return indexFileRange.getIds();
    }

    @Override
    public long getNumberOfRecords() {
        return indexFileRange.size();
    }

    @Override
    public void close() throws IOException {
        indexFileRange.close();
        
    }


    @Override
    public CloseableIterator<AceContig> iterator() {
        return new IndexedContigIterator(idIterator());
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return indexFileRange.isClosed();
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
        private IndexedFileRange indexFileRange;
        private final File aceFile;
        private long currentStartOffset;
        private int currentLineLength;
        private long currentFileOffset;
        private boolean firstContig=true;
        private String currentContigId;
        private boolean hasTags=false;
        public IndexedAceFileDataStoreBuilder(File aceFile){
            if(aceFile==null){
                throw new NullPointerException("ace file cannot be null");
            }           
            this.aceFile = aceFile;
        }
        public IndexedAceFileDataStoreBuilder(File aceFile, IndexedFileRange indexFileRange){
            this(aceFile);
            this.indexFileRange = indexFileRange;
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
        	if(!firstContig){                
                visitContig();
            }
            currentContigId = contigId;
            currentStartOffset=currentFileOffset-currentLineLength;
            firstContig=false;
            return false;
		}

        protected synchronized void visitContig() {
            indexFileRange.put(currentContigId, Range.create(currentStartOffset, 
                    currentFileOffset-currentLineLength-1));
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized AceContigDataStore build() {
            return new IndexedAceFileDataStore(aceFile, indexFileRange);
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
            if(!hasTags){
                Range range = indexFileRange.getRangeFor(currentContigId);
                if(range ==null){
                    throw new IllegalStateException("in complete ace file.  Did not finish reading "+ currentContigId);
                   // indexFileRange.put(currentContigId, Range.create(currentStartOffset, 
                   //         currentFileOffset-currentLineLength-1));
                }
                indexFileRange.put(currentContigId, range.grow(0, currentLineLength));
            }
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitHeader(int numberOfContigs, int totalNumberOfReads) {
            if(indexFileRange==null){
                indexFileRange = new DefaultIndexedFileRange(numberOfContigs);
            }
            
        }

     
        /**
         * {@inheritDoc}
         */
        @Override
		public void visitBeginContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
			
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
            hasTags=true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized boolean visitEndOfContig() {    

            visitContig();
            return true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitBeginConsensusTag(String id, String type,
                String creator, long gappedStart, long gappedEnd,
                Date creationDate, boolean isTransient) {
            hasTags=true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitConsensusTagComment(String comment) {
            hasTags=true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitConsensusTagData(String data) {
            hasTags=true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndConsensusTag() {
            hasTags=true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitWholeAssemblyTag(String type, String creator,
                Date creationDate, String data) {
            hasTags=true;
        }
    }
    
    private static final class IndexedAceFileContig implements AceContig{

    	private final String contigId;
    	private Map<String, AlignedReadInfo> readInfoMap;
    	private IndexedFileRange readOffsetRanges;
    	private boolean isComplimented;
    	private final NucleotideSequence consensus;
    	private final File aceFile;
    	private final long contigStartFileOffset;
    	
    	
		public IndexedAceFileContig(String contigId,
				Map<String, AlignedReadInfo> readInfoMap,
				IndexedFileRange readOffsetRanges, boolean isComplimented,
				NucleotideSequence consensus, File aceFile,
				long contigStartFileOffset) {
			this.contigId = contigId;
			this.readInfoMap = readInfoMap;
			this.readOffsetRanges = readOffsetRanges;
			this.isComplimented = isComplimented;
			this.consensus = consensus;
			this.aceFile = aceFile;
			this.contigStartFileOffset = contigStartFileOffset;
		}

		@Override
		public String getId() {
			return contigId;
		}

		@Override
		public int getNumberOfReads() {
			return readInfoMap.size();
		}

		@Override
		public CloseableIterator<AcePlacedRead> getReadIterator() {
			InputStream in = null;
			try{
				in = new FileInputStream(aceFile);
				//seek to start of contig
				IOUtil.blockingSkip(in, contigStartFileOffset);
				//start parsing
				IndexedReadIterator iter = new IndexedReadIterator(in, consensus, readInfoMap);
				iter.start();
				return iter;
			} catch (IOException e) {
				throw new IllegalStateException("error iterating over reads",e);
			}
		}

		@Override
		public NucleotideSequence getConsensus() {
			return consensus;
		}

		@Override
		public AcePlacedRead getRead(String id) {
			if(!containsRead(id)){
				return null;
			}
			InputStream in = null;
			try{
				Range offsetRange = readOffsetRanges.getRangeFor(id);
				in = IOUtil.createInputStreamFromFile(aceFile, offsetRange);
				ReadVisitorBuilder builder = new ReadVisitorBuilder(consensus);
				builder.visitBeginContig(contigId, 0, 0, 0, isComplimented);
				AlignedReadInfo alignmentInfo = readInfoMap.get(id);
				builder.visitAssembledFromLine(id, alignmentInfo.getDirection(), alignmentInfo.getStartOffset());
				
				AceFileParser.parseAceFile(in, builder);
				return builder.build();
				
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("ace file no longer exists", e);
			} catch (IOException e) {
				
				throw new IllegalStateException("error parsing ace file", e);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
		}

		@Override
		public boolean containsRead(String readId) {
			return readInfoMap.containsKey(readId);
		}

		@Override
		public boolean isComplemented() {
			return isComplimented;
		}
    	
		private static final class IndexedContigVisitorBuilder implements AceFileVisitor, Builder<AceContig>{
			
			private long startOffset=0;
			private IndexedFileRange readRanges;
			private final File aceFile;
			private String currentLine;
			private String contigId;
			private boolean isComplimented;
			private Map<String, AlignedReadInfo> readInfoMap;
			private NucleotideSequenceBuilder consensusBuilder;
			private boolean readingConsensus=true;
			private int currentReadLength=0;
			private String currentReadId;
			private long contigStartOffset=0;
			
			public IndexedContigVisitorBuilder(long startOffset, File aceFile) {
				this.startOffset = startOffset;
				this.aceFile = aceFile;
				this.contigStartOffset = startOffset;
			}

			@Override
			public void visitLine(String line) {
				currentLine = line;
				if(readingConsensus){
					startOffset+=currentLine.length();
				}else{
					currentReadLength+=currentLine.length();
				}
			}

			@Override
			public void visitFile() {
				
			}

			@Override
			public void visitEndOfFile() {
				
			}

			@Override
			public AceContig build() {
				return new IndexedAceFileContig(contigId, readInfoMap, readRanges, isComplimented, 
						consensusBuilder.build(), aceFile, 
						contigStartOffset);
			}

			@Override
			public void visitHeader(int numberOfContigs, int totalNumberOfReads) {
				
			}

			@Override
			public boolean shouldVisitContig(String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplimented) {
				this.contigId =contigId;
				readRanges = new DefaultIndexedFileRange(numberOfReads);
				isComplimented = reverseComplimented;
				readInfoMap = new LinkedHashMap<String, AlignedReadInfo>(numberOfReads+1, 1F);
				consensusBuilder = new NucleotideSequenceBuilder();
				return true;
			}

			@Override
			public void visitBeginContig(String contigId, int numberOfBases,
					int numberOfReads, int numberOfBaseSegments,
					boolean reverseComplimented) {
				
			}

			@Override
			public void visitConsensusQualities() {
				
			}

			@Override
			public void visitAssembledFromLine(String readId, Direction dir,
					int gappedStartOffset) {
				readInfoMap.put(readId, new AlignedReadInfo(gappedStartOffset, dir));
				
			}

			@Override
			public void visitBaseSegment(Range gappedConsensusRange,
					String readId) {
				
			}

			@Override
			public void visitReadHeader(String readId, int gappedLength) {
				if(readingConsensus){
					readingConsensus=false;
					startOffset -=currentLine.length();
				}
				currentReadLength = currentLine.length();
				currentReadId= readId;
			}

			@Override
			public void visitQualityLine(int qualLeft, int qualRight,
					int alignLeft, int alignRight) {
				
			}

			@Override
			public void visitTraceDescriptionLine(String traceName,
					String phdName, Date date) {
				//end of current read
				readRanges.put(currentReadId, Range.createOfLength(startOffset, currentReadLength));
				startOffset += currentReadLength+1;
			}

			@Override
			public void visitBasesLine(String mixedCaseBasecalls) {
				if(readingConsensus){
					consensusBuilder.append(mixedCaseBasecalls.trim());
				}
				
			}

			@Override
			public void visitReadTag(String id, String type, String creator,
					long gappedStart, long gappedEnd, Date creationDate,
					boolean isTransient) {
				
			}

			@Override
			public boolean visitEndOfContig() {
				return false;
			}

			@Override
			public void visitBeginConsensusTag(String id, String type,
					String creator, long gappedStart, long gappedEnd,
					Date creationDate, boolean isTransient) {
				
			}

			@Override
			public void visitConsensusTagComment(String comment) {
				
			}

			@Override
			public void visitConsensusTagData(String data) {
				
			}

			@Override
			public void visitEndConsensusTag() {
				
			}

			@Override
			public void visitWholeAssemblyTag(String type, String creator,
					Date creationDate, String data) {
				
			}
			
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

    
    private final class IndexedContigIterator implements CloseableIterator<AceContig>{

    	private final CloseableIterator<String> idIterator;
    	
		public IndexedContigIterator(CloseableIterator<String> idIterator) {
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
			} catch (DataStoreException e) {
				throw new IllegalStateException("error getting contig "+ id,e);
			}finally{
				IOUtil.closeAndIgnoreErrors(idIterator);
			}
		}

		@Override
		public void remove() {
			idIterator.remove();
			
		}

    }
    private static final class IndexedReadIterator extends AbstractBlockingCloseableIterator<AcePlacedRead>{

    	private final NucleotideSequence consensus;
    	private final Map<String, AlignedReadInfo> readInfoMap;
    	private final InputStream in;
    	
		public IndexedReadIterator(InputStream in, NucleotideSequence consensus, final Map<String, AlignedReadInfo> readInfoMap) {
			this.consensus = consensus;
			this.readInfoMap = readInfoMap;
			this.in = in;
		}

		@Override
		protected void backgroundThreadRunMethod()
				throws RuntimeException {
			AbstractAceFileVisitor visitor = new AbstractAceFileVisitor() {
				{
					this.setAlignedInfoMap(readInfoMap);
				}
				@Override
				protected void visitNewContig(String contigId,
						NucleotideSequence consensus, int numberOfBases, int numberOfReads,
						boolean isComplimented) {
					//no-op
					
				}
				
				@Override
				public boolean visitEndOfContig() {
					return false;
				}

				@Override
				protected void visitAceRead(String readId,
						NucleotideSequence validBasecalls, int offset, Direction dir,
						Range validRange, PhdInfo phdInfo, int ungappedFullLength) {
					AcePlacedRead read =DefaultAcePlacedRead.createBuilder(IndexedReadIterator.this.consensus, readId, validBasecalls, offset, dir, validRange, phdInfo, ungappedFullLength)
							.build();
					
					blockingPut(read);
				}
			};
			try{
				AceFileParser.parseAceFile(in, visitor);
			} catch (IOException e) {
				throw new IllegalStateException("error parsing reads from contig in ace file",e);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}
		
	}
  
}
