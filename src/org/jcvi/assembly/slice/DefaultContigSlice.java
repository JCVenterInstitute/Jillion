/*
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class DefaultContigSlice<T extends PlacedRead> implements ContigSlice<T>{

    private final Location<EncodedGlyphs<NucleotideGlyph>> consensus;
    private final List<SliceLocation<T>> underlyingSliceLocations;
    
    public DefaultContigSlice(Location<EncodedGlyphs<NucleotideGlyph>> consensus,List<SliceLocation<T>> underlyingReads ){
        if(consensus ==null){
            throw new IllegalArgumentException("consensus can not be null");
        }
        if(underlyingReads ==null){
            throw new IllegalArgumentException("underlyingReads can not be null");
        }
        this.consensus = consensus;
        this.underlyingSliceLocations = new ArrayList<SliceLocation<T>>(underlyingReads.size());
        this.underlyingSliceLocations.addAll(underlyingReads);
    }
    @Override
    public Location<EncodedGlyphs<NucleotideGlyph>> getConsensus() {
        return consensus;
    }

    @Override
    public List<SliceLocation<T>> getUnderlyingSliceLocations() {
        return underlyingSliceLocations;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + consensus.hashCode();
        result = prime * result
                + underlyingSliceLocations.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultContigSlice))
            return false;
        DefaultContigSlice other = (DefaultContigSlice) obj;
        if (!consensus.equals(other.consensus)){
            return false;
        }
        if (!underlyingSliceLocations.equals(other.underlyingSliceLocations)){
            return false;
        }
        return true;
    }



}
