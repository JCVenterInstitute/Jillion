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
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.math.BigInteger;
import java.util.Arrays;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.MathUtil;
import org.jcvi.jillion.core.util.ObjectsUtil;

final class DefaultSffReadData implements SffReadData {

    private final NucleotideSequence basecalls;
    private final byte[] indexes;
    private final short[] values;
    private final QualitySequence qualities;


    /**
     * @param basecalls
     * @param indexes
     * @param values
     * @param qualities
     */
    public DefaultSffReadData(NucleotideSequence basecalls, byte[] indexes, short[] values,
    		QualitySequence qualities) {
        validateArguments(basecalls, indexes, values, qualities);
        this.basecalls = basecalls;
        //make defensive copies
        
        this.indexes = Arrays.copyOf(indexes, indexes.length);
        this.values = Arrays.copyOf(values, values.length);
        this.qualities = qualities;
    }

    private void validateArguments(NucleotideSequence basecalls, byte[] indexes,
            short[] values, QualitySequence qualities) {
        canNotBeNull(basecalls, indexes, values, qualities);
        lengthsMatch(basecalls, indexes, qualities);
        indexesWithinBounds(indexes, values);
    }

    private void indexesWithinBounds(byte[] indexes, short[] values) {
        final BigInteger sum = MathUtil.sumOf(indexes);
        if(sum.compareTo(BigInteger.valueOf(values.length)) >0){
            throw new ArrayIndexOutOfBoundsException("indexed flowgram value refers to "+ sum
                    + "flowgram value length is" + values.length);
        }
    }

    private void lengthsMatch(NucleotideSequence basecalls, byte[] indexes, QualitySequence qualities) {
        if(basecalls.getLength() !=indexes.length || indexes.length !=qualities.getLength()){
            throw new IllegalArgumentException("basecalls, indexes and qualities must be the same length");
        }
    }

    private void canNotBeNull(NucleotideSequence basecalls, byte[] indexes, short[] values,
    		QualitySequence qualities) {
        ObjectsUtil.checkNotNull(basecalls, "basecalls can not be null");
        ObjectsUtil.checkNotNull(indexes, "indexes can not be null");
        ObjectsUtil.checkNotNull(values, "flowgram values can not be null");
        ObjectsUtil.checkNotNull(qualities, "qualities can not be null");
    }

    @Override
    public NucleotideSequence getNucleotideSequence() {
        return basecalls;
    }

    @Override
    public byte[] getFlowIndexPerBase() {
        //defensive copy
        return Arrays.copyOf(indexes, indexes.length);
    }

    @Override
    public short[] getFlowgramValues() {
        //defensive copy
        return Arrays.copyOf(values, values.length);
    }

    @Override
    public QualitySequence getQualitySequence() {
        return qualities;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result+ basecalls.hashCode();
        result = prime * result + Arrays.hashCode(indexes);
        result = prime * result + qualities.hashCode();
        result = prime * result + Arrays.hashCode(values);
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultSffReadData)){
            return false;
        }
        final DefaultSffReadData other = (DefaultSffReadData) obj;
        return ObjectsUtil.nullSafeEquals(getNucleotideSequence(), other.getNucleotideSequence())
                && Arrays.equals(indexes, other.indexes)
                && qualities.equals(other.getQualitySequence())
                && Arrays.equals(values, other.values);

    }

}
