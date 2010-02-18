/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit.glyph;

import java.util.List;

import org.jcvi.assembly.edit.EditException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;

public abstract class AbstractInsertConsensusBaseEdit<G extends Glyph,E extends EncodedGlyphs<G>> extends AbstractGlyphEdit<G,E>{

    private final G newBase;
    private final int offset;
    
    /**
     * @param tobeInserted
     * @param offset
     */
    public AbstractInsertConsensusBaseEdit(G tobeInserted, int offset) {
        this.newBase = tobeInserted;
        this.offset = offset;
    }


    @Override
    public E performEdit(E original) throws EditException {
        List<G> decoded = original.decode();
        decoded.add(offset,newBase);
        return encode(decoded);
    }

}
