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

package org.jcvi.common.core.assembly.contig.ace;

import java.util.Collection;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;

/**
 * {@code AceContigBuilder} is a {@link Builder}
 * for {@link AceContig}s that allows
 * the contig consensus or underlying
 * reads to be modified before
 * the creation of the {@link AceContig} instance
 * (which is immutable).
 * @author dkatzel
 *
 *
 */
public interface AceContigBuilder extends Builder<AceContig>{
    /**
     * Change the contig id to the given id.
     * @param contigId the new id this contig should have.
     * @return this.
     * @throws NullPointerException if contigId is null.
     */
   AceContigBuilder setContigId(String contigId);
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
     * Add the given {@link AcePlacedRead} read to this contig with the given values.  This read
     * can later get modified via the {@link #getAcePlacedReadBuilder(String)}.
     * @param acePlacedRead the read to add (can not be null).
     * @return this.
     * @throws NullPointerException if acePlacedRead is null.
     */
    AceContigBuilder addRead(AcePlacedRead acePlacedRead);
    /**
     * Adds all the given reads to this contig.  These reads
     * can later get modified via the {@link #getAcePlacedReadBuilder(String)}.
     * @param reads the reads to add (can not be null).
     * @return this.
     * @throws NullPointerException if reads is null.
     */
    AceContigBuilder addAllReads(Iterable<AcePlacedRead> reads);
    /**
     * Get a collection of all the AcePlacedReadBuilders that are
     * currently associated with this contig.  This collection
     * is backed by the contig builder so any changes to the 
     * returned collection or modifications to any of its 
     * {@link AcePlacedReadBuilder}s will modify the contig
     * as well.
     * @return a a collection of all the AcePlacedReadBuilders that are
     * currently associated with this contig; never null.
     */
    Collection<AcePlacedReadBuilder> getAllAcePlacedReadBuilders();
    /**
     * Get the {@link AcePlacedReadBuilder} for the read in this 
     * contig with the given read id.  Any changes to the returned
     * instance will modify that read in this contig.
     * @param readId the id of the read to get.
     * @return a {@link AcePlacedReadBuilder}, will return 
     * null if no read with that id currently exists for this contig.
     */
    AcePlacedReadBuilder getAcePlacedReadBuilder(String readId);
    /**
     * Remove the read with the given read id from this contig.
     * If this contig doesn't have a read with that readId, then
     * the contig is unchanged.
     * @param readId the read id to remove, can not be null.
     * @throws NullPointerException if readId is null.
     */
    void removeRead(String readId);
    /**
     * Add a read to this contig with the given values.  This read
     * can later get modified via the {@link #getAcePlacedReadBuilder(String)}.
     * @param readId the Id this read should have
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
    AceContigBuilder addRead(String readId, String validBases, int offset,
            Direction dir, Range clearRange, PhdInfo phdInfo,
            int ungappedFullLength);

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
     * {@inheritDoc}
     * <p>
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
    AceContig build();

}