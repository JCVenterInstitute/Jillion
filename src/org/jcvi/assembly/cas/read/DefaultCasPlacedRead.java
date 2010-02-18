/*
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.util.Collections;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public class DefaultCasPlacedRead implements CasPlacedRead{

    private final Read read;
    private final Range validRange;
    private final long startOffset;
    private final SequenceDirection dir;
    public DefaultCasPlacedRead(Read read, long startOffset,Range validRange, SequenceDirection dir){
        this.read= read;
        this.validRange = validRange;
        this.startOffset = startOffset;
        this.dir= dir;
    }
    @Override
    public long getEnd() {
        return startOffset+getLength()-1;
    }
    @Override
    public long getLength() {
        return read.getLength();
    }
    @Override
    public long getStart() {
        return startOffset;
    }
    @Override
    public NucleotideEncodedGlyphs getEncodedGlyphs() {
        return read.getEncodedGlyphs();
    }
    @Override
    public String getId() {
        return read.getId();
    }
    public SequenceDirection getDirection() {
        return dir;
    }
    @Override
    public String toString() {
        return "DefaultCasPlacedRead [startOffset=" + startOffset
                + ", validRange=" + validRange + ", dir=" + dir + ", read="
                + read + "]";
    }
    @Override
    public long convertReferenceIndexToValidRangeIndex(long referenceIndex) {
        throw new UnsupportedOperationException("no reference");
    }
    @Override
    public long convertValidRangeIndexToReferenceIndex(long validRangeIndex) {
        throw new UnsupportedOperationException("no reference");
    }
    @Override
    public SequenceDirection getSequenceDirection() {
        return dir;
    }
    @Override
    public Map<Integer, NucleotideGlyph> getSnps() {
        return Collections.emptyMap();
    }
    @Override
    public Range getValidRange() {
        return validRange;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((read == null) ? 0 : read.hashCode());
        result = prime * result + (int) (startOffset ^ (startOffset >>> 32));
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DefaultCasPlacedRead))
            return false;
        DefaultCasPlacedRead other = (DefaultCasPlacedRead) obj;
        if (read == null) {
            if (other.read != null)
                return false;
        } else if (!read.equals(other.read))
            return false;
        if (startOffset != other.startOffset)
            return false;
        return true;
    }
   
}
