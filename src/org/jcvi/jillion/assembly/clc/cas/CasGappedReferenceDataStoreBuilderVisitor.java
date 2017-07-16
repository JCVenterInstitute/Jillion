/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.assembly.GappedReferenceBuilder;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;

/**
 * {@code CasGappedReferenceDataStoreBuilderVisitor}
 * is a {@link CasFileVisitor} that will create a 
 * {@link CasGappedReferenceDataStore} when it visits a {@literal .cas}
 * encoded file.
 * 
 * <p>
 * CLC {@literal .cas} files don't store the final gapped assembly
 * consensus sequences.  In order to correctly build valid
 * contig objects, the gapped consensus must be calculated for each
 * reference by visiting all the alignment information of all the input reads.
 * 
 * <p>
 * Once the entire cas file has been visited, the {@link #build()}
 * method can be called to return the {@link CasGappedReferenceDataStore}.
 * 
 * <p>
 * Here is how this class should be used:
 * <pre>
 * File casFile = ...
 CasGappedReferenceDataStoreBuilderVisitor gappedRefVisitor = new CasGappedReferenceDataStoreBuilderVisitor(casFile.getParentFile());
 
 CasFileParser casFileParser = new CasFileParser(casFile);
 casFileParser.accept(gappedRefVisitor);
 
 CasGappedReferenceDataStore gappedReferenceDataStore = gappedRefVisitor.build();
        </pre>
 * 
 * 
 * 
 * @author dkatzel
 *
 */
public final class CasGappedReferenceDataStoreBuilderVisitor implements CasFileVisitor{

	private String[] refIndexToIdMap;
	
	private GappedReferenceBuilder[] gappedReferenceBuilders ;
	private volatile boolean halted=false;
	
	private volatile CasGappedReferenceDataStore builtDataStore=null;
	
