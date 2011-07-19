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
package org.jcvi.common.core.assembly.contig;

import java.util.Set;

import org.jcvi.common.core.assembly.contig.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
/**
 * A {@code Contig} is a CONTIGuous region of genomic data.
 * Contigs are assembled by overlapping reads to form a consensus.
 * @author dkatzel
 * @param <T> the type of {@link PlacedRead}s which were used to build
 * this contig.
 *
 */
public interface Contig<T extends PlacedRead>{
    /**
     * Get the id of this contig.
     * @return the Id of this contig as a String; will never be null.
     */
    String getId();
    /**
     * Get the number of reads in this contig.
     * @return the number of reads in this contig; will always be >=1.
     */
    int getNumberOfReads();
    /**
     * Get the {@link Set} of {@link PlacedRead}s
     * in this contig. 
     * @return a Set of {@link PlacedRead}s; will never be null or empty.
     */
    Set<T> getPlacedReads();
    /**
     * Get the consensus sequence of this contig.  The Consensus
     * is determined by the underlying reads that make up this contig.  Different
     * consensus callers can create different consensus using various criteria
     * and paramters.
     * @return the consensus of this contig as {@link NucleotideSequence}; will
     * never be null.
     * @see ConsensusCaller
     */
    NucleotideSequence getConsensus();
    /**
     * Get the {@link PlacedRead} in this contig with the given id.
     * @param id the id of the read to get.
     * @return the {@link PlacedRead} with that id; or {@code null}
     * if no such read exists in this contig.
     * @see #containsPlacedRead(String)
     */
    T getPlacedReadById(String id);
    /**
     * Does this contig have a {@link PlacedRead} with the given id?
     * @param placedReadId the id of the place read to check for.
     * @return {@code true} if this contig has a read
     * with the given id; {@code false} otherwise.
     */
    boolean containsPlacedRead(String placedReadId);
}
