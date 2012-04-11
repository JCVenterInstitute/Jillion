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
package org.jcvi.common.core.seq.read.trace.pyro;


import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.read.trace.Trace;
/**
 * A {@code Flowgram} is a next-generation trace
 * created using pyrosequencing.
 * @author dkatzel
 * @see <a href="http://en.wikipedia.org/wiki/Pyrosequencing">Pyrosequencing Wikipedia Article</a>
 *
 */
public interface Flowgram extends Trace {
	/**
	 * The name of this flowgram.
	 * @return the id as a String; will never be null.
	 */
    String getId();
    
    /**
     * The quality clip points that
     * specify the subset of the basecalls
     * that are good quality.  If no
     * clip is set, then the Range should be
     * equal to Range.create(0,0);
     * @return a Range (never null).
     */
    Range getQualityClip();
    /**
     * The adapter clip points that
     * specify the subset of the basecalls
     * that are not adapter sequence.  If no
     * clip is set, then the Range should be
     * equal to Range.create(0,0);
     * @return a Range (never null).
     */
    Range getAdapterClip();
    /**
     * Get the number of flows that this
     * flowgram has.
     * @return an int >=0.
     */
    int getNumberOfFlows();
    /**
     * Get the flow value for the given flow index.  
     * The flow value is a float which is the estimated
     * size of a homopolymer run for the given flow index.
     * 
     * @param index the index into the flowgram
     * values to get.  Must be >=0 and < the number of flows.
     * @return the flow value as a float.
     * @throws ArrayIndexOutOfBoundsException if 
     * the index is outside the number of flows
     * specified by {@link #getNumberOfFlows()}.
     */
    float getFlowValue(int index);
}
