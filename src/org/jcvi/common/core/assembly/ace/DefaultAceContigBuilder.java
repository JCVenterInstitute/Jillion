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
 * Created on Feb 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AbstractContig;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapUtil;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.slice.CompactedSlice;
import org.jcvi.common.core.assembly.util.slice.QualityValueStrategy;
import org.jcvi.common.core.assembly.util.slice.Slice;
import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;
/**
 * {@code AceContigBuilder} is a {@link Builder}
 * for {@link AceContig}s that allows
 * creating a contig object read by read. by adding placed reads
 * and setting a consensus.  An {@link AceContigBuilder}
 * can be used to create AceContig objects that 
 * have been created by an assembler or can be used
 * to create contigs from the imagination.
 * There are additional methods to allow
 * the contig consensus or underlying
 * reads to be modified before
 * the creation of the {@link AceContig} instance
 * (which is immutable).
 * @author dkatzel
 *
 *
 */
public final class  DefaultAceContigBuilder implements AceContigBuilder{

	
   
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param consensus the initial contig consensus for this contig (may be changed later)
     * @return a new {@link AceContigBuilder} instance; never null.
     * @throws NullPointerException if contigId or consensus are null.
     */
    public static DefaultAceContigBuilder createBuilder(String contigId, String consensus){
        return new DefaultAceContigBuilder(contigId, consensus);
    }
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param consensus the initial contig consensus for this contig (may be changed later)
     * @return a new {@link AceContigBuilder} instance; never null.
     * @throws NullPointerException if contigId or consensus are null.
     */
    public static DefaultAceContigBuilder createBuilder(String contigId, NucleotideSequence consensus){
        return new DefaultAceContigBuilder(contigId, consensus);
    }
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
    	
    	private QualityValueStrategy qualityValueStrategy=null;
    	