	private final File casDir;
	private final Predicate<String> refIdFilter;
	private int refCounter=0;
	/**
	 * Create a new Visitor that only makes a gapped
	 * reference DataStore for all references.
	 * This is the same as using a refIdFilter that accepts
	 * all references.
	 * 
	 * @param casDir he parent directory that the cas file to be parsed is located in;
	 * if this value is {@code null} then the cas is located in the current.
	 */
	public CasGappedReferenceDataStoreBuilderVisitor(File casDir) {
		this(casDir, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Create a new Visitor that only makes a gapped
	 * reference DataStore for the references that pass the given {@link Predicate}.
	 * 
	 * @param casDir the parent directory that the cas file to be parsed is located in;
	 * if this value is {@code null} then the cas is located in the current 
	 * @param refIdFilter the filter to use to determine if some alignments should be skipped;
	 * can not be null.
	 * 
	 * @throws NullPointerException if refIdFilter is null.
	 * @since 5.0
	 */
	public CasGappedReferenceDataStoreBuilderVisitor(File casDir, Predicate<String> refIdFilter) {
		Objects.requireNonNull(refIdFilter);
		this.casDir = casDir;
		this.refIdFilter = refIdFilter;
	}
	

	@Override
	public void visitAssemblyProgramInfo(String name, String version,
			String parameters) {
		checkNotYetBuilt();
	}

	@Override
	public void visitMetaData(long numberOfReferenceSequences,	long numberOfReads) {
		checkNotYetBuilt();
		gappedReferenceBuilders = new GappedReferenceBuilder[(int)numberOfReferenceSequences];
		refIndexToIdMap = new String[(int)numberOfReferenceSequences];
	}

	@Override
	public void visitNumberOfReadFiles(long numberOfReadFiles) {
		checkNotYetBuilt();
	}

	@Override
	public void visitNumberOfReferenceFiles(long numberOfReferenceFiles) {
		checkNotYetBuilt();
	}

	@Override
	public void visitReferenceFileInfo(CasFileInfo referenceFileInfo) {
		checkNotYetBuilt();
		
		for(String filePath: referenceFileInfo.getFileNames()){
            try {
            	File refFile = CasUtil.getFileFor(casDir, filePath);
            	NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(refFile)
            												.build();
            	
            	
            	StreamingIterator<NucleotideFastaRecord> iter=null;
            	try{            		
            		iter = datastore.iterator();
            		while(iter.hasNext()){
            			NucleotideFastaRecord next = iter.next();
            			String id = next.getId();
            			
            			refIndexToIdMap[refCounter]= id;
            			if(refIdFilter.test(id)){
	            			gappedReferenceBuilders[refCounter]= new GappedReferenceBuilder(next.getSequence());
            			}
            			refCounter++;
            		}
            	}finally{
        			IOUtil.closeAndIgnoreErrors(iter, datastore);
        		}
                
            } catch (Exception e) {
               throw new IllegalStateException("could not load read file: "+ filePath,e);
            }
        }
		
	}

	@Override
	public void visitReadFileInfo(CasFileInfo readFileInfo) {
		checkNotYetBuilt();
	}

	@Override
	public void visitScoringScheme(CasScoringScheme scheme) {
		checkNotYetBuilt();
	}

	@Override
	public void visitReferenceDescription(CasReferenceDescription description) {
		checkNotYetBuilt();
	}

	@Override
	public void visitContigPair(CasContigPair contigPair) {
		checkNotYetBuilt();
	}

	@Override
	public void visitEnd() {
		checkNotYetBuilt();
		int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(gappedReferenceBuilders.length);
		Map<String, NucleotideSequence> gappedSequenceMap = new LinkedHashMap<String, NucleotideSequence>(capacity);
	    for(int j = 0; j< gappedReferenceBuilders.length; j++){
        	
        	String refId = refIndexToIdMap[j];
        	if(refIdFilter.test(refId)){
        		gappedSequenceMap.put(refId, gappedReferenceBuilders[j].build());
        	}
        }
        
        builtDataStore = new CasGappedReferenceDataStoreImpl(DataStore.of(gappedSequenceMap), 
        													refIndexToIdMap);
		
	}

	private void checkNotYetBuilt(){
		if(builtDataStore !=null){
			throw new IllegalStateException("should only parse cas once");
		}
	}
	

	@Override
	public void halted() {
		halted = true;
	}

	@Override
	public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
		return new MaxRefGapVisitor();
	}

	public CasGappedReferenceDataStore build(){
		if(halted){
			throw new IllegalStateException("visiting was halted; can not build datastore");
		}
		if(builtDataStore ==null){
			throw new IllegalStateException("have not yet completly visited the cas to build the datastore");
		}
		return builtDataStore;
	}
	
	private class MaxRefGapVisitor implements CasMatchVisitor{

		private List<CasAlignmentRegion> getAlignmentRegionsToConsider(
				CasAlignment alignment) {
			List<CasAlignmentRegion> regionsToConsider = new ArrayList<CasAlignmentRegion>(alignment.getAlignmentRegions());
			int lastIndex = regionsToConsider.size()-1;
			//CLC puts 3' unmapped portion of read as an insertion
			if(regionsToConsider.get(lastIndex).getType()==CasAlignmentRegionType.INSERT){
			    regionsToConsider.remove(lastIndex);
			}
			return regionsToConsider;
		}

		@Override
		public void visitMatch(CasMatch match) {
			 if(match.matchReported()){
		            handleMatch(match);
		        }
			
		}

		private void handleMatch(CasMatch match) {
			CasAlignment alignment =match.getChosenAlignment();
			long referenceIndex = alignment.getReferenceIndex();		            
			
			if(includeReadsFromThisReference(referenceIndex)){
			
				addInsertionsToReference(alignment, referenceIndex);
			}
		}

		private void addInsertionsToReference(CasAlignment alignment, Long referenceIndex) {
			
			GappedReferenceBuilder builder = gappedReferenceBuilders[referenceIndex.intValue()];
			if(builder ==null){
				//skip read
				return;
			}
			List<CasAlignmentRegion> regionsToConsider = getAlignmentRegionsToConsider(alignment);
			boolean outsideValidRange=true;
			int currentOffset = (int)alignment.getStartOfMatch();
			for(CasAlignmentRegion region: regionsToConsider){
				//1st non insertion type is beginning of where we map
			    if(outsideValidRange && region.getType() != CasAlignmentRegionType.INSERT){
			        outsideValidRange=false;
			    }
			    if(!outsideValidRange){
			        
			        if(region.getType() == CasAlignmentRegionType.INSERT){
			        	builder.addReadInsertion(currentOffset, (int)region.getLength());			            
			        }else{
			            currentOffset +=(int)region.getLength();
			        }
			    }
			}
		}

		
		private boolean includeReadsFromThisReference(long referenceIndex){
			int i = (int)referenceIndex;
			if( i< 0 || i >= gappedReferenceBuilders.length){
				throw new IllegalStateException("reference file does not contain a reference with index "+ referenceIndex);
			}
			return gappedReferenceBuilders[i] !=null;
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
	
	 
	 
	 private static final class CasGappedReferenceDataStoreImpl implements CasGappedReferenceDataStore{

		 private final DataStore<NucleotideSequence> delegate;
		 private final String[] refIndexToIdMap;
		 private final Map<String, Long> id2IndexMap;
		 
		public CasGappedReferenceDataStoreImpl(
				DataStore<NucleotideSequence> delegate,
				String[] refIndexToIdMap) {
			this.delegate = delegate;
			this.refIndexToIdMap = refIndexToIdMap;
			id2IndexMap = new HashMap<String, Long>(MapUtil.computeMinHashMapSizeWithoutRehashing(refIndexToIdMap.length));
			for(int i=0; i< refIndexToIdMap.length; i++){
				String id = refIndexToIdMap[i];
				if(id !=null){
					id2IndexMap.put(id, Long.valueOf(i));
				}
			}
			
		}

		@Override
		public Long getIndexById(String id) {
			return id2IndexMap.get(id);
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			return delegate.idIterator();
		}

		@Override
		public NucleotideSequence get(String id) throws DataStoreException {
			return delegate.get(id);
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			return delegate.contains(id);
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			return delegate.getNumberOfRecords();
		}

		@Override
		public boolean isClosed() {
			return delegate.isClosed();
		}

		@Override
		public StreamingIterator<NucleotideSequence> iterator()
				throws DataStoreException {
			return delegate.iterator();
		}

		
		
		@Override
		public StreamingIterator<DataStoreEntry<NucleotideSequence>> entryIterator()
				throws DataStoreException {
			return delegate.entryIterator();
		}

		@Override
		public void close() throws IOException {
			delegate.close();
			
		}

		@Override
		public NucleotideSequence getReferenceByIndex(long index) throws DataStoreException {
			return get(getIdByIndex(index));
		}

		@Override
		public String getIdByIndex(long index) {
			return refIndexToIdMap[(int)index];
		}
		 
	 }
}
