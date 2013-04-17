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
package org.jcvi.jillion.internal.assembly;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
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
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.assembly.util.CompactedSliceMap;

/**
 * {@code AbstractContigBuilder} implements the
 * {@link ContigBuilder} interface
 * 
 * @author dkatzel
 *
 *
 */
public abstract class AbstractContigBuilder<P extends AssembledRead, C extends Contig<P>> implements ContigBuilder<P,C>{
	    
		/**
	     * default quality value that every basecall will get
	     * if consensus caller is used to recall the consensus
	     * but no {@link QualitySequenceDataStore} is given.
	     */
	    private static final PhredQuality DEFAULT_QUALITY = PhredQuality.valueOf(30);
		
		private final NucleotideSequenceBuilder consensus;
		
        private String id;
        private final Map<String, AssembledReadBuilder<P>> reads;
        
        
      
    	/**
    	 * {@link ConsensusCaller} used to update the
    	 * consensus during {@link #build()}.  If set to {@code null},
    	 * then no recalling is to be done (null by default).
    	 */
    	protected ConsensusCaller consensusCaller =null;
    	/**
    	 * {@link QualitySequenceDataStore} used during
    	 * consensus recalling.  Set to null if 
    	 * no recalling is to be done. (null by default).
    	 */
    	private QualitySequenceDataStore qualityDataStore =null;
    	
    	private GapQualityValueStrategy qualityValueStrategy=null;
        /**
         * Create a new Builder instance with the given id and consensus.
         * @param id can not be null.
         * @param consensus can not be null.
         * @throws NullPointerException if either id or consensus
         * are null.
         */
        protected AbstractContigBuilder(String id, NucleotideSequence consensus){
        	if(id==null){
        		throw new NullPointerException("id can not be null");
        	}
        	if(consensus==null){
        		throw new NullPointerException("consensus can not be null");
        	}
            this.id = id;
            this.consensus = new NucleotideSequenceBuilder(consensus);
            reads = new LinkedHashMap<String,AssembledReadBuilder<P>>();
        }
        public AbstractContigBuilder<P,C> addRead(String id, int offset,Range validRange, String basecalls, Direction dir, int fullUngappedLength){
            reads.put(id, createPlacedReadBuilder(id,offset,validRange,basecalls,dir,fullUngappedLength));
            return this;
        }
        public  AbstractContigBuilder<P,C>  addRead(P read){
            reads.put(read.getId(),createPlacedReadBuilder(read));
            return this;
        }
        protected abstract AssembledReadBuilder<P> createPlacedReadBuilder(P read);
        protected abstract AssembledReadBuilder<P> createPlacedReadBuilder(String id, int offset,Range validRange, String basecalls, Direction dir, int fullUngappedLength);

        
        /**
         * Recall the consensus using the given
         * {@link ConsensusCaller} and {@link QualitySequenceDataStore}
         * which contains the quality data for all of the reads in this contig.
         * The consensus will get recalled inside the {@link #build()}
         * and {@link #recallConsensusNow()}.
         * method before the {@link Contig} instance is created.
         * @param consensusCaller the {@link ConsensusCaller}  instance to use
         * to recall the consensus of this contig; can not be null.
         * @param qualityDataStore the {@link QualitySequenceDataStore}
         * which contains all the quality data for all of the reads
         * in this contig; can not be null.
         * @return this.
         * @throws NullPointerException if any parameter is null.
         */
        @Override
        public ContigBuilder<P, C> recallConsensus(ConsensusCaller consensusCaller, 
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
         * method before the {@link Contig} instance is created.
         * @param consensusCaller the {@link ConsensusCaller}  instance to use
         * to recall the consensus of this contig; can not be null.
         * @return this.
         * @throws NullPointerException if any parameter is null.
         */
        public ContigBuilder<P, C> recallConsensus(ConsensusCaller consensusCaller){
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
        public ContigBuilder<P, C> recallConsensusNow() {
        	if(consensusCaller==null){
        		throw new IllegalStateException("must set consensus caller");
        	}
        	
        	final SliceMap sliceMap;
        	try{
        	if(qualityDataStore==null){
        		sliceMap= CompactedSliceMap.create(createStreamingReadIterator(),
        			(int)consensus.getLength(),DEFAULT_QUALITY, qualityValueStrategy);
        	}else{
        		sliceMap= CompactedSliceMap.create(createStreamingReadIterator(),
            			(int)consensus.getLength(),qualityDataStore, qualityValueStrategy);
        	}
        	}catch(DataStoreException e){
        		throw new IllegalStateException("error getting quality values from datastore",e);
        	}
        	
        	for(int i=0; i<sliceMap.getSize(); i++){
        		Slice slice = sliceMap.getSlice(i);
        		if(slice.getCoverageDepth() !=0){
        			consensus.replace(i,consensusCaller.callConsensus(slice).getConsensus());
        		}
        	}
        	
        	return this;
    	}
        
        
        private StreamingIterator<P> createStreamingReadIterator(){
        	
        	return IteratorUtil.createStreamingIterator(new Iterator<P>() {
        		Iterator<AssembledReadBuilder<P>> builderIterator = reads.values().iterator();
				@Override
				public boolean hasNext() {
					return builderIterator.hasNext();
				}

				@Override
				public P next() {
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
         * 
        * {@inheritDoc}
         */
        @Override
        public abstract C build();
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<P, C> setContigId(String contigId) {
            id = contigId;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String getContigId() {
            return id;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public int numberOfReads() {
            return reads.size();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<P, C> addAllReads(Iterable<P> reads) {
            for(P read : reads){
                addRead(read);
            }
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Collection<? extends AssembledReadBuilder<P>> getAllAssembledReadBuilders() {
            return reads.values();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public AssembledReadBuilder<P> getAssembledReadBuilder(String readId) {
            return reads.get(readId);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<P, C> removeRead(String readId) {
            reads.remove(readId);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceBuilder getConsensusBuilder() {
            return consensus;
        }
        
        
   
}
