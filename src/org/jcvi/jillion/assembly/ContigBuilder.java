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
package org.jcvi.jillion.assembly;

import java.util.Collection;

import org.jcvi.jillion.assembly.util.GapQualityValueStrategy;
import org.jcvi.jillion.assembly.util.consensus.ConsensusCaller;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.Builder;

/**
 * {@code ContigBuilder} is a {@link Builder}
 * for {@link Contig}s that allows
 * creating a contig object by adding placed reads
 * and setting a consensus.  An {@link ContigBuilder}
 * can be used to create contig objects that 
 * have been created by an assembler or can be used
 * to create contigs from the imagination.
 * There are additional methods to allow
 * the contig consensus or underlying
 * reads to be modified before
 * the creation of the {@link Contig} instance
 * (which is immutable).
 * @author dkatzel
 *
 */
public interface ContigBuilder<R extends AssembledRead,C extends Contig<R>> extends Builder<C>{
    /**
     * Change the contig id to the given id.
     * @param contigId the new id this contig should have.
     * @return this.
     * @throws NullPointerException if contigId is null.
     */
    ContigBuilder<R,C> setContigId(String contigId);
   /**
    * Get the current contig id.
    * @return the contig id.
    */
    String getContigId();
    /**
     * Get the number of reads currently
     * in this contig.
     * @return an int will always be >=0.
     */
    int numberOfReads();
    /**
     * Add the given {@link AssembledRead} read to this contig with the given values.  This read
     * can later get modified via the {@link #getAssembledReadBuilder(String)}.
     * @param placedRead the read to add (can not be null).
     * @return this.
     * @throws NullPointerException if acePlacedRead is null.
     */
    ContigBuilder<R,C> addRead(R placedRead);
    /**
     * Adds all the given reads to this contig.  These reads
     * can later get modified via the {@link #getAssembledReadBuilder(String)}.
     * @param reads the reads to add (can not be null).
     * @return this.
     * @throws NullPointerException if reads is null.
     */
    ContigBuilder<R,C> addAllReads(Iterable<R> reads);
    /**
     * Get a collection of all the {@link AssembledReadBuilder}s that are
     * currently associated with this contig.  This collection
     * is backed by the contig builder so any changes to the 
     * returned collection or modifications to any of its 
     * {@link AssembledReadBuilder}s will modify the contig
     * as well.
     * @return a a collection of all the {@link AssembledReadBuilder} that are
     * currently associated with this contig; never null.
     */
    Collection<? extends AssembledReadBuilder<R>> getAllAssembledReadBuilders();
    /**
     * Get the {@link AssembledReadBuilder} for the read in this 
     * contig with the given read id.  Any changes to the returned
     * instance will modify that read in this contig.
     * @param readId the id of the read to get.
     * @return a {@link AssembledReadBuilder}, will return 
     * null if no read with that id currently exists for this contig.
     */
    AssembledReadBuilder<R> getAssembledReadBuilder(String readId);
    /**
     * Remove the read with the given read id from this contig.
     * If this contig doesn't have a read with that readId, then
     * the contig is unchanged.
     * @param readId the read id to remove, can not be null.
     * @throws NullPointerException if readId is null.
     * @return this
     */
    ContigBuilder<R,C> removeRead(String readId);
    

    /**
     * Get the {@link NucleotideSequenceBuilder} instance that 
     * will be the consensus in the built contig, any changes
     * to the returned instance will modify the consensus
     * in this contig.
     * @return the {@link NucleotideSequenceBuilder} for this
     * contig's consensus, never null.
     */
    NucleotideSequenceBuilder getConsensusBuilder();
    
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
    ContigBuilder<R, C> recallConsensus(ConsensusCaller consensusCaller, 
    		QualitySequenceDataStore qualityDataStore,
    		GapQualityValueStrategy qualityValueStrategy);
    
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
    ContigBuilder<R, C> recallConsensus(ConsensusCaller consensusCaller);
    
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
    ContigBuilder<R, C> recallConsensusNow();
    
    /**
     * {@inheritDoc}
     * <p/>
     * Take the current read and consensus data 
     * (which has possibly been previous edited and/or shifted)
     * and create a new AceContig instance.  Calling this method
     * might release resources or destroy temp data
     * that is required to built this contig, therefore
     * this method should be only called once per builder instance.
     * If this method is called more than once, then 
     * an {@link IllegalStateException} will be thrown.
     * @return a new AceContig instance, never null.
     * @throws IllegalStateException if this method is called more than once.
     */
    @Override
    C build();
}
