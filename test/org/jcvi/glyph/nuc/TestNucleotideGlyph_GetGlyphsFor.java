/*
 * Created on Nov 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestNucleotideGlyph_GetGlyphsFor {

    @Test
    public void convertGlyph(){
        for(NucleotideGlyph g: NucleotideGlyph.values()){
            final Character uppercase = g.getCharacter();
            assertEquals(g, NucleotideGlyph.getGlyphFor(uppercase));
            assertEquals(g, NucleotideGlyph.getGlyphFor(Character.toLowerCase(uppercase)));
        }
    }
    @Test
    public void convertXToN(){
        assertEquals(NucleotideGlyph.Unknown, NucleotideGlyph.getGlyphFor('X'));
        assertEquals(NucleotideGlyph.Unknown, NucleotideGlyph.getGlyphFor('x'));
    }
}
