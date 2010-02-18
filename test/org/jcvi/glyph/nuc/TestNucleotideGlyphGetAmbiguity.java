/*
 * Created on Jun 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.jcvi.glyph.nuc.NucleotideGlyph.*;
public class TestNucleotideGlyphGetAmbiguity {
    private static final Set<NucleotideGlyph> EMPTY_SET = EnumSet.noneOf(NucleotideGlyph.class);
    @Test
    public void N(){
        final NucleotideGlyph n = NucleotideGlyph.getGlyphFor('N');
        assertEquals(n, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Adenine,Cytosine,Guanine,Thymine)));
        assertEquals(EMPTY_SET, n.getPossibleAmbiguites());
    }
    @Test
    public void V(){
        final NucleotideGlyph v = NucleotideGlyph.getGlyphFor('V');
        assertEquals(v, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Adenine,Cytosine,Guanine)));
        assertEquals(EMPTY_SET, v.getPossibleAmbiguites());
    }
    @Test
    public void H(){
        final NucleotideGlyph h = NucleotideGlyph.getGlyphFor('H');
        assertEquals(h, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Adenine,Cytosine,Thymine)));
        assertEquals(EMPTY_SET, h.getPossibleAmbiguites());
    }
    @Test
    public void D(){
        final NucleotideGlyph d = NucleotideGlyph.getGlyphFor('D');
        assertEquals(d, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Adenine,Guanine,Thymine)));
        assertEquals(EMPTY_SET, d.getPossibleAmbiguites());
    }
    @Test
    public void B(){
        final NucleotideGlyph b = NucleotideGlyph.getGlyphFor('B');
        assertEquals(b, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Cytosine,Guanine,Thymine)));
        assertEquals(EMPTY_SET, b.getPossibleAmbiguites());
    }
    @Test
    public void W(){
        final NucleotideGlyph w = NucleotideGlyph.getGlyphFor('W');
        assertEquals(w, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Adenine,Thymine)));
        assertEquals(EMPTY_SET, w.getPossibleAmbiguites());
    }
    @Test
    public void M(){
        final NucleotideGlyph m = NucleotideGlyph.getGlyphFor('M');
        assertEquals(m, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Adenine,Cytosine)));
        assertEquals(EMPTY_SET, m.getPossibleAmbiguites());
    }
    @Test
    public void R(){
        final NucleotideGlyph r = NucleotideGlyph.getGlyphFor('R');
        assertEquals(r, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Adenine,Guanine)));
        assertEquals(EMPTY_SET, r.getPossibleAmbiguites());
    }
    @Test
    public void S(){
        final NucleotideGlyph s = NucleotideGlyph.getGlyphFor('S');
        assertEquals(s, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Cytosine,Guanine)));
        assertEquals(EMPTY_SET, s.getPossibleAmbiguites());
    }
    @Test
    public void Y(){
        final NucleotideGlyph y = NucleotideGlyph.getGlyphFor('Y');
        assertEquals(y, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Cytosine,Thymine)));
        assertEquals(EMPTY_SET, y.getPossibleAmbiguites());
    }
    @Test
    public void K(){
        final NucleotideGlyph k = NucleotideGlyph.getGlyphFor('K');
        assertEquals(k, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Guanine,Thymine)));
        assertEquals(EMPTY_SET, k.getPossibleAmbiguites());
    }
    @Test
    public void A(){
        final NucleotideGlyph a = NucleotideGlyph.getGlyphFor('A');
        assertEquals(a, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Adenine)));
        assertEquals(EnumSet.of(Unknown,NotThymine,NotGuanine,NotCytosine,Weak, Amino,Purine), 
                a.getPossibleAmbiguites());
    }
    @Test
    public void G(){
        final NucleotideGlyph g = NucleotideGlyph.getGlyphFor('G');
        assertEquals(g, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Guanine)));
        assertEquals(EnumSet.of(Unknown,NotThymine,NotCytosine,NotAdenine,Strong, Keto,Purine), 
                g.getPossibleAmbiguites());
    }
    @Test
    public void C(){
        final NucleotideGlyph c = NucleotideGlyph.getGlyphFor('C');
        assertEquals(c, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Cytosine)));
        assertEquals(EnumSet.of(Unknown,NotThymine,NotGuanine,NotAdenine,Amino, Pyrimidine,Strong), 
                c.getPossibleAmbiguites());
    }
    @Test
    public void T(){
        final NucleotideGlyph t = NucleotideGlyph.getGlyphFor('T');
        assertEquals(t, 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Thymine)));
        assertEquals(EnumSet.of(Unknown,NotCytosine,NotGuanine,NotAdenine,Keto, Pyrimidine,Weak), 
                t.getPossibleAmbiguites());
    }
    @Test
    public void gap(){
        assertEquals(NucleotideGlyph.getGlyphFor('-'), 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Gap)));
    }
    
    @Test
    public void extendedAShouldReturnA(){
        assertEquals(NucleotideGlyph.getGlyphFor('A'), 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Gap, Adenine)));
    }
    @Test
    public void extendedAShouldReturnC(){
        assertEquals(NucleotideGlyph.getGlyphFor('C'), 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Gap, Cytosine)));
    }
    @Test
    public void extendedAShouldReturnG(){
        assertEquals(NucleotideGlyph.getGlyphFor('G'), 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Gap, Guanine)));
    }
    @Test
    public void extendedAShouldReturnT(){
        assertEquals(NucleotideGlyph.getGlyphFor('T'), 
                NucleotideGlyph.getAmbiguityFor(Arrays.asList(Gap, Thymine)));
    }
}
