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

import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Placed;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public interface AcePlacedReadBuilder extends Placed<AcePlacedReadBuilder>{

    AcePlacedReadBuilder reference(NucleotideSequence reference, int newOffset);

    long getStart();

    String getId();

    AcePlacedReadBuilder setStartOffset(int newOffset);

    AcePlacedReadBuilder shiftRight(int numberOfBases);

    AcePlacedReadBuilder shiftLeft(int numberOfBases);

    /**
     * @return the clearRange
     */
    Range getClearRange();

    /**
     * @return the phdInfo
     */
    PhdInfo getPhdInfo();

    /**
     * @return the dir
     */
    Direction getDirection();

    /**
     * @return the ungappedFullLength
     */
    int getUngappedFullLength();

    AcePlacedRead build();

    AcePlacedReadBuilder reAbacus(Range gappedValidRangeToChange, String newBasecalls);

    AcePlacedReadBuilder reAbacus(Range gappedValidRangeToChange,
            List<Nucleotide> newBasecalls);

    long getLength();

    long getEnd();

    Range asRange();

    /**
     * @return the basesBuilder
     */
    NucleotideSequenceBuilder getBasesBuilder();

    NucleotideSequence getCurrentNucleotideSequence();

    /**
     * {@inheritDoc}
     */
    int compareTo(AcePlacedReadBuilder o);

}