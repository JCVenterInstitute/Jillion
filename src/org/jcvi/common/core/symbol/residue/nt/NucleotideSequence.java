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
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nt;


import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.ResidueSequence;
/**
 * {@code NucleotideSequence} an interface to abstract
 * how a {@link Sequence} of {@link Nucleotide}s are encoded in memory.  Nucleotide data
 * can be stored in many different ways depending
 * on the use case and size and composition of the sequence.
 * Different encoding implementations can take up more or less memory or require
 * more computations to decode.  This interface hides implementation details
 * regarding the decoding so users don't have to worry about it.
 * @author dkatzel
 */
public interface NucleotideSequence extends ResidueSequence<Nucleotide>{
	/**
     * Two {@link NucleotideSequence}s are equal
     * if they contain the same {@link Nucleotide}s 
     * in the same order.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    boolean equals(Object o);
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    int hashCode();
}
