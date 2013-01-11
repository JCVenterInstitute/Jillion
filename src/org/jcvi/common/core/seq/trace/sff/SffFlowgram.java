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
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sff;

import java.util.Arrays;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.ObjectsUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.internal.util.GrowableShortArray;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

final class SffFlowgram implements Flowgram {
    private final String id;
    private final NucleotideSequence basecalls;
    private final QualitySequence qualities;
    private final Range qualitiesClip;
    private final Range adapterClip;
    private final short[] values;

    public static Flowgram create(SffReadHeader readHeader, SffReadData readData) {
        return new SffFlowgram(
                readHeader.getId(),
                new NucleotideSequenceBuilder(readData.getNucleotideSequence()).build(),
                new QualitySequenceBuilder(readData.getQualitySequence()).build(),
                computeValues(readData),
                readHeader.getQualityClip(),
                readHeader.getAdapterClip());
    }
    
    static short[] computeValues(SffReadData readData) {
        final byte[] indexes = readData.getFlowIndexPerBase();
        final short[] encodedValues =readData.getFlowgramValues();
        verifyNotEmpty(encodedValues);
        return computeValues(indexes, encodedValues);
    }

    private static short[] computeValues(final byte[] indexes,
            final short[] encodedValues) {
    	if(indexes.length==0){
    		return new short[0];
    	}
        GrowableShortArray values = new GrowableShortArray(indexes.length);
        // positions are 1-based so start with -1 to compensate.
        int position=-1;
        int i=0;

        while( i < indexes.length){
            if(indexes[i] != 0){
                position+=IOUtil.toUnsignedByte(indexes[i]);
                values.append(encodedValues[position]);
            }
            i++;
        }

        return values.toArray();
    }

    private static void verifyNotEmpty(final short[] encodedValues) {
        if(encodedValues.length==0){
            throw new IllegalArgumentException("read data must contain Flowgram values");
        }
    }
    /**
     * @param basecalls
     * @param qualities
     * @param values
     * @param qualitiesClip
     * @param adapterClip
     */
    protected SffFlowgram(String id,NucleotideSequence basecalls, QualitySequence qualities,
            short[] values, Range qualitiesClip, Range adapterClip) {
        canNotBeNull(id,basecalls, qualities, values, qualitiesClip, adapterClip);
        this.id = id;
        this.basecalls = basecalls;
        this.qualities = qualities;
        this.values = values;
        this.qualitiesClip = qualitiesClip;
        this.adapterClip = adapterClip;
    }

    private void canNotBeNull(String id,NucleotideSequence basecalls, Sequence<PhredQuality> qualities,
            short[] values, Range qualitiesClip, Range adapterClip) {
        ObjectsUtil.checkNotNull(id, "id can not be null");
        ObjectsUtil.checkNotNull(basecalls, "basecalls can not be null");
        ObjectsUtil.checkNotNull(qualities, "qualities can not be null");
        ObjectsUtil.checkNotNull(values, "values can not be null");
        ObjectsUtil.checkNotNull(qualitiesClip, "qualitiesClip can not be null");
        ObjectsUtil.checkNotNull(adapterClip, "adapterClip can not be null");

        if(values.length==0){
            throw new IllegalArgumentException("values can not be empty");
        }
    }

     @Override
     public String getId() {
         return id;
     }

    @Override
	public NucleotideSequence getNucleotideSequence() {
		return basecalls;
	}

	@Override
    public QualitySequence getQualitySequence() {
        return qualities;
    }

    /**
     * @return the qualityClip
     */
    @Override
    public Range getQualityClip() {
        return qualitiesClip;
    }
    /**
     * @return the adapterClip
     */
    @Override
    public Range getAdapterClip() {
        return adapterClip;
    }
    @Override
    public int getNumberOfFlows() {
        return values.length;
    }
    @Override
    public float getFlowValue(int index) {
        return SffUtil.convertFlowgramValue(values[index]);
    }
    /**
     * Returns the hash code for this {@link SffFlowgram}.
     * Hash code based on hashcodes for values, qualities, and clip points.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        result = prime * result + basecalls.hashCode();
        result = prime * result + Arrays.hashCode(values);
        result = prime * result + qualities.hashCode();
        result = prime * result + qualitiesClip.hashCode();
        result = prime * result + adapterClip.hashCode();
        
        return result;
    }
    /**
     * Compares this {@link SffFlowgram} with the specified Object for equality.
     * This method considers two {@link SffFlowgram} objects equal 
     * only if they are have equal values, qualities and clip points. 
     * (basecalls can be derived from the values so basecalls 
     * are not taken into account).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof Flowgram)){
            return false;
        }
        Flowgram other = (Flowgram) obj;
        
        boolean nonValuesEqual=
        ObjectsUtil.nullSafeEquals(id, other.getId())
        && ObjectsUtil.nullSafeEquals(basecalls, other.getNucleotideSequence())
        && ObjectsUtil.nullSafeEquals(qualities, other.getQualitySequence())
        && ObjectsUtil.nullSafeEquals(qualitiesClip, other.getQualityClip())
        && ObjectsUtil.nullSafeEquals(adapterClip, other.getAdapterClip())
        && ObjectsUtil.nullSafeEquals(getNumberOfFlows(), other.getNumberOfFlows());
        if(!nonValuesEqual){
        	return false;
        }
        for(int i=0; i<values.length; i++){
        	if(getFlowValue(i) != other.getFlowValue(i)){
        		return false;
        	}
        }
        return true;
        
       
        
    }

    

    
}
