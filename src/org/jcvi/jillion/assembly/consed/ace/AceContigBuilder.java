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

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.ContigBuilder;
import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageMapBuilder;
import org.jcvi.jillion.assembly.util.CoverageRegion;
import org.jcvi.jillion.assembly.util.GapQualityValueStrategy;
import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceBuilder;
import org.jcvi.jillion.assembly.util.consensus.ConsensusCaller;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.assembly.DefaultContig;
/**
 * Builder
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
 * <p>
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
	
	private GapQualityValueStrategy qualityValueStrategy=GapQualityValueStrategy.LOWEST_FLANKING;
	
    private NucleotideSequence initialConsensus;
    private NucleotideSequenceBuilder mutableConsensus;
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
    
    private QualitySequenceBuilder createDefaultQualitySequenceBuilder(int ungappedConsensusLength){
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
        checkNotNull(contigId);
        if(initialConsensus ==null){
            throw new NullPointerException("consensus can not be null");
        }
    	this.initialConsensus = initialConsensus;
    	 this.contigId = contigId;
    	 this.mutableConsensus = new NucleotideSequenceBuilder(initialConsensus);
    	 aceReadBuilderMap = new HashMap<String, AceAssembledReadBuilder>(200);
    }

	public void checkNotNull(String contigId) {
		if(contigId ==null){
            throw new NullPointerException("contig id can not be null");
        }
	}
    
    public AceContigBuilder(String contigId, ConsensusCaller consensusCaller){
    	checkNotNull(contigId);
        if(consensusCaller ==null){
            throw new NullPointerException("consensusCaller can not be null");
        }
    	 this.contigId = contigId;
    	 this.mutableConsensus = null;
    	 recallConsensus(consensusCaller);
    	 
    	 aceReadBuilderMap = new HashMap<String, AceAssembledReadBuilder>(200);
    }
    /**
     * Create a new {@link AceContigBuilder} for a <strong>de novo</strong> contig
     * that will have the given contig id.
     * There is initially, no consensus 
     * ({@link #getConsensusBuilder()} will return {@code null})
     * Once reads are added and the contig is built by calling
     * {@link #build()}
     * the consensus will be created by using the underlying
     * read information to call consensus using
     * the given {@link ConsensusCaller},
     *  {@link QualitySequenceDataStore} and {@link GapQualityValueStrategy}.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * {@link ConsensusCaller} and {@link QualitySequenceDataStore}
     * which contains the quality data for all of the reads in this ace contig.
     * The consensus will get recalled inside the {@link #build()}
     * and {@link #recallConsensusNow()}.
     * method before the {@link AceContig} instance is created.
     * @param consensusCaller the {@link ConsensusCaller}  instance to use
     * to recall the consensus of this contig; can not be null.
     * @param qualityDataStore the {@link QualitySequenceDataStore}
     * which contains all the quality data for all of the reads 
     * that <strong>will be added</strong> to this contig; can not be null.
     * @param qualityValueStrategy the {@link GapQualityValueStrategy}
     * that will be used to compute the quality values of all
     * of the read's gaps.
     * @throws NullPointerException if any of the parameters are null.
     */
    public AceContigBuilder(String contigId, ConsensusCaller consensusCaller, 
    		QualitySequenceDataStore qualityDataStore,
    		GapQualityValueStrategy qualityValueStrategy){
        checkNotNull(contigId);
        if(consensusCaller ==null){
            throw new NullPointerException("consensusCaller can not be null");
        }
    	 this.contigId = contigId;
    	 this.mutableConsensus = null;
    	 recallConsensus(consensusCaller, qualityDataStore, qualityValueStrategy);
    	 
    	 aceReadBuilderMap = new HashMap<String, AceAssembledReadBuilder>(200);
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
     * data structures as a performance optimization.  Must be &ge;0.
     * 
     * @throws NullPointerException if contigId or consensus are null.
     * @throws IllegalArgumentException if estimatedNumberOfReads
     * is &lt; 0
     */
    public AceContigBuilder(String contigId, NucleotideSequence initialConsensus,
    		int estimatedNumberOfReads){
        checkNotNull(contigId);
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
     * @param qualityValueStrategy the {@link GapQualityValueStrategy}
     * that will be used to compute the quality values of all
     * of the read's gaps.
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
    	if(qualityValueStrategy ==null){
    		throw new NullPointerException("quality value strategy can not be null");
    	}
    	this.consensusCaller=consensusCaller;
    	this.qualityDataStore = qualityDataStore;
    	this.qualityValueStrategy = qualityValueStrategy;
    	return this;
    }
    
    public AceContigBuilder updateConsensusRecaller(ConsensusCaller consensusCaller){
    	if(consensusCaller ==null){
    		throw new NullPointerException("consensus caller can not be null");
    	}
    	this.consensusCaller=consensusCaller;
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
    	this.qualityValueStrategy = GapQualityValueStrategy.LOWEST_FLANKING;
    	return this;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public AceContigBuilder setContigId(String contigId){
        checkNotNull(contigId);
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
     * @throws NullPointerException if parameters are null
     * @throws IllegalArgumentException if a read with the same readId already exists in this contig.
     */
    public AceContigBuilder addRead(String readId, NucleotideSequence validBases, int offset,
            Direction dir, Range clearRange,PhdInfo phdInfo,int ungappedFullLength) {
        if(readId ==null){
        	throw new NullPointerException("readId can not be null");
        }
        if(aceReadBuilderMap.containsKey(readId)){
        	throw new IllegalArgumentException("read with same id already in contig" + readId);
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
                readId,validBases,
                offset,
                dir,clearRange,phdInfo,ungappedFullLength, this);
    }
    private void adjustContigLeftAndRight(NucleotideSequence validBases, int offset) {
        adjustContigLeft(offset);
        adjustContigRight(validBases, offset);
    }
    private void adjustContigRight(NucleotideSequence validBases, int offset) {
        final int endOfNewRead = offset+ (int)validBases.getLength()-1;
        adjustContigRight(endOfNewRead);
    }
    private void adjustContigRight(int endOfNewRead) {
        
     //   if((initialConsensus ==null || endOfNewRead <= initialConsensus.getLength()) && (contigRight ==-1 || endOfNewRead > contigRight)){
    	 if(contigRight ==-1 || endOfNewRead > contigRight){
            contigRight = endOfNewRead ;
        }
    }
    private void adjustContigLeft(int offset) {
        
        if(contigLeft ==-1 || offset <contigLeft){
            contigLeft = offset;
        }
    }
    
    
	void updatedReadRange(String id, long begin, long end) {
		if(aceReadBuilderMap.containsKey(id)){
			adjustContigLeft((int)begin);
			adjustContigRight((int)end);
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
        	//turn off firing updates
        	//since we don't want to update contigLeft and right anymore
        	((DefaultAceAssembledRead.Builder)aceReadBuilder).setParentContigBuilder(null);
            aceReadBuilder.setStartOffset(newOffset);
            placedReads.add(aceReadBuilder.build(validConsensus));                
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
        		mutableConsensusQualities = createDefaultQualitySequenceBuilder((int)mutableConsensus.getUngappedLength());
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
     * <p>
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
    	final SliceBuilder builders[];
    	if(mutableConsensus==null){
    		builders = initializeSliceBuilders(contigRight+1);
    	}else{
    		builders = initializeSliceBuilders(mutableConsensus.build());
    	}
    	for(AceAssembledReadBuilder aceReadBuilder : aceReadBuilderMap.values()){
    		int start = (int)aceReadBuilder.getBegin();
			
			String id =aceReadBuilder.getId();
			Direction dir = aceReadBuilder.getDirection();
			QualitySequence fullQualities =null;
			
			if(qualityDataStore!=null){
				try {
					fullQualities = qualityDataStore.get(id);
				} catch (DataStoreException e) {
					throw new IllegalStateException("error recalling consensus",e);
				}
    			if(fullQualities ==null){
    				throw new NullPointerException("could not get qualities for "+id);
    			}
			}
			NucleotideSequence readSequence = aceReadBuilder.getCurrentNucleotideSequence();
			if(fullQualities==null){
				byte[] qualArray = new byte[aceReadBuilder.getUngappedFullLength()];
				Arrays.fill(qualArray, DEFAULT_QUALITY.getQualityScore());
				fullQualities = new QualitySequenceBuilder(qualArray).build();
			}
			QualitySequence gappedValidRangequalities = 
					qualityValueStrategy.getGappedValidRangeQualitySequenceFor(readSequence, fullQualities, 
							aceReadBuilder.getClearRange(), dir);	
			
			Iterator<Nucleotide> baseIter = readSequence.iterator();
			Iterator<PhredQuality> qualIter = gappedValidRangequalities.iterator();
			int i=0;
			while(baseIter.hasNext()){
				Nucleotide base = baseIter.next();
				PhredQuality quality = qualIter.next();
				builders[start+i].add(id, base, quality, dir);
				i++;
			}
			
    	}
    	if(mutableConsensus ==null){
    		mutableConsensus = new NucleotideSequenceBuilder(builders.length);
    		for(int i=0; i<builders.length; i++){
	    		SliceBuilder builder = builders[i];
    			Slice slice = builder.build();            
	    		mutableConsensus.append(consensusCaller.callConsensus(slice).getConsensus());
	    	}
    	}else{
	    	for(int i=0; i<builders.length; i++){
	    		SliceBuilder builder = builders[i];
	    		//skip 0x
	    		if(builder.getCurrentCoverageDepth()>0){
	    			Slice slice = builder.build();            
		    		mutableConsensus.replace(i,consensusCaller.callConsensus(slice).getConsensus());
	    		}
	    	}
    	}
    	return this;
	}
    private SliceBuilder[] initializeSliceBuilders(int length){
    	SliceBuilder builders[] = new SliceBuilder[length];
    	for(int i=0; i<length; i++){
    		builders[i] = new SliceBuilder();
    	}
    	return builders;
    }
    private SliceBuilder[] initializeSliceBuilders(NucleotideSequence consensus){
    	SliceBuilder builders[] = new SliceBuilder[(int)consensus.getLength()];
		int i=0;
		Iterator<Nucleotide> iter = consensus.iterator();
		while(iter.hasNext()){
			builders[i++] = new SliceBuilder().setConsensus(iter.next());
		}
    	return builders;
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
    		 AceContigBuilder splitContig;
    		if(mutableConsensus==null){
    			splitContig = new AceContigBuilder(contigId, consensusCaller);
    		}else{
	            NucleotideSequence contigConsensus =mutableConsensus
	            									.copy()
													.trim(rangeTokeep)
													.build();
	            splitContig = new AceContigBuilder(contigId, contigConsensus);
	            splitContig.consensusCaller = this.consensusCaller;            
    		}
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
            	Range.Builder readTrimRangeBuilder = new Range.Builder(readBuilder.asRange().intersection(rangeTokeep))
            						.shift(-readBuilder.getBegin()); //adjust trim range to be relative of read start
            						
            	
            	//VHTNGS-910 : check that the new trim region
            	//doesn't start or end in gaps.
            	//if so trim more.
            	NucleotideSequence untrimmedReadSequence = readBuilder.getCurrentNucleotideSequence();
            	
            	
            	
            	//null out copy's parent contigBuilder
            	//to avoid erroneous trim calls
            	//to the wrong contig builder.
            	//(we don't want to alter the original contig)
            	//we discard this read builder anyway when
            	//we add the final trimmed data to our new builder anyway.
            	((DefaultAceAssembledRead.Builder)readBuilder).setParentContigBuilder(null);
            	
            	
            	//trim updated
            	//valid range sequence
            	//and clear range
            	//so we can just use the returned values
            	//when adding this adjusted read to the split
            	//contig.
        		readBuilder.trim(untrimmedReadSequence.getContractingFlankingNonGapRangeFor(readTrimRangeBuilder));
        				
            	
            	
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
		public ThrowingStream<AceAssembledRead> reads() {
			return contig.reads();
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

			try(StreamingIterator<AceAssembledRead> readIter = contig.getReadIterator()){
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
			}			
			return true;
		}
    }

}
