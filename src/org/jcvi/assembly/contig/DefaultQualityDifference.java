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
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.common.core.seq.Sequence;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;
import org.jcvi.common.core.seq.qual.PhredQuality;

public class DefaultQualityDifference implements BasecallDifference{

    private final PhredQuality quality;
    private final Location<PlacedRead> read;
    private final Location<Sequence<NucleotideGlyph>> reference;
    /**
     * @param reference
     * @param read
     * @param quality
     */
    public DefaultQualityDifference(
            Location<Sequence<NucleotideGlyph>> reference,
            Location<PlacedRead> read, PhredQuality quality) {
        if(reference ==null){
            throw new IllegalArgumentException("can not have a null reference");
        }
        if(read ==null){
            throw new IllegalArgumentException("can not have a null read");
        }
        this.reference = reference;
        this.read = read;
        this.quality = quality;
    }
    public final PhredQuality getQuality() {
        return quality;
    }
    public final Location<PlacedRead> getReadLocation() {
        return read;
    }
    public final Location<Sequence<NucleotideGlyph>> getReferenceLocation() {
        return reference;
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(read.getSource().getId());
        result.append("  quality = ");
        result.append(quality);
        result.append(" has a ");
        result.append(read.getSource().getEncodedGlyphs().get(read.getIndex()));
        result.append("@ ");
        result.append(read.getIndex());
        result.append(" reference has a ");
        result.append(reference.getSource().get(reference.getIndex()));
        result.append("@ ");
        result.append(reference.getIndex());
        
        return result.toString();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + quality.hashCode();
        result = prime * result + read.hashCode();
        result = prime * result + reference.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultQualityDifference)){
            return false;
        }
        DefaultQualityDifference other = (DefaultQualityDifference) obj;
        if (quality != other.quality){
            return false;
        }
        if (!read.equals(other.read)){
            return false;
        }
        if (!reference.equals(other.reference)){
            return false;
        }
        return true;
    }
    
    
    
}
