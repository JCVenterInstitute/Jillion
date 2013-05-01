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
/*
 * Created on Feb 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.ContigBuilder;
import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageMapBuilder;
import org.jcvi.jillion.assembly.util.CoverageRegion;
import org.jcvi.jillion.assembly.util.GapQualityValueStrategy;
import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceMap;
import org.jcvi.jillion.assembly.util.consensus.ConsensusCaller;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.jcvi.jillion.internal.assembly.util.CompactedSliceMap;
/**
 * {@code AceContigBuilder} is a {@link Builder}
 * for {@link AceContig}s that allows
 * creating {@link AceContig} objects read by read by adding assembled reads
 * and setting a consensus.  An {@link AceContigBuilder}
 * can be used to create AceContig objects that 
 * have been created by an assembler or can be used
 * to create contigs from "scratch".
 * There are additional methods to allow
 * the contig consensus or underlying
 * reads to be modified before
 * the creation of the {@link AceContig} instance
 * (which is immutable).
 * <p/>
 * This class is not thread-safe.
 * @author dkatzel
 *
 *
 */
public final class  AceContigBuilder implements ContigBuilder<AceAssembledRead,AceContig>{
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
	
    private NucleotideSequence initialConsensus;
    private final NucleotideSequenceBuilder mutableConsensus;
    private String contigId;
    private final Map<String, AceAssembledReadBuilder> aceReadBuilderMap;
    private int contigLeft= -1;
    private int contigRight = -1;
    private volatile boolean built=false;
    private boolean complemented=false;
    private boolean computeConsensusQualities=false;
    
    private QualitySequenceBuilder mutableConsensusQualities;
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param initialConsensus the initial contig consensus for this contig (may be changed later)
     * @throws NullPointerException if contigId or consensus are null.
     */
    public AceContigBuilder(String contigId, String initialConsensus){
       this(contigId,                   
    		   new NucleotideSequenceBuilder(initialConsensus).build()
        );
    }
    
    public AceContigBuilder setInitialConsensusQualities(QualitySequence consensusQualities){
    	this.mutableConsensusQualities = new QualitySequenceBuilder(consensusQualities);
    	return this;
    }
    
