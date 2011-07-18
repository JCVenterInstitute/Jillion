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

import org.jcvi.CommonUtil;
import org.jcvi.Range;

public class DefaultSFFReadHeader implements SFFReadHeader {
    private final int numberOfBases;
    private final Range qualityClip;
    private final Range adapterClip;
    private final String name;


    /**
     * @param headerLength
     * @param numberOfBases
     * @param qualityClip
     * @param adapterClip
     * @param name
     */
    public DefaultSFFReadHeader(int numberOfBases,
            Range qualityClip, Range adapterClip, String name) {
        this.numberOfBases = numberOfBases;
        this.qualityClip = qualityClip;
        this.adapterClip = adapterClip;
        this.name = name;
        
    }

    @Override
    public Range getAdapterClip() {
        return adapterClip;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getNumberOfBases() {
        return numberOfBases;
    }

    @Override
    public Range getQualityClip() {
        return qualityClip;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + numberOfBases;
        result = prime * result
                + ((qualityClip == null) ? 0 : qualityClip.hashCode());
        result = prime * result
        + ((adapterClip == null) ? 0 : adapterClip.hashCode());
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
        if (!(obj instanceof DefaultSFFReadHeader)){
            return false;
        }
        final DefaultSFFReadHeader other = (DefaultSFFReadHeader) obj;
        return CommonUtil.similarTo(getName(), other.getName())
        && CommonUtil.similarTo(getNumberOfBases(), other.getNumberOfBases())
        && CommonUtil.similarTo(getAdapterClip(), other.getAdapterClip())
        && CommonUtil.similarTo(getQualityClip(), other.getQualityClip());

    }


    public static class Builder implements org.jcvi.Builder<DefaultSFFReadHeader>{
        private  int numberOfBases;
        private  Range qualityClip;
        private  Range adapterClip;
        private  String name;
        
        public Builder(SFFReadHeader header){
            this.numberOfBases = header.getNumberOfBases();
            this.qualityClip = header.getQualityClip();
            this.adapterClip = header.getAdapterClip();
            this.name = header.getName();
        }
        
        public Builder qualityClip(Range clip){
            this.qualityClip = clip;
            return this;
        }
        @Override
        public DefaultSFFReadHeader build() {
            return new DefaultSFFReadHeader(
                    numberOfBases, 
                    qualityClip, adapterClip, name);
        }
        
    }

}
