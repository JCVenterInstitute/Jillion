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
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nt;

import java.util.Map;
/**
 * {@code ReferenceEncodedNucleotideSequence} encodes
 * a NucleotideSequence by referring to a reference sequence
 * and only storing the differences (SNPs) between
 * this NucleotideSequence and its reference. Any {@link #get(int)}
 * that refers to a non-SNP
 * gets delegated to the reference.
 * <p>
 * This should keep the memory footprint
 * quite low since an underlying sequence should map to a reference 
 * with a high identity.  If the reference is the consensus,
 * the underlying sequence should map more than 90%.
 * @author dkatzel
 *
 *
 */
public interface ReferenceEncodedNucleotideSequence extends NucleotideSequence{
    
    /**
     * Get a Mapping of all the offsets (as Integers) 
     * of this read compared to the reference.
     * All coordinates are 0-based gapped offset locations in the read coordinate system;
     * so if a difference is located in the first base of the read,
     * then its integer will be zero.
     * @return a Map of all the differences between
     * this sequence and its reference; will never be null
     * but may be empty if there are no differences.
     */
    Map<Integer, Nucleotide> getDifferenceMap();
}
