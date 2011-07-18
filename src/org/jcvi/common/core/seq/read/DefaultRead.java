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
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read;

import org.jcvi.CommonUtil;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;

public class DefaultRead<T extends NucleotideSequence> implements Read<T>{
    private final String id;
    private final T glyphs;
    public DefaultRead(String id, T glyphs){
        this.id= id;
        this.glyphs = glyphs;
    }
    @Override
    public T getEncodedGlyphs() {
        return glyphs;
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public long getLength() {
        return glyphs.getLength();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    /**
     * Two Reads are equal if they have the same id.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof Read)){
            return false;
        }
        Read other = (Read) obj;
        return CommonUtil.similarTo(id, other.getId());
        
    }
    @Override
    public String toString() {
        return "read : " + getId() + "  validRange" + getEncodedGlyphs().getValidRange()+"  " + getEncodedGlyphs().decode().toString();
    }

    

}
