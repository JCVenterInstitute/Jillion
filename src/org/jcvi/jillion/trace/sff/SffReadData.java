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
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code SFFReadData} contains the raw
 * sequencing data from a given SFF read.
 * @author dkatzel
 *
 *
 */
public interface SffReadData {
    /**
     * The flowgram values contains the homopolymer
     * stretch estimates for each flow of the read.
     * @return an array containing homopolymer
     * estimates for each flow; never null.
     */
    short[] getFlowgramValues();
    /**
     * the flow index in the array
     * returned by {@link #getFlowgramValues()} for each base
     * in the called sequence.
     * @return
     */
    byte[] getFlowIndexPerBase();
    /**
     * The called basecalls as a {@link NucleotideSequence}.
     * @return a {@link NucleotideSequence}; never null.
     */
    NucleotideSequence getNucleotideSequence();
    /**
     * The quality scores for each base in the sequence
     * as a {@link QualitySequence}.
     * @return
     */
    QualitySequence getQualitySequence();

}
