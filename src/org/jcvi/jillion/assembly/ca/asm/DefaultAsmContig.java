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
package org.jcvi.jillion.assembly.ca.asm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.ContigBuilder;
import org.jcvi.jillion.assembly.util.GapQualityValueStrategy;
import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceMap;
import org.jcvi.jillion.assembly.util.consensus.ConsensusCaller;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.jcvi.jillion.internal.assembly.util.CompactedSliceMap;

/**
 * @author dkatzel
 *
 *
 */
final class DefaultAsmContig implements AsmContig{

    private final boolean isDegenerate;
    private final Contig<AsmAssembledRead> contig;
    public static AsmContigBuilder createBuilder(String id, NucleotideSequence consensus){
        return createBuilder(id,consensus,false);
    }
    public static AsmContigBuilder createBuilder(String id, NucleotideSequence consensus, boolean isDegenerate){
        return new DefaultAsmContigBuilder(id, consensus, isDegenerate);
    }

    
    private DefaultAsmContig(String id, NucleotideSequence consensus,
            Set<AsmAssembledRead> reads,boolean isDegenerate) {
        contig = new DefaultContig<AsmAssembledRead>(id, consensus, reads);
        this.isDegenerate = isDegenerate;
    }
   
 



	@Override
	public String getId() {
		return contig.getId();
	}



	@Override
	public long getNumberOfReads() {
		return contig.getNumberOfReads();
	}



	@Override
	public NucleotideSequence getConsensusSequence() {
		return contig.getConsensusSequence();
	}



	@Override
	public AsmAssembledRead getRead(String id) {
		return contig.getRead(id);
	}



	@Override
	public boolean containsRead(String readId) {
		return contig.containsRead(readId);
	}



