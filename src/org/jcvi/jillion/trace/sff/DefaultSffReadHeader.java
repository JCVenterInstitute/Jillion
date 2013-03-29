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
import org.jcvi.jillion.core.util.ObjectsUtil;

final class DefaultSffReadHeader implements SffReadHeader {
    private final int numberOfBases;
    private final Range qualityClip;
    private final Range adapterClip;
    private final String name;


    /**
     * @param numberOfBases
     * @param qualityClip
     * @param adapterClip
     * @param name
     */
    public DefaultSffReadHeader(int numberOfBases,
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
    public String getId() {
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
        if (!(obj instanceof DefaultSffReadHeader)){
            return false;
        }
        final DefaultSffReadHeader other = (DefaultSffReadHeader) obj;
        return ObjectsUtil.nullSafeEquals(getId(), other.getId())
        && ObjectsUtil.nullSafeEquals(getNumberOfBases(), other.getNumberOfBases())
        && ObjectsUtil.nullSafeEquals(getAdapterClip(), other.getAdapterClip())
        && ObjectsUtil.nullSafeEquals(getQualityClip(), other.getQualityClip());

    }


    public static class Builder implements org.jcvi.jillion.core.util.Builder<DefaultSffReadHeader>{
        private  final int numberOfBases;
        private  Range qualityClip;
        private  final Range adapterClip;
        private  final String name;
        
        public Builder(SffReadHeader header){
            this.numberOfBases = header.getNumberOfBases();
            this.qualityClip = header.getQualityClip();
            this.adapterClip = header.getAdapterClip();
            this.name = header.getId();
        }
        
        public Builder qualityClip(Range clip){
            this.qualityClip = clip;
            return this;
        }
        @Override
        public DefaultSffReadHeader build() {
            return new DefaultSffReadHeader(
                    numberOfBases, 
                    qualityClip, adapterClip, name);
        }
        
    }

}
