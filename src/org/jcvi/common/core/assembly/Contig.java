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
 * Created on Sep 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * A {@code Contig} is a CONTIGuous region of genomic data.
 * Contigs are assembled by overlapping reads to form a consensus.
 * @author dkatzel
 * @param <T> the type of {@link AssembledRead}s which were used to build
 * this contig.
 */
public interface Contig<T extends AssembledRead>{
    /**
     * Get the id of this contig.
     * @return the Id of this contig as a String; will never be null.
     */
    String getId();
    /**
     * Get the number of reads in this contig.
     * @return the number of reads in this contig; will always be >=0.
     */
    int getNumberOfReads();
    /**
     * Get the {@link CloseableIterator} of {@link AssembledRead}s
     * that are contained in this contig. 
     * @return a {@link CloseableIterator}  of {@link AssembledRead}s; will never be null 
     * but could be empty.
     */
    CloseableIterator<T> getReadIterator();
    /**
     * Get the consensus sequence of this contig.  The Consensus
     * is determined by the underlying reads that make up this contig.  Different
     * consensus callers can create different consensus using various criteria
     * and paramters.
     * @return the consensus of this contig as {@link NucleotideSequence}; will
     * never be null.
     * @see ConsensusCaller
     */
    NucleotideSequence getConsensusSequence();
    /**
     * Get the {@link AssembledRead} in this contig with the given id.
     * @param id the id of the read to get.
     * @return the {@link AssembledRead} with that id; or {@code null}
     * if no such read exists in this contig.
     * @see #containsRead(String)
     */
    T getRead(String id);
    /**
     * Does this contig have a {@link AssembledRead} with the given id?
     * @param readId the id of the {@link AssembledRead} to check for.
     * @return {@code true} if this contig has a read
     * with the given id; {@code false} otherwise.
     */
    boolean containsRead(String readId);
}
