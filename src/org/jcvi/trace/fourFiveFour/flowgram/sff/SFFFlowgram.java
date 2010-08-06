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
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.util.List;

import org.jcvi.CommonUtil;
import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public class SFFFlowgram implements Flowgram {

    private NucleotideEncodedGlyphs basecalls;
    private EncodedGlyphs<PhredQuality> qualities;
    private Range qualitiesClip;
    private Range adapterClip;
    private List<Short> values;


    /**
     * @param basecalls
     * @param qualities
     * @param values
     * @param qualitiesClip
     * @param adapterClip
     */
    public SFFFlowgram(NucleotideEncodedGlyphs basecalls, EncodedGlyphs<PhredQuality> qualities,
            List<Short> values, Range qualitiesClip, Range adapterClip) {
        canNotBeNull(basecalls, qualities, values, qualitiesClip, adapterClip);
        this.basecalls = basecalls;
        this.qualities = qualities;
        this.values = values;
        this.qualitiesClip = qualitiesClip;
        this.adapterClip = adapterClip;
    }

    private void canNotBeNull(NucleotideEncodedGlyphs basecalls, EncodedGlyphs<PhredQuality> qualities,
            List<Short> values, Range qualitiesClip, Range adapterClip) {
        CommonUtil.cannotBeNull(basecalls, "basecalls can not be null");
        CommonUtil.cannotBeNull(qualities, "qualities can not be null");
        CommonUtil.cannotBeNull(values, "values can not be null");
        CommonUtil.cannotBeNull(qualitiesClip, "qualitiesClip can not be null");
        CommonUtil.cannotBeNull(adapterClip, "adapterClip can not be null");

        if(values.isEmpty()){
            throw new IllegalArgumentException("values can not be empty");
        }
    }

    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return basecalls;
    }

    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        return qualities;
    }

    /**
     * @return the qualityClip
     */
    @Override
    public Range getQualitiesClip() {
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
    public int getSize() {
        return values.size();
    }
    @Override
    public float getValueAt(int index) {
        return SFFUtil.convertFlowgramValue(values.get(index));
    }
    /**
     * Returns the hash code for this {@link SFFFlowgram}.
     * Hash code based on hashcodes for values, qualities, and clip points.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + basecalls.decode().hashCode();
        result = prime * result + values.hashCode();
        result = prime * result + qualities.decode().hashCode();
        result = prime * result + qualitiesClip.hashCode();
        result = prime * result + adapterClip.hashCode();
        
        return result;
    }
    /**
     * Compares this {@link SFFFlowgram} with the specified Object for equality.
     * This method considers two {@link SFFFlowgram} objects equal 
     * only if they are have equal values, qualities and clip points. 
     * (basecalls can be derived from the values so basecalls 
     * are not taken into account).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof SFFFlowgram)){
            return false;
        }
        SFFFlowgram other = (SFFFlowgram) obj;
        
        boolean mostFieldsEqual= 
        CommonUtil.similarTo(basecalls.decode(), other.basecalls.decode()) &&
        CommonUtil.similarTo(qualities.decode(), other.qualities.decode()) &&
        CommonUtil.similarTo(qualitiesClip, other.qualitiesClip) &&
        CommonUtil.similarTo(adapterClip, other.adapterClip);
        
        if(mostFieldsEqual){
            //have to do this because of floating point
            //inaccuracy.. 
            //this might technically break equals and hashcode
            //contract.
            for(int i=0; i< getSize(); i++){
                if ( Math.abs( getValueAt(i) - other.getValueAt(i) ) > .01F ){
                    return false;
                }
            }            
        }
        return true;
        
    }

    
}