	@Override
	public StreamingIterator<AsmAssembledRead> getReadIterator() {
		return contig.getReadIterator();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isDegenerate ? 1231 : 1237);
		result = prime * result + contig.hashCode();
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AsmContig)) {
			return false;
		}
		AsmContig other = (AsmContig) obj;
		if (isDegenerate != other.isDegenerate()) {
			return false;
		}
		if (!contig.getId().equals(other.getId())) {
			return false;
		}
		if (!contig.getConsensusSequence().equals(other.getConsensusSequence())) {
			return false;
		}
		if (contig.getNumberOfReads()!=other.getNumberOfReads()) {
			return false;
		}
		StreamingIterator<AsmAssembledRead> readIter=null;
		try{
			readIter = contig.getReadIterator();
			while(readIter.hasNext()){
				AsmAssembledRead read = readIter.next();
				String readId = read.getId();
				if(!other.containsRead(readId)){
					return false;
				}
				if(!read.equals(other.getRead(readId))){
					return false;
				}
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(readIter);
		}			
		return true;
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isDegenerate() {
        return isDegenerate;
    }

    private static class DefaultAsmContigBuilder implements AsmContigBuilder{

    	/**
         * default quality value that every basecall will get
         * if consensus caller is used to recall the consensus
         * but no {@link QualitySequenceDataStore} is given.
         */
        private static final PhredQuality DEFAULT_QUALITY = PhredQuality.valueOf(30);
    	/**
    	 * {@link ConsensusCaller} used to update the
    	 * consensus during {@link #build()}.  If set to {@code null},
    	 * then no recalling is to be done (null by default).
    	 */
    	private ConsensusCaller consensusCaller =null;
    	/**
    	 * {@link QualitySequenceDataStore} used during
    	 * consensus recalling.  Set to null if 
    	 * no recalling is to be done. (null by default).
    	 */
    	private QualitySequenceDataStore qualityDataStore =null;
    	
    	private GapQualityValueStrategy qualityValueStrategy=null;
    	
    	
        private final NucleotideSequence fullConsensus;
        private final NucleotideSequenceBuilder mutableConsensus;
        private String contigId;
        private final Map<String, AsmAssembledReadBuilder>asmReadBuilderMap = new HashMap<String, AsmAssembledReadBuilder>();
   
        boolean isDegenerate;
        DefaultAsmContigBuilder(String id, NucleotideSequence consensus,boolean isDegenerate){
            this.contigId = id;
            this.fullConsensus = consensus;
            this.mutableConsensus = new NucleotideSequenceBuilder(fullConsensus);
            this.isDegenerate = isDegenerate;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmAssembledRead, AsmContig> setContigId(String contigId) {
            this.contigId =contigId;
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String getContigId() {
            return contigId;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int numberOfReads() {
            return asmReadBuilderMap.size();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmAssembledRead, AsmContig> addRead(
                AsmAssembledRead placedRead) {
            return addRead(placedRead.getId(),
                    placedRead.getNucleotideSequence().toString(),
                    (int)placedRead.getGappedStartOffset(),
                    placedRead.getDirection(),
                    placedRead.getReadInfo().getValidRange(),
                    placedRead.getReadInfo().getUngappedFullLength(),
                    placedRead.isRepeatSurrogate());
        }
        

         /**
          * {@inheritDoc}
          */
          @Override
          public AsmContigBuilder addRead(String readId, String validBases,
                  int offset, Direction dir, Range clearRange,
                  int ungappedFullLength, boolean isSurrogate) {
              asmReadBuilderMap.put(readId, DefaultAsmAssembledRead.createBuilder(
                      this.fullConsensus, readId, validBases, offset, dir, clearRange, ungappedFullLength, isSurrogate));
              return this;
          }
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmAssembledRead, AsmContig> addAllReads(
                Iterable<AsmAssembledRead> reads) {
           for(AsmAssembledRead read : reads){
               addRead(read);
           }
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Collection<? extends AssembledReadBuilder<AsmAssembledRead>> getAllAssembledReadBuilders() {
           
            return asmReadBuilderMap.values();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AssembledReadBuilder<AsmAssembledRead> getAssembledReadBuilder(String readId) {
            return asmReadBuilderMap.get(readId);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmAssembledRead, AsmContig> removeRead(String readId) {
            asmReadBuilderMap.remove(readId);   
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceBuilder getConsensusBuilder() {
            return mutableConsensus;
        }
        /**
         * Recall the consensus using the given
         * {@link ConsensusCaller} and {@link QualitySequenceDataStore}
         * which contains the quality data for all of the reads in this ace contig.
         * The consensus will get recalled inside the {@link #build()}
         * and {@link #recallConsensusNow()}.
         * method before the {@link AceContig} instance is created.
         * @param consensusCaller the {@link ConsensusCaller}  instance to use
         * to recall the consensus of this contig; can not be null.
         * @param qualityDataStore the {@link QualitySequenceDataStore}
         * which contains all the quality data for all of the reads
         * in this contig; can not be null.
         * @return this.
         * @throws NullPointerException if any parameter is null.
         */
        @Override
        public AsmContigBuilder recallConsensus(ConsensusCaller consensusCaller, 
        		QualitySequenceDataStore qualityDataStore,
        		GapQualityValueStrategy qualityValueStrategy){
        	if(consensusCaller ==null){
        		throw new NullPointerException("consensus caller can not be null");
        	}
        	if(qualityDataStore ==null){
        		throw new NullPointerException("quality datastore can not be null");
        	}
        	this.consensusCaller=consensusCaller;
        	this.qualityDataStore = qualityDataStore;
        	this.qualityValueStrategy = qualityValueStrategy;
        	return this;
        }
        /**
         * Recall the consensus using the given
         * {@link ConsensusCaller} using faked quality data
         * where all basecalls (and gaps) all get the same quality value.
         * The consensus will get recalled inside the {@link #build()}
         * and from {@link #recallConsensusNow()}.
         * method before the {@link AceContig} instance is created.
         * @param consensusCaller the {@link ConsensusCaller}  instance to use
         * to recall the consensus of this contig; can not be null.
         * @return this.
         * @throws NullPointerException if any parameter is null.
         */
        @Override
        public AsmContigBuilder recallConsensus(ConsensusCaller consensusCaller){
        	if(consensusCaller ==null){
        		throw new NullPointerException("consensus caller can not be null");
        	}
        	this.consensusCaller=consensusCaller;
        	this.qualityDataStore = null;
        	this.qualityValueStrategy = null;
        	return this;
        }
        
        
        /**
         * Recompute the contig
         * consensus now using the current reads in the contig
         * using the {@link ConsensusCaller} and optional quality data
         * that was set by
         * {@link #recallConsensus(ConsensusCaller)} or
         * {@link #recallConsensus(ConsensusCaller, QualitySequenceDataStore, GapQualityValueStrategy)}.
         * Only regions of the contig that have read coverage 
         * get recalled.  The Consensus of "0x" regions
         * remains unchanged.
         * 
         * If this method is called without first
         * setting a {@link ConsensusCaller}, then this
         * method will throw an {@link IllegalStateException}.
         * <p/>
         * Recomputing the contig consensus may be computationally
         * expensive and time consuming.  So this method
         * should not be called on a regular basis.
         * Also, the contig consensus will always
         * get recalled during {@link #build()}
         * even if this method has already been called
         * since it is too hard to track if any underlying read changes occurred
         * in between.
         * @return this.
         * @throws IllegalStateException if a consensus caller
         * was not first set using {@link #recallConsensus(ConsensusCaller)} or
         * {@link #recallConsensus(ConsensusCaller, QualitySequenceDataStore, GapQualityValueStrategy)}.
         * @see #build()
         */
        @Override
        public AsmContigBuilder recallConsensusNow() {
        	if(consensusCaller==null){
        		throw new IllegalStateException("must set consensus caller");
        	}
        	
        	final SliceMap sliceMap;
        	try{
        	if(qualityDataStore==null){
        		sliceMap= CompactedSliceMap.create(createStreamingReadIterator(),
        			(int)mutableConsensus.getLength(),DEFAULT_QUALITY, qualityValueStrategy);
        	}else{
        		sliceMap= CompactedSliceMap.create(createStreamingReadIterator(),
            			(int)mutableConsensus.getLength(),qualityDataStore, qualityValueStrategy);
        	}
        	}catch(DataStoreException e){
        		throw new IllegalStateException("error getting quality values from datastore",e);
        	}
        	
        	for(int i=0; i<sliceMap.getSize(); i++){
        		Slice slice = sliceMap.getSlice(i);
        		if(slice.getCoverageDepth() !=0){
        			mutableConsensus.replace(i,consensusCaller.callConsensus(slice).getConsensus());
        		}
        	}
        	
        	return this;
    	}
        
        
        private StreamingIterator<AsmAssembledRead> createStreamingReadIterator(){
        	
        	return IteratorUtil.createStreamingIterator(new Iterator<AsmAssembledRead>() {
        		Iterator<AsmAssembledReadBuilder> builderIterator = asmReadBuilderMap.values().iterator();
				@Override
				public boolean hasNext() {
					return builderIterator.hasNext();
				}

				@Override
				public AsmAssembledRead next() {
					//should be able to call build multiple times
					return builderIterator.next().build();
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();					
				}
        		
			});
        }
        	
        /**
        * {@inheritDoc}
        */
        @Override
        public AsmContig build() {
        	if(consensusCaller !=null){
    			recallConsensusNow();
            }
        	
            Set<AsmAssembledRead> reads = new HashSet<AsmAssembledRead>(asmReadBuilderMap.size()+1);
            for(AsmAssembledReadBuilder builder : asmReadBuilderMap.values()){
                reads.add(builder.build());
            }
            asmReadBuilderMap.clear();
            return new DefaultAsmContig(contigId,mutableConsensus.build(),reads, isDegenerate);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void setDegenerate(boolean isDegenerate) {
            this.isDegenerate = isDegenerate;
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isDegenerate() {
            return isDegenerate;
        }
       
        
    }
}