    private QualitySequenceBuilder createDefaultQualitySequenceBuilder(){
    	int ungappedConsensusLength = (int)mutableConsensus.getUngappedLength();
    	byte[] quals = new byte[ungappedConsensusLength];
    	Arrays.fill(quals, DEFAULT_QUALITY.getQualityScore());
    	return new QualitySequenceBuilder(quals);
    }
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param initialConsensus the initial contig consensus for this contig (may be changed later)
     * @throws NullPointerException if contigId or consensus are null.
     */
    public AceContigBuilder(String contigId, NucleotideSequence initialConsensus){
        if(contigId ==null){
            throw new NullPointerException("contig id can not be null");
        }
        if(initialConsensus ==null){
            throw new NullPointerException("consensus can not be null");
        }
    	this.initialConsensus = initialConsensus;
    	 this.contigId = contigId;
    	 this.mutableConsensus = new NucleotideSequenceBuilder(initialConsensus);
    	 aceReadBuilderMap = new HashMap<String, AceAssembledReadBuilder>();
    }
    
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param initialConsensus the initial contig consensus for this contig (may be changed later)
     * @param estimatedNumberOfReads expected number of reads that will be added to this
     * contig.  This value is only used to allocate the initial map sizes for internal
     * data structures as a performance optimization.  Must be >=0.
     * 
     * @throws NullPointerException if contigId or consensus are null.
     * @throws IllegalArgumentException if estimatedNumberOfReads
     * is <0
     */
    public AceContigBuilder(String contigId, NucleotideSequence initialConsensus,
    		int estimatedNumberOfReads){
        if(contigId ==null){
            throw new NullPointerException("contig id can not be null");
        }
        if(initialConsensus ==null){
            throw new NullPointerException("consensus can not be null");
        }
    	this.initialConsensus = initialConsensus;
    	 this.contigId = contigId;
    	 this.mutableConsensus = new NucleotideSequenceBuilder(initialConsensus);
    	 int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(estimatedNumberOfReads);
    	 aceReadBuilderMap = new HashMap<String, AceAssembledReadBuilder>(capacity);
    }
    /**
     * Create a new instance of DefaultAceContigBuilder
     * whose initial state is an exact copy
     * of the given AceContig.
     * @param copy the {@link AceContig} to copy
     * can not be null.
     * @throws NullPointerException if copy is null.
     */
    public AceContigBuilder(AceContig copy){
    	this.contigId=copy.getId();
    	this.initialConsensus = copy.getConsensusSequence();
    	this.mutableConsensus = new NucleotideSequenceBuilder(initialConsensus);
    	aceReadBuilderMap = new HashMap<String, AceAssembledReadBuilder>(MapUtil.computeMinHashMapSizeWithoutRehashing(copy.getNumberOfReads()));
    	StreamingIterator<AceAssembledRead> readIter =null;
    	try{
    		readIter = copy.getReadIterator();
    		while(readIter.hasNext()){
    			this.addRead(readIter.next());
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(readIter);
    	}
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
    public AceContigBuilder setComplemented(boolean complemented){
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
     * in this contig; can not be null. This value will override
     * the quality datastore set by {@link #computeConsensusQualities(QualitySequenceDataStore)}
     * and vice versa, last one called wins. (This is more for user's
     * convenience in case they don't want to do one or the other).
     * @return this.
     * @throws NullPointerException if any parameter is null.
     */
    @Override
    public AceContigBuilder recallConsensus(ConsensusCaller consensusCaller, 
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
     * Compute the consensus Qualities using the same
     * algorithm that consed uses.    The read quality values
     * will be taken from the {@link QualitySequenceDataStore} set by
     * {@link #recallConsensus(ConsensusCaller, QualitySequenceDataStore, GapQualityValueStrategy)}.
     * If you do not wish to recall consensus, then use 
     * {@link #computeConsensusQualities(QualitySequenceDataStore)}.
     * If this method is not specified
     * AND {@link #setInitialConsensusQualities(QualitySequence)} is not
     * called, then the consensus qualities will be set to a dummy value
     * of all 30.
     * @return this
     * @see #computeConsensusQualities(QualitySequenceDataStore)
     * 
     */
    public AceContigBuilder computeConsensusQualities(){
    	this.computeConsensusQualities = true;
    	return this;
    }
    /**
     * Compute the consensus Qualities using the same
     * algorithm that consed uses.    The read quality values
     * will be taken from the given {@link QualitySequenceDataStore}.
     * If this method is not specified
     * AND {@link #setInitialConsensusQualities(QualitySequence)} is not
     * called, then the consensus qualities will be set to a dummy value
     * of all 30.
     * @param readQualityDataStore the {@link QualitySequenceDataStore}
     * to use to compute consensus qualities.  This value will override
     * the quality datastore set by {@link #recallConsensus(ConsensusCaller, QualitySequenceDataStore, GapQualityValueStrategy)}
     * and vice versa, last one called wins. (This is more for user's
     * convenience in case they don't want to do one or the other).
     * @return this
     * @throws NullPointerException if readQualityDataStore is null.
     */
    public AceContigBuilder computeConsensusQualities(QualitySequenceDataStore readQualityDataStore){
    	if(readQualityDataStore ==null){
    		throw new NullPointerException("read quality datastore can not be null");
    	}
    	this.computeConsensusQualities = true;
    	
    	this.qualityDataStore = readQualityDataStore;
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
    public AceContigBuilder recallConsensus(ConsensusCaller consensusCaller){
    	if(consensusCaller ==null){
    		throw new NullPointerException("consensus caller can not be null");
    	}
    	this.consensusCaller=consensusCaller;
    	this.qualityDataStore = null;
    	this.qualityValueStrategy = null;
    	return this;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public AceContigBuilder setContigId(String contigId){
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
    public AceContigBuilder addRead(AceAssembledRead acePlacedRead) {
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
    public AceContigBuilder addAllReads(Iterable<AceAssembledRead> reads){
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
    public AceContigBuilder removeRead(String readId) {
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
    public AceContigBuilder addRead(String readId, NucleotideSequence validBases, int offset,
            Direction dir, Range clearRange,PhdInfo phdInfo,int ungappedFullLength) {
        if(readId ==null){
        	throw new NullPointerException("readId can not be null");
        }
    	if(validBases ==null){
    		throw new NullPointerException("valid bases can not be null");
    	}
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
            return new DefaultAceContigImpl(contigId, new NucleotideSequenceBuilder().build(),
            		Collections.<AceAssembledRead>emptySet(),complemented,
            		new QualitySequenceBuilder().build());
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
        Range contigTrimRange = Range.of(contigLeft, contigRight);
		NucleotideSequence validConsensus = mutableConsensus
        		.copy()
        		.trim(contigTrimRange)
        		.build();
		
        for(AceAssembledReadBuilder aceReadBuilder : aceReadBuilderMap.values()){
        	int newOffset = (int)aceReadBuilder.getBegin() - contigLeft;
            aceReadBuilder.reference(validConsensus,newOffset);
            placedReads.add(aceReadBuilder.build());                
        } 
       
        if(mutableConsensusQualities ==null){
        	//no consensus qualities set
        	if(computeConsensusQualities){
        		if(qualityDataStore ==null){
        			throw new NullPointerException("quality datastore can not be null");
        		}
        		try {
					mutableConsensusQualities = new QualitySequenceBuilder(ConsedConsensusQualityComputer.computeConsensusQualities(mutableConsensus.build(), placedReads, qualityDataStore));
				} catch (DataStoreException e) {
					throw new IllegalStateException("error computing consensus quality sequence",e);
				}
        	}else{
        		mutableConsensusQualities = createDefaultQualitySequenceBuilder();
        	}
        }else{
        	//make sure consensus quality length matches consensus ungapped length
        	long qualLength =mutableConsensusQualities.getLength();
        	long ungappedLength = mutableConsensus.getUngappedLength();
        	if(qualLength != ungappedLength){
        		throw new IllegalStateException("given consensus quality length does not match ungapped consensus length");
        	}
        }
        NucleotideSequence fullConsensus = mutableConsensus.build();
        Range ungappedContigTrimRange = Range.of(fullConsensus.getUngappedOffsetFor(contigLeft),
        										fullConsensus.getUngappedOffsetFor(contigRight));
        QualitySequence consensusQualitySequence = mutableConsensusQualities
								        		.trim(ungappedContigTrimRange)
								    			.build();
        built=true;
        aceReadBuilderMap.clear();
        initialConsensus = null;
        
        return new DefaultAceContigImpl(contigId, validConsensus,placedReads,complemented, consensusQualitySequence);
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
    public AceContigBuilder recallConsensusNow() {
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
    private StreamingIterator<AceAssembledRead> createStreamingReadIterator(){
    	
    	return IteratorUtil.createStreamingIterator(new Iterator<AceAssembledRead>() {
    		Iterator<AceAssembledReadBuilder> builderIterator = aceReadBuilderMap.values().iterator();
			@Override
			public boolean hasNext() {
				return builderIterator.hasNext();
			}

			@Override
			public AceAssembledRead next() {
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
     * Split the contents of the current ContigBuilder into possibly multiple
     * new ContigBuilders.  The returned ContigBuilders will be new
     * instances which only contain the reads and consensus of the initial
     * contig that intersects the input rangesToKeep.  If a {@link ConsensusCaller}
     * and related {@link QualitySequenceDataStore} and {@link GapQualityValueStrategy}
     * were set via {@link #recallConsensus(ConsensusCaller)} or {@link #recallConsensus(ConsensusCaller, QualitySequenceDataStore, GapQualityValueStrategy)}
     * then those values are copied as well.
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
     * which contains only the portion of this contig at that particular entry key range.  The same id
     * is used for all the returned split contigBuilders so be sure to rename them using 
     * {@link #setContigId(String)} to make them unique.
     */
    public SortedMap<Range, AceContigBuilder> split(Collection<Range> rangesToKeep){
    	SortedMap<Range, AceContigBuilder> splitContigs = new TreeMap<Range, AceContigBuilder>(Range.Comparators.ARRIVAL);
    	
    	CoverageMap<AceAssembledReadBuilder> coverageMap = new CoverageMapBuilder<AceAssembledReadBuilder>(aceReadBuilderMap.values()).build();
    	
    	for(Range rangeTokeep :rangesToKeep){
    		
            NucleotideSequence contigConsensus =mutableConsensus
            									.copy()
												.trim(rangeTokeep)
												.build();
            AceContigBuilder splitContig = new AceContigBuilder(contigId, contigConsensus);
            splitContig.consensusCaller = this.consensusCaller;
            splitContig.qualityDataStore = this.qualityDataStore;
            splitContig.qualityValueStrategy = this.qualityValueStrategy;
            
            Set<String> contigReads = new HashSet<String>();            
            for(CoverageRegion<AceAssembledReadBuilder> region : coverageMap.getRegionsWhichIntersect(rangeTokeep)){
                for(AceAssembledReadBuilder read : region){
                    contigReads.add(read.getId());
                }
            }
            for(String readId : contigReads){
            	//create a copy so we 
            	//can modify our version without
            	//affecting original
            	AceAssembledReadBuilder readBuilder = aceReadBuilderMap.get(readId)
            											.copy();
            	Range readTrimRange = new Range.Builder(readBuilder.asRange().intersection(rangeTokeep))
            						.shift(-readBuilder.getBegin()) //adjust trim range to be relative of read start
            						.build();
            	//trim updated
            	//valid range sequence
            	//and clear range
            	//so we can just use the returned values
            	//when adding this adjusted read to the split
            	//contig.
        		readBuilder.trim(readTrimRange);
            	
            	
            	splitContig.addRead(readId, 
            			readBuilder.getCurrentNucleotideSequence(), 
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
    
    private static final class  DefaultAceContigImpl implements AceContig{

    	
        private final boolean complemented;
        private final QualitySequence consensusQualities;
        
        private final Contig<AceAssembledRead> contig;
        private DefaultAceContigImpl(String id, NucleotideSequence consensus,
                Set<AceAssembledRead> reads,boolean complemented,
                QualitySequence consensusQualities) {
            contig = new DefaultContig<AceAssembledRead>(id, consensus, reads);
            this.complemented = complemented;
            this.consensusQualities = consensusQualities;
        }
       
        
        
        @Override
		public QualitySequence getConsensusQualitySequence() {
			return consensusQualities;
		}



		/**
        * {@inheritDoc}
        */
        @Override
        public boolean isComplemented() {
            return complemented;
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
		public AceAssembledRead getRead(String id) {
			return contig.getRead(id);
		}



		@Override
		public boolean containsRead(String readId) {
			return contig.containsRead(readId);
		}



		@Override
		public StreamingIterator<AceAssembledRead> getReadIterator() {
			return contig.getReadIterator();
		}



		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (complemented ? 1231 : 1237);
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
			if (!(obj instanceof AceContig)) {
				return false;
			}
			AceContig other = (AceContig) obj;
			if (complemented != other.isComplemented()) {
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
			StreamingIterator<AceAssembledRead> readIter=null;
			try{
				readIter = contig.getReadIterator();
				while(readIter.hasNext()){
					AceAssembledRead read = readIter.next();
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
    }

}