        private NucleotideSequence initialConsensus;
        private final NucleotideSequenceBuilder mutableConsensus;
        private String contigId;
        private final Map<String, AceAssembledReadBuilder>aceReadBuilderMap = new HashMap<String, AceAssembledReadBuilder>();
        private int contigLeft= -1;
        private int contigRight = -1;
        private volatile boolean built=false;
        private boolean complemented=false;
        /**
         * Create a new {@link DefaultAceContigBuilder} for a contig with the given
         * contig id and starting with the given consensus.  Both the contig id
         * and the consensus can be changed by calling methods on the returned
         * builder.
         * @param contigId the initial contig id to use for this contig (may later be changed)
         * @param initialConsensus the initial contig consensus for this contig (may be changed later)
         * @return a new {@link DefaultAceContigBuilder} instance; never null.
         * @throws NullPointerException if contigId or consensus are null.
         */
        public DefaultAceContigBuilder(String contigId, String initialConsensus){
           this(contigId,                   
        		   new NucleotideSequenceBuilder(initialConsensus).build()
            );
        }
        /**
         * Create a new {@link DefaultAceContigBuilder} for a contig with the given
         * contig id and starting with the given consensus.  Both the contig id
         * and the consensus can be changed by calling methods on the returned
         * builder.
         * @param contigId the initial contig id to use for this contig (may later be changed)
         * @param initialConsensus the initial contig consensus for this contig (may be changed later)
         * @return a new {@link DefaultAceContigBuilder} instance; never null.
         * @throws NullPointerException if contigId or consensus are null.
         */
        public DefaultAceContigBuilder(String contigId, NucleotideSequence initialConsensus){
            if(contigId ==null){
                throw new NullPointerException("contig id can not be null");
            }
            if(initialConsensus ==null){
                throw new NullPointerException("consensus can not be null");
            }
        	this.initialConsensus = initialConsensus;
        	 this.contigId = contigId;
        	 this.mutableConsensus = new NucleotideSequenceBuilder(initialConsensus);
        }
        /**
         * Set this contig as being complemented.
         * This does not actually modify either the consensus
         * or underlying read sequences.  If you wish
         * to actually reverse complement the basecalls
         * you must use call {@link NucleotideSequenceBuilder#reverseComplement()}
         * method on the consensus builder returned by {@link #getConsensusBuilder()}
         * and on each underlying read using {@link #getAllAssembledReadBuilders()}.
         * @param complemented is this ace contig complemented or not.
         * @return this
         */
        @Override
        public DefaultAceContigBuilder setComplemented(boolean complemented){
            this.complemented = complemented;
            return this;
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
        public DefaultAceContigBuilder recallConsensus(ConsensusCaller consensusCaller, 
        		QualitySequenceDataStore qualityDataStore,
        		QualityValueStrategy qualityValueStrategy){
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
        public DefaultAceContigBuilder recallConsensus(ConsensusCaller consensusCaller){
        	if(consensusCaller ==null){
        		throw new NullPointerException("consensus caller can not be null");
        	}
        	this.consensusCaller=consensusCaller;
        	return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAceContigBuilder setContigId(String contigId){
            if(contigId==null){
                throw new NullPointerException("contig id can not be null");
            }
            this.contigId = contigId;
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
        public int numberOfReads(){
            return aceReadBuilderMap.size();
        }
        
        
        @Override
        public DefaultAceContigBuilder addRead(AceAssembledRead acePlacedRead) {
         return addRead(acePlacedRead.getId(),
        		 acePlacedRead.getNucleotideSequence(),
        		 (int)acePlacedRead.getGappedStartOffset(),
        		 acePlacedRead.getDirection(),
        		 acePlacedRead.getReadInfo().getValidRange(),
        		 acePlacedRead.getPhdInfo(),
        		 acePlacedRead.getReadInfo().getUngappedFullLength());
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAceContigBuilder addAllReads(Iterable<AceAssembledRead> reads){
            for(AceAssembledRead read : reads){
                addRead(read);
            }
            return this;
        }
    	/**
        * {@inheritDoc}
        */
    	@Override
        public Collection<AceAssembledReadBuilder> getAllAssembledReadBuilders(){
    	    return aceReadBuilderMap.values();
    	}
        /**
        * {@inheritDoc}
        */
        @Override
        public AceAssembledReadBuilder getAssembledReadBuilder(String readId){
            return aceReadBuilderMap.get(readId);
        }
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAceContigBuilder removeRead(String readId) {
            if(readId==null){
                throw new NullPointerException("read id can not be null");
            }
            aceReadBuilderMap.remove(readId);
            return this;
        }
        
        /**
         * Add a read to this contig with the given values.  This read
         * can later get modified via the {@link #getAssembledReadBuilder(String)}.
         * The read to be added must be fully contained 
         * @param readId the id this read should have
         * @param validBases the gapped bases of this read that align (however well/badly)
         * to this contig and will be used as underlying sequence data for this contig.
         * @param offset the gapped start offset of this read into the contig
         * consensus.
         * @param dir the {@link Direction} of this read.
         * @param clearRange the ungapped clear range of the valid bases
         * relative to the full length non-trimmed raw full length
         * read from the sequence machine.
         * @param phdInfo the {@link PhdInfo} object for this read.
         * @param ungappedFullLength the ungapped full length
         * non-trimmed raw full length
         * read from the sequence machine.
         * @return this.
         */
        @Override
        public DefaultAceContigBuilder addRead(String readId, NucleotideSequence validBases, int offset,
                Direction dir, Range clearRange,PhdInfo phdInfo,int ungappedFullLength) {
            //contig left (and right) might be beyond consensus depending on how
            //trimmed the data is and what assembly/consensus caller is used.
            //force contig left and right to be within the called consensus
            //BCISD-211
            int correctedOffset = Math.max(0,offset);
            adjustContigLeftAndRight(validBases, correctedOffset);
            AceAssembledReadBuilder aceReadBuilder = createNewAceReadBuilder(readId, validBases, correctedOffset, dir, 
                        clearRange,phdInfo,ungappedFullLength);
                
                
                aceReadBuilderMap.put(readId,aceReadBuilder);
            
            return this;
        }
        private AceAssembledReadBuilder createNewAceReadBuilder(
                String readId, NucleotideSequence validBases, int offset,
                Direction dir, Range clearRange, PhdInfo phdInfo,int ungappedFullLength) {
            return DefaultAceAssembledRead.createBuilder(
                    initialConsensus,readId,
                    validBases,
                    offset,dir,clearRange,phdInfo,ungappedFullLength);
        }
        private void adjustContigLeftAndRight(NucleotideSequence validBases, int offset) {
            adjustContigLeft(offset);
            adjustContigRight(validBases, offset);
        }
        private void adjustContigRight(NucleotideSequence validBases, int offset) {
            final int endOfNewRead = offset+ (int)validBases.getLength()-1;
            if(endOfNewRead <= initialConsensus.getLength() && (contigRight ==-1 || endOfNewRead > contigRight)){
                contigRight = endOfNewRead ;
            }
        }
        private void adjustContigLeft(int offset) {
            
            if(contigLeft ==-1 || offset <contigLeft){
                contigLeft = offset;
            }
        }
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceBuilder getConsensusBuilder() {
            return mutableConsensus;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public AceContig build(){
             if(built){
                 throw new IllegalStateException("this contig has already been built");
             }
             
            if(numberOfReads()==0){
                //force empty contig if no reads...
            	 built=true;
                return new DefaultAceContigImpl(contigId, new NucleotideSequenceBuilder().build(),Collections.<AceAssembledRead>emptySet(),complemented);
            }
            if(consensusCaller !=null){
				recallConsensusNow();
            }
            SortedSet<AceAssembledRead> placedReads = new TreeSet<AceAssembledRead>(ConsedReadComparator.INSTANCE);
            //contig left (and right) might be beyond consensus depending on how
            //trimmed the data is and what assembler/consensus caller is used.
            //force contig left and right to be within the called consensus
            //for example, reads that have negative start offsets
            //not AF records with negative values; actual negative alignment start offsets!
            //BCISD-211            
            contigLeft = Math.max(contigLeft, 0);
            contigRight = Math.min(contigRight,(int)mutableConsensus.getLength()-1);
            //here only include the gapped valid range consensus bases
            //throw away the rest            
            NucleotideSequence validConsensus = mutableConsensus
            		.copy()
            		.trim(Range.of(contigLeft, contigRight))
            		.build();
            for(AceAssembledReadBuilder aceReadBuilder : aceReadBuilderMap.values()){
                int newOffset = (int)aceReadBuilder.getBegin() - contigLeft;
                aceReadBuilder.reference(validConsensus,newOffset);
                placedReads.add(aceReadBuilder.build());                
            } 
            built=true;
            aceReadBuilderMap.clear();
            initialConsensus = null;
            
            return new DefaultAceContigImpl(contigId, validConsensus,placedReads,complemented);
        }
    /**
     * Recompute the contig
     * consensus now using the current reads in the contig
     * using the {@link ConsensusCaller} and optional quality data
     * that was set by
     * {@link #recallConsensus(ConsensusCaller)} or
     * {@link #recallConsensus(ConsensusCaller, QualitySequenceDataStore, QualityValueStrategy)}.
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
     * {@link #recallConsensus(ConsensusCaller, QualitySequenceDataStore, QualityValueStrategy)}.
     * @see #build()
     */
    public DefaultAceContigBuilder recallConsensusNow() {
    	if(consensusCaller==null){
    		throw new IllegalStateException("must set consensus caller");
    	}
    	CompactedSlice.Builder builders[] = new CompactedSlice.Builder[(int)mutableConsensus.getLength()];
    	
    	for(AceAssembledReadBuilder aceReadBuilder : aceReadBuilderMap.values()){
    		int start = (int)aceReadBuilder.getBegin();
			int i=0;
			String id =aceReadBuilder.getId();
			Direction dir = aceReadBuilder.getDirection();
			QualitySequence fullQualities =null;
			AceAssembledRead tempRead=null;
			if(qualityDataStore!=null){
				try {
					fullQualities = qualityDataStore.get(id);
				} catch (DataStoreException e) {
					throw new IllegalStateException("error recalling consensus",e);
				}
				//should be able to call build multiple times
				tempRead = aceReadBuilder.build();
    			if(fullQualities ==null){
    				throw new NullPointerException("could not get qualities for "+id);
    			}
			}
			
			
			for(Nucleotide base : aceReadBuilder.getCurrentNucleotideSequence()){
				
				final PhredQuality quality;
				if(fullQualities==null){
					quality = DEFAULT_QUALITY;
				}else{					
					quality= qualityValueStrategy.getQualityFor(tempRead, fullQualities, i);
				}
				if(builders[start+i] ==null){
					builders[start+i] = new CompactedSlice.Builder();
				}
				builders[start+i].addSliceElement(id, base, quality, dir);
				i++;
			}
    	}
    	for(int i=0; i<builders.length; i++){
    		Slice<?> slice = builders[i].build();            
    		mutableConsensus.replace(i,consensusCaller.callConsensus(slice).getConsensus());
    	}
    	return this;
	}
    
    public NucleotideSequenceBuilder getReadSequenceBuilder(String readId){
    	AceAssembledReadBuilder assembledReadBuilder = getAssembledReadBuilder(readId);
		if(assembledReadBuilder==null){
			throw new IllegalArgumentException(
    				String.format("read %s is not in contig", readId));
		}
    	return assembledReadBuilder.getNucleotideSequenceBuilder();
    }
    
    public DefaultAceContigBuilder shiftRead(String readId, int numberOfBases){
    	AceAssembledReadBuilder readBuilder =getAssembledReadBuilder(readId);
    	if(readBuilder==null){
    		throw new IllegalArgumentException(
    				String.format("read %s is not in contig", readId));
    	}
    	long oldReadEnd = readBuilder.getEnd();
    	long newReadEnd = oldReadEnd + numberOfBases;
    	long newReadBegin = readBuilder.getBegin()+numberOfBases;
    	if(newReadBegin<0 || newReadEnd>=mutableConsensus.getLength()){
    		throw new IllegalArgumentException(
    				String.format("shifting read %s by %d will extend beyond consensus",readId, numberOfBases));
    	}
    	readBuilder.shift(numberOfBases);
    	return this;
    }
    
    /**
     * Split the contents of the current ContigBuilder into possibly multiple
     * new ContigBuilders.  The returned ContigBuilders will be new
     * instances which only contain the reads and consensus of the initial
     * contig that intersects the input rangesToKeep.
     * @param rangesToKeep The {@link Range}s of the contig to make into new
     * contigs.  For each range given, a new ContigBuilder instance is created
     * which contains only the reads and portion of the consensus sequence
     * that intersects that range in the original contig.  If a read
     * extends beyond the input range, then that read sequence is trimmed.
     * If a read spans multiple input ranges, then it will exist (but trimmed)
     * in multiple returned contig Builders.  If the given ranges to keep are overlapping, then the returned
     * contigBuilders will also be overlapping.  Each returned contigBuilder will have all
     * have the same contig id this contigBuilder. 
     * <strong>It is up to the client to modify or remove reads and change the returned
     * contig ids so that the final contigs meet the desired 
     * uniqueness constraints </strong>   
     * @return a new Map, sorted by Range arrival, each entry value in the map is a new contigBuilder
     * which contains only the portion of this contig at that particular entry key range.
     */
    public SortedMap<Range, DefaultAceContigBuilder> split(Collection<Range> rangesToKeep){
    	SortedMap<Range, DefaultAceContigBuilder> splitContigs = new TreeMap<Range, DefaultAceContigBuilder>(Range.Comparators.ARRIVAL);
    	
    	CoverageMap<AceAssembledReadBuilder> coverageMap = CoverageMapFactory.create(aceReadBuilderMap.values());
    	
    	for(Range rangeTokeep :rangesToKeep){
    		
            NucleotideSequence contigConsensus =mutableConsensus
            									.copy()
												.trim(rangeTokeep)
												.build();
            DefaultAceContigBuilder splitContig = new DefaultAceContigBuilder(contigId, contigConsensus);
            Set<String> contigReads = new HashSet<String>();            
            for(CoverageRegion<AceAssembledReadBuilder> region : CoverageMapUtil.getRegionsWhichIntersect(coverageMap, rangeTokeep)){
                for(AceAssembledReadBuilder read : region){
                    contigReads.add(read.getId());
                }
            }
            for(String readId : contigReads){
            	AceAssembledReadBuilder readBuilder = aceReadBuilderMap.get(readId);
            	Range readTrimRange = new Range.Builder(readBuilder.asRange().intersection(rangeTokeep))
            						.shift(-readBuilder.getBegin()) //adjust trim range to be relative of read start
            						.build();
            	NucleotideSequence trimmedBases = readBuilder.getNucleotideSequenceBuilder()
            							.copy()
            							.trim(readTrimRange)
            							.build();
            	splitContig.addRead(readId, 
            			trimmedBases, 
            			(int)(readBuilder.getBegin() - rangeTokeep.getBegin()), 
            			readBuilder.getDirection(), 
            			readBuilder.getClearRange(), 
            			readBuilder.getPhdInfo(), 
            			readBuilder.getUngappedFullLength());
            }
            splitContigs.put(rangeTokeep, splitContig);
    	}
    	
    	return splitContigs;
    }

	/**
     * Comparator singleton that sorts reads like consed does when outputing ace files.
     * @author dkatzel
     *
     */
    private static enum ConsedReadComparator implements Comparator<AceAssembledRead>{
		INSTANCE;
		
		@Override
		public int compare(AceAssembledRead o1, AceAssembledRead o2) {
			int comp= Range.Comparators.ARRIVAL.compare(o1.asRange(),o2.asRange());
			if(comp!=0){
				return comp;
			}
			//ranges the same order by id
			return o1.getId().compareTo(o2.getId());
		}

	}
    
    private static final class  DefaultAceContigImpl extends AbstractContig<AceAssembledRead> implements AceContig{

    	
        private final boolean complemented;

        private DefaultAceContigImpl(String id, NucleotideSequence consensus,
                Set<AceAssembledRead> reads,boolean complemented) {
            super(id, consensus, reads);
            this.complemented = complemented;
        }
       
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isComplemented() {
            return complemented;
        }



        @Override
    	public int hashCode() {
    		final int prime = 31;
    		int result = super.hashCode();
    		result = prime * result + (complemented ? 1231 : 1237);
    		return result;
    	}
    	@Override
    	public boolean equals(Object obj) {
    		if (this == obj) {
    			return true;
    		}
    		if (!super.equals(obj)) {
    			return false;
    		}
    		if (!(obj instanceof DefaultAceContigBuilder)) {
    			return false;
    		}
    		DefaultAceContigBuilder other = (DefaultAceContigBuilder) obj;
    		if (complemented != other.complemented) {
    			return false;
    		}
    		return true;
    	}
    }

}
