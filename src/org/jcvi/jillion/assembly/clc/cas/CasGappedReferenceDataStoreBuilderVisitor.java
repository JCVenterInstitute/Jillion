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
package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
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
 * <p/>
 * CLC {@literal .cas} files don't store the final gapped assembly
 * consensus sequences.  In order to correctly build valid
 * contig objects, the gapped consensus must be calculated for each
 * reference by visiting all the alignment information of all the input reads.
 * 
 * <p/>
 * Once the entire cas file has been visited, the {@link #build()}
 * method can be called to return the {@link CasGappedReferenceDataStore}.
 * 
 * <p/>
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

	private final SortedMap<Long, SortedMap<Long,Insertion>> gapsByReferenceIndex = new TreeMap<Long, SortedMap<Long,Insertion>>();
	private final Map<Long, String> refIndexToIdMap = new TreeMap<Long, String>();
	
	private final Map<Long, NucleotideSequenceBuilder> gappedReferenceBuilders = new TreeMap<Long, NucleotideSequenceBuilder>();
	private volatile boolean halted=false;
	
	private volatile CasGappedReferenceDataStore builtDataStore=null;
	
	private final File casDir;
	
	
	
	public CasGappedReferenceDataStoreBuilderVisitor(File casDir) {
		this.casDir = casDir;
	}

	@Override
	public void visitAssemblyProgramInfo(String name, String version,
			String parameters) {
		checkNotYetBuilt();
	}

	@Override
	public void visitMetaData(long numberOfReferenceSequences,
			long numberOfReads) {
		checkNotYetBuilt();
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
		long refCounter=0L;
		for(String filePath: referenceFileInfo.getFileNames()){
            try {
            	File refFile = CasUtil.getFileFor(casDir, filePath);
            	NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(refFile)
            												.hint(DataStoreProviderHint.ITERATION_ONLY)
            												.build();
            	StreamingIterator<NucleotideFastaRecord> iter=null;
            	try{
            		iter = datastore.iterator();
            		while(iter.hasNext()){
            			NucleotideFastaRecord next = iter.next();
            			String id = next.getId();
            			Long index =Long.valueOf(refCounter);
            			
            			refIndexToIdMap.put(index, id);
            			gappedReferenceBuilders.put(index, new NucleotideSequenceBuilder(next.getSequence()));
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
		
		Map<String, NucleotideSequence> gappedSequenceMap = new LinkedHashMap<String, NucleotideSequence>();
	    
		for(Entry<Long, NucleotideSequenceBuilder> entry : gappedReferenceBuilders.entrySet()){
        	Long refIndex = entry.getKey();
        	
        	NucleotideSequenceBuilder gappedSequenceBuilder = entry.getValue();
        	//iterates in reverse to keep offsets in sync
        	SortedMap<Long, Insertion> sortedMap = gapsByReferenceIndex.get(refIndex);
        	//VHTNGS-603 - if no reads mapped to the reference then sortedMap is null
        	if(sortedMap !=null){
				for(Entry<Long, Insertion> insertionEntry : sortedMap.entrySet()){
	        		int offset = insertionEntry.getKey().intValue();
	        		int maxGapSize = (int)insertionEntry.getValue().getSize();
	        		gappedSequenceBuilder.insert(offset, createGapStringOf(maxGapSize));
	        	}
        	}
        	gappedSequenceMap.put(refIndexToIdMap.get(refIndex), gappedSequenceBuilder.build());
        }
        
        builtDataStore = new CasGappedReferenceDataStoreImpl(DataStoreUtil.adapt(gappedSequenceMap), 
        													refIndexToIdMap);
		
	}

	private void checkNotYetBuilt(){
		if(builtDataStore !=null){
			throw new IllegalStateException("should only parse cas once");
		}
	}
	
	public String createGapStringOf(int maxGapSize) {
		char[] gaps = new char[maxGapSize];
		Arrays.fill(gaps, '-');
		return new String(gaps);
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
		            
		            
		            CasAlignment alignment =match.getChosenAlignment();
		            Long referenceIndex = alignment.getReferenceIndex();		            
		            
		            if(!gapsByReferenceIndex.containsKey(referenceIndex)){
		            	if(!refIndexToIdMap.containsKey(referenceIndex)){
		            		throw new IllegalStateException("reference file does not contain a reference with index "+ referenceIndex);
		            	}
		                gapsByReferenceIndex.put(referenceIndex, new TreeMap<Long,Insertion>(DescendingOffsetComparator.INSTANCE));
		            }
		            
		            List<CasAlignmentRegion> regionsToConsider = getAlignmentRegionsToConsider(alignment);
		            boolean outsideValidRange=true;
		            long currentOffset = alignment.getStartOfMatch();
		            for(CasAlignmentRegion region: regionsToConsider){
		            	//1st non insertion type is beginning of where we map
		                if(outsideValidRange && region.getType() != CasAlignmentRegionType.INSERT){
		                    outsideValidRange=false;
		                }
		                if(!outsideValidRange){
		                    
		                    if(region.getType() == CasAlignmentRegionType.INSERT){
		                        Map<Long,Insertion> insertions =gapsByReferenceIndex.get(referenceIndex);
		                        if(insertions.containsKey(currentOffset)){
		                            insertions.get(currentOffset).updateSize(region.getLength());
		                        }
		                        else{
		                            insertions.put(currentOffset, new Insertion(region.getLength()));
		                        }
		                    }else{
		                        currentOffset +=region.getLength();
		                    }
		                }
		            }
		        }
			
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
	
	 static class Insertion{
	        private long size=0;
	        
	        public  Insertion(long initialSize){
	            this.size = initialSize;
	        }
	        public void updateSize(long newSize){
	            if(newSize > size){
	                this.size = newSize;
	            }
	        }
	        public long getSize(){
	            return size;
	        }
	    }
	 /**
	  * {@code ReverseOffsetComparator} 
	  * compares offsets in descending order
	  * instead of ascending order
	  * so we can modify our sequences
	  * without worrying about re-adjusting offsets. 
	  * @author dkatzel
	  *
	  */
	 private static enum DescendingOffsetComparator implements Comparator<Long>{
		 INSTANCE;

		@Override
		public int compare(Long o1, Long o2) {
			return o2.compareTo(o1);
		}
		 
	 }
	 
	 
	 private static final class CasGappedReferenceDataStoreImpl implements CasGappedReferenceDataStore{

		 private final DataStore<NucleotideSequence> delegate;
		 private final Map<Long, String> refIndexToIdMap;
		 private final Map<String, Long> Id2IndexMap;
		 
		public CasGappedReferenceDataStoreImpl(
				DataStore<NucleotideSequence> delegate,
				Map<Long, String> refIndexToIdMap) {
			this.delegate = delegate;
			this.refIndexToIdMap = refIndexToIdMap;
			Id2IndexMap = new HashMap<String, Long>(refIndexToIdMap.size());
			for(Entry<Long, String> entry : refIndexToIdMap.entrySet()){
				Id2IndexMap.put(entry.getValue(), entry.getKey());
			}
		}

		@Override
		public Long getIndexById(String id) {
			return Id2IndexMap.get(id);
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
		public void close() throws IOException {
			delegate.close();
			
		}

		@Override
		public NucleotideSequence getReferenceByIndex(long index) throws DataStoreException {
			return get(getIdByIndex(index));
		}

		@Override
		public String getIdByIndex(long index) {
			return refIndexToIdMap.get(Long.valueOf(index));
		}
		 
	 }
}
