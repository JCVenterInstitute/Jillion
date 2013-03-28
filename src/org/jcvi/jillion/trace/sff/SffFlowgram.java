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


import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.trace.Trace;
/**
 * A {@code Flowgram} is a next-generation trace
 * created using pyrosequencing.
 * @author dkatzel
 * @see <a href="http://en.wikipedia.org/wiki/Pyrosequencing">Pyrosequencing Wikipedia Article</a>
 *
 */
public interface SffFlowgram extends Trace {
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
    float getCalledFlowValue(int index);
    
    /**
     * Get the raw  flow indexes in the array returned
     * by {@link #getRawEncodedFlowValues() }
     * @return a new byte array; never null
     */
    byte[] getRawIndexes();
    /**
     * Get the raw flowgram values which contains
     * the homopolymer stretch estimates for each flow of the read.
     * This is equivalent to the {@link SffReadData#getFlowgramValues()}.
     * @return a new short array; never null
     */
    short[] getRawEncodedFlowValues();
}
