/*
 * Created on Mar 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import org.jcvi.CommonUtil;
import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Library;
import org.jcvi.trace.Trace;

public class DefaultFragment implements Fragment{
    private final String id;
    private final NucleotideEncodedGlyphs bases;
    private final EncodedGlyphs<PhredQuality> qualities;
    private final Range validRange, vectorClearRange;
    private final String comment;
    private final Library library;
    public DefaultFragment(String id, Trace trace,Range validRange,Range vectorClearRange, Library library,String comment){
        this(id, trace.getBasecalls(), trace.getQualities(),validRange,vectorClearRange,library,comment);
    }
    public DefaultFragment(String id, NucleotideEncodedGlyphs bases,
            EncodedGlyphs<PhredQuality> qualities,Range validRange,Range vectorClearRange, Library library,String comment){
        if(id ==null){
            throw new IllegalArgumentException("id can not be null");
        }
        this.id = id;
        this.validRange = validRange;
        this.bases = bases;
        this.qualities = qualities;
        this.comment = comment;
        this.library = library;
        this.vectorClearRange = vectorClearRange;
    }


    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return bases;
    }

    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        return qualities;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public Range getValidRange() {
        return validRange;
    }


    @Override
    public NucleotideEncodedGlyphs getEncodedGlyphs() {
        return bases;
    }


    @Override
    public long getLength() {
        return bases.getLength();
    }
    public String getComment() {
        return comment;
    }
    @Override
    public Library getLibrary() {
        return library;
    }
    @Override
    public Range getVectorClearRange() {
        return vectorClearRange;
    }
    @Override
    public String getLibraryId() {
        return library.getId();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultFragment)){
            return false;
        }
        DefaultFragment other = (DefaultFragment) obj;
        return CommonUtil.similarTo(getId(), other.getId());
    }

    
}
