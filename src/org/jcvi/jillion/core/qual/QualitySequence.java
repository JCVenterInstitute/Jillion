/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.qual;

import org.jcvi.jillion.core.Sequence;

/**
 * {@code QualitySequence} is a marker interface
 * for {@link Sequence} implementations
 * that encode {@link PhredQuality} values.
 * @author dkatzel
 *
 *
 */
public interface QualitySequence extends Sequence<PhredQuality>{

	/**
     * Two {@link QualitySequence}s are equal
     * if they contain the same {@link PhredQuality}s 
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
    
    
    /**
     * Create an new array of bytes of length {@link #getLength()}
     * where index in the array is the ith quality score stored
     * as a byte.  This method may be expensive to perform
     * depending on the size of the sequence and the encoding used.
     * @return a new byte array, never null.
     */
    byte[] toArray();
    /**
     * Get the average quality score as a double.
     * This calculation only works on a sequence
     * that is not empty.
     * @return the avg quality score as a double.
     * @throws ArithmeticException if the sequence length is 0.
     */
    double getAvgQuality() throws ArithmeticException;
    /**
     * Get the min {@link PhredQuality} in the 
     * Sequence.
     * @return a {@link PhredQuality} or {@code null}
     * if the sequence is empty.
     */
    PhredQuality getMinQuality();
    /**
     * Get the min {@link PhredQuality} in the 
     * Sequence.
     * @return a {@link PhredQuality} or {@code null}
     * if the sequence is empty.
     */
    PhredQuality getMaxQuality();
    /**
     *  /**
     * Create a new Builder object that is initialized
     * to the current sequence.  Any changes made to the returned Builder
     * will <strong>NOT</strong> affect this immutable Sequence.
     * 
     * @return a new Builder instance, will never be null.
     * @sincen 5.0
     */
    default QualitySequenceBuilder toBuilder(){
        return new QualitySequenceBuilder(this);
    }
}
