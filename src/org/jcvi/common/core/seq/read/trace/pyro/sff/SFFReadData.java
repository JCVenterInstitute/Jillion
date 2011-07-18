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
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;
/**
 * {@code SFFReadData} contains the raw
 * sequencing data from a given SFF read.
 * @author dkatzel
 *
 *
 */
public interface SFFReadData {
    /**
     * The flowgram values contains the homopolymer
     * stretch estimates for each flow of the read.
     * @return an array conaining homopolymer
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
     * The called basecalls.
     * @return
     */
    String getBasecalls();
    /**
     * The quality scores for each bases in the sequence
     * stored as Phred values.
     * @return
     */
    byte[] getQualities();

}
