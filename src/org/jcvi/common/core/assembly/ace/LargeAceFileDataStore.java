package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.AbstractDataStore;
import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code LargeAceFileDataStore} is an {@link AceContigDataStore}
 * implementation that doesn't store any contig or 
 * read information in memory.
 * This means that each {@link #get(String)} or {@link #contains(String)}
 * requires re-parsing the ace file which can take some time.
 * Other methods such as {@link #getNumberOfRecords()} are lazy-loaded
 * and are only parsed the first time they are asked for.
 * <p/>
 * Since each method call involves re-parsing the ace file,
 * that file must not be modified or moved during the
 * entire lifetime of the instance.
 * It is recommended that instances of {@link LargeAceFileDataStore}
 * are wrapped by {@link CachedDataStore}.
 * @author dkatzel
 *
 */
public final class LargeAceFileDataStore extends AbstractDataStore<AceContig> implements AceContigDataStore{

	private final File aceFile;
	private Long numberOfContigs = null;
	private final DataStoreFilter contigIdFilter;
	
	/**
	 * Create a new instance of {@link LargeAceFileDataStore}.
	 * @param aceFile the ace file to create an {@link AceContigDataStore}
	 * from. (can not be null and must exist)
	 * @return a new {@link AceContigDataStore}; 
	 * will never be null.
	 * @throws FileNotFoundException if the ace file does not exist.
	 * @throws NullPointerException if aceFile is null.
	 */
	public static AceContigDataStore create(File aceFile) throws FileNotFoundException{
		return new LargeAceFileDataStore(aceFile, AcceptingDataStoreFilter.INSTANCE);
	}
	/**
	 * Create a new instance of {@link LargeAceFileDataStore}
	 * with only some of the contigs from the given ace file.
	 * Any contigs excluded by the given {@link DataStoreFilter}
	 * will be completely ignored during calls to {@link #getNumberOfRecords()}
	 * {@link #iterator()} and {@link #idIterator()}, return
	 * {@code false} for {@link #contains(String)}
	 * and return null for {@link #get(String)}
	 * @param aceFile the ace file to create an {@link AceContigDataStore}
	 * from. (can not be null and must exist)
	 * @param contigIdFilter a {@link DataStoreFilter}
	 * instance if only some contigs from the given
	 * file should be included in this datastore.
	 * Calls to {@link #get(String)}
	 * @return a new {@link AceContigDataStore}; 
	 * will never be null.
	 * @throws FileNotFoundException if the ace file does not exist.
	 * @throws NullPointerException if aceFile is null.
	 */
	public static AceContigDataStore create(File aceFile, DataStoreFilter contigIdFilter) throws FileNotFoundException{
		return new LargeAceFileDataStore(aceFile, contigIdFilter);
	}
	
	
	private LargeAceFileDataStore(File aceFile, DataStoreFilter contigIdFilter) throws FileNotFoundException {
		if(contigIdFilter ==null){
			throw new NullPointerException("filter can not be null");
		}
		if(aceFile ==null){
			throw new NullPointerException("ace file can not be null");
		}
		if(!aceFile.exists()){
			throw new FileNotFoundException(
					String.format("ace file %s does not exist", aceFile.getAbsolutePath()));
		}
		this.aceFile = aceFile;
		this.contigIdFilter = contigIdFilter;
	}
	@Override
	public synchronized CloseableIterator<String> idIterator() throws DataStoreException {
		throwExceptionIfClosed();
		IdVisitor ids = new IdVisitor();
		ids.start();
		return ids;
	}
	@Override
	public synchronized AceContig get(String id) throws DataStoreException {
		throwExceptionIfClosed();
		if(!contigIdFilter.accept(id)){
			throw new DataStoreException(String.format("contig id %s not allowed by filter", id));
		}
		SingleContigVisitor visitor = new SingleContigVisitor(id);
		try {
			AceFileParser.parseAceFile(aceFile, visitor);
		} catch (IOException e) {
			throw new DataStoreException("error parsing ace file",e);
		}
		return visitor.getContig();
	}
	@Override
	public synchronized boolean contains(String id) throws DataStoreException {
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		throwExceptionIfClosed();
		if(!contigIdFilter.accept(id)){
			throw new DataStoreException(String.format("contig id %s not allowed by filter", id));
		}
		boolean found = false;
		CloseableIterator<String> ids = idIterator();
		try{
			while(ids.hasNext()){
				String nextId = ids.next();
				if(id.equals(nextId)){
					found=true;
					IOUtil.closeAndIgnoreErrors(ids);
				}
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(ids);
		}
		return found;
	}
	@Override
	public synchronized long getNumberOfRecords() throws DataStoreException {
		throwExceptionIfClosed();
		if(numberOfContigs ==null){
			//haven't parsed num contigs yet 
			SizeVisitor visitor = new SizeVisitor();
			try {
				AceFileParser.parseAceFile(aceFile, visitor);
			} catch (IOException e) {
				throw new DataStoreException("error parsing number of contigs",e);
			}
			numberOfContigs= visitor.getSize();
		}
		return numberOfContigs;
	}
	
	@Override
	public synchronized CloseableIterator<AceContig> iterator() {
		throwExceptionIfClosed();
		AceFileDataStoreIterator iter= new AceFileDataStoreIterator();
	    iter.start();
	    return iter;
	}
	
	@Override
	protected void handleClose() throws IOException {
		//no-op
		
	}

	private class SizeVisitor implements AceFileVisitor{

		private long size=0;
		
		
		public long getSize() {
			return size;
		}

		@Override
		public void visitLine(String line) {
			//no-op
			
		}

		@Override
		public void visitFile() {
			//no-op
			
		}

		@Override
		public void visitEndOfFile() {
			//no-op
			
		}

		@Override
		public void visitHeader(int numberOfContigs, int totalNumberOfReads) {
			//no-op
			
		}

		@Override
		public boolean shouldVisitContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
			if(contigIdFilter.accept(contigId)){
				size++;
				return true;
			}
			return false;
		}

		@Override
		public void visitBeginContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
			//no-op
			
		}

		@Override
		public void visitConsensusQualities() {
			//no-op
			
		}

		@Override
		public void visitAssembledFromLine(String readId, Direction dir,
				int gappedStartOffset) {
			//no-op
			
		}

		@Override
		public void visitBaseSegment(Range gappedConsensusRange, String readId) {
			//no-op
			
		}

		@Override
		public void visitReadHeader(String readId, int gappedLength) {
			//no-op
			
		}

		@Override
		public void visitQualityLine(int qualLeft, int qualRight,
				int alignLeft, int alignRight) {
			//no-op
			
		}

		@Override
		public void visitTraceDescriptionLine(String traceName, String phdName,
				Date date) {
			//no-op
			
		}

		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			//no-op
			
		}

		@Override
		public void visitReadTag(String id, String type, String creator,
				long gappedStart, long gappedEnd, Date creationDate,
				boolean isTransient) {
			//no-op
			
		}

		@Override
		public boolean visitEndOfContig() {
			//no-op
			return true;
		}

		@Override
		public void visitBeginConsensusTag(String id, String type,
				String creator, long gappedStart, long gappedEnd,
				Date creationDate, boolean isTransient) {
			//no-op
			
		}

		@Override
		public void visitConsensusTagComment(String comment) {
			//no-op
			
		}

		@Override
		public void visitConsensusTagData(String data) {
			//no-op
			
		}

		@Override
		public void visitEndConsensusTag() {
			//no-op
			
		}

		@Override
		public void visitWholeAssemblyTag(String type, String creator,
				Date creationDate, String data) {
			//no-op
			
		}

	}
	/**
     * Special implementation of a {@link CloseableIterator}
     * that directly parses the ace file.  This allows us
     * to iterate over the entire file in 1 pass.
     * @author dkatzel
     */
    private class AceFileDataStoreIterator extends AbstractBlockingCloseableIterator<AceContig>{

        /**
        * {@inheritDoc}
        */
        @Override
        protected void backgroundThreadRunMethod() {
            AbstractAceContigBuilder builder = new AbstractAceContigBuilder() {

                @Override
				public synchronized boolean shouldVisitContig(String contigId,
						int numberOfBases, int numberOfReads,
						int numberOfBaseSegments, boolean reverseComplimented) {
					return contigIdFilter.accept(contigId);
				}

			

                @Override
                protected void visitContig(AceContig contig) {
                    AceFileDataStoreIterator.this.blockingPut(contig);
                    
                }
                
                
            };
            try {
                AceFileParser.parseAceFile(aceFile, builder);
            } catch (Exception e) {
                //some kind of exception occured while we were parsing the ace file
                throw new RuntimeException("error while iterating over ace file",e);
            }
            
        }
    }
    
    private final class SingleContigVisitor extends  AbstractAceContigBuilder{

    	private final String contigIdtoFind;
    	private AceContig contig;
    	
		public SingleContigVisitor(String contigIdtoFind) {
			if(contigIdtoFind ==null){
				throw new NullPointerException("contig id to find can not be null");
			}
			this.contigIdtoFind = contigIdtoFind;
		}

		@Override
		public boolean shouldVisitContig(String contigId, int numberOfBases,
				int numberOfReads, int numberOfBaseSegments,
				boolean reverseComplimented) {
			return contigIdtoFind.equals(contigId);
		}

		@Override
		protected void visitContig(AceContig contig) {
			this.contig = contig;
		}

		public AceContig getContig() {
			return contig;
		}
    }
    
    private class IdVisitor extends AbstractBlockingCloseableIterator<String>{

        /**
        * {@inheritDoc}
        */
        @Override
        protected void backgroundThreadRunMethod() {
        	AceFileVisitor builder = new AceFileVisitor() {

				@Override
				public void visitLine(String line) {
					//no-op
					
				}

				@Override
				public void visitFile() {
					//no-op
					
				}

				@Override
				public void visitEndOfFile() {
					//no-op
					
				}

				@Override
				public void visitHeader(int numberOfContigs,
						int totalNumberOfReads) {
					//no-op
					
				}

				@Override
				public boolean shouldVisitContig(String contigId,
						int numberOfBases, int numberOfReads,
						int numberOfBaseSegments, boolean reverseComplimented) {
					if(contigIdFilter.accept(contigId)){
						IdVisitor.this.blockingPut(contigId);
						return true;
					}
					return false;
				}

				@Override
				public void visitBeginContig(String contigId,
						int numberOfBases, int numberOfReads,
						int numberOfBaseSegments, boolean reverseComplimented) {
					//no-op
					
				}

				@Override
				public void visitConsensusQualities() {
					//no-op
					
				}

				@Override
				public void visitAssembledFromLine(String readId,
						Direction dir, int gappedStartOffset) {
					//no-op
					
				}

				@Override
				public void visitBaseSegment(Range gappedConsensusRange,
						String readId) {
					//no-op
					
				}

				@Override
				public void visitReadHeader(String readId, int gappedLength) {
					//no-op
					
				}

				@Override
				public void visitQualityLine(int qualLeft, int qualRight,
						int alignLeft, int alignRight) {
					//no-op
					
				}

				@Override
				public void visitTraceDescriptionLine(String traceName,
						String phdName, Date date) {
					//no-op
					
				}

				@Override
				public void visitBasesLine(String mixedCaseBasecalls) {
					//no-op
					
				}

				@Override
				public void visitReadTag(String id, String type,
						String creator, long gappedStart, long gappedEnd,
						Date creationDate, boolean isTransient) {
					//no-op
					
				}

				@Override
				public boolean visitEndOfContig() {
					//no-op
					return true;
				}

				@Override
				public void visitBeginConsensusTag(String id, String type,
						String creator, long gappedStart, long gappedEnd,
						Date creationDate, boolean isTransient) {
					//no-op
					
				}

				@Override
				public void visitConsensusTagComment(String comment) {
					//no-op
					
				}

				@Override
				public void visitConsensusTagData(String data) {
					//no-op
					
				}

				@Override
				public void visitEndConsensusTag() {
					//no-op
					
				}

				@Override
				public void visitWholeAssemblyTag(String type, String creator,
						Date creationDate, String data) {
					//no-op
					
				}

               
			

                
                
            };
            try {
                AceFileParser.parseAceFile(aceFile, builder);
            } catch (Exception e) {
                //some kind of exception occured while we were parsing the ace file
                throw new RuntimeException("error while iterating over ace file",e);
            }
            
        }
    }
}
