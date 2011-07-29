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
 * Created on Jun 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nuc;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.jcvi.common.core.symbol.residue.nuc.Nucleotide.*;
public class TestNucleotideGlyphGetAmbiguity {
    private static final Set<Nucleotide> EMPTY_SET = EnumSet.noneOf(Nucleotide.class);
    @Test
    public void N(){
        final Nucleotide n = Nucleotide.getGlyphFor('N');
        assertEquals(n, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Cytosine,Guanine,Thymine)));
        assertEquals(EMPTY_SET, n.getNucleotides());
    }
    @Test
    public void V(){
        final Nucleotide v = Nucleotide.getGlyphFor('V');
        assertEquals(v, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Cytosine,Guanine)));
        assertEquals(EMPTY_SET, v.getNucleotides());
    }
    @Test
    public void H(){
        final Nucleotide h = Nucleotide.getGlyphFor('H');
        assertEquals(h, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Cytosine,Thymine)));
        assertEquals(EMPTY_SET, h.getNucleotides());
    }
    @Test
    public void D(){
        final Nucleotide d = Nucleotide.getGlyphFor('D');
        assertEquals(d, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Guanine,Thymine)));
        assertEquals(EMPTY_SET, d.getNucleotides());
    }
    @Test
    public void B(){
        final Nucleotide b = Nucleotide.getGlyphFor('B');
        assertEquals(b, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Cytosine,Guanine,Thymine)));
        assertEquals(EMPTY_SET, b.getNucleotides());
    }
    @Test
    public void W(){
        final Nucleotide w = Nucleotide.getGlyphFor('W');
        assertEquals(w, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Thymine)));
        assertEquals(EMPTY_SET, w.getNucleotides());
    }
    @Test
    public void M(){
        final Nucleotide m = Nucleotide.getGlyphFor('M');
        assertEquals(m, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Cytosine)));
        assertEquals(EMPTY_SET, m.getNucleotides());
    }
    @Test
    public void R(){
        final Nucleotide r = Nucleotide.getGlyphFor('R');
        assertEquals(r, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Guanine)));
        assertEquals(EMPTY_SET, r.getNucleotides());
    }
    @Test
    public void S(){
        final Nucleotide s = Nucleotide.getGlyphFor('S');
        assertEquals(s, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Cytosine,Guanine)));
        assertEquals(EMPTY_SET, s.getNucleotides());
    }
    @Test
    public void Y(){
        final Nucleotide y = Nucleotide.getGlyphFor('Y');
        assertEquals(y, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Cytosine,Thymine)));
        assertEquals(EMPTY_SET, y.getNucleotides());
    }
    @Test
    public void K(){
        final Nucleotide k = Nucleotide.getGlyphFor('K');
        assertEquals(k, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Guanine,Thymine)));
        assertEquals(EMPTY_SET, k.getNucleotides());
    }
    @Test
    public void A(){
        final Nucleotide a = Nucleotide.getGlyphFor('A');
        assertEquals(a, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine)));
        assertEquals(EnumSet.of(Unknown,NotThymine,NotGuanine,NotCytosine,Weak, Amino,Purine), 
                a.getNucleotides());
    }
    @Test
    public void G(){
        final Nucleotide g = Nucleotide.getGlyphFor('G');
        assertEquals(g, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Guanine)));
        assertEquals(EnumSet.of(Unknown,NotThymine,NotCytosine,NotAdenine,Strong, Keto,Purine), 
                g.getNucleotides());
    }
    @Test
    public void C(){
        final Nucleotide c = Nucleotide.getGlyphFor('C');
        assertEquals(c, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Cytosine)));
        assertEquals(EnumSet.of(Unknown,NotThymine,NotGuanine,NotAdenine,Amino, Pyrimidine,Strong), 
                c.getNucleotides());
    }
    @Test
    public void T(){
        final Nucleotide t = Nucleotide.getGlyphFor('T');
        assertEquals(t, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Thymine)));
        assertEquals(EnumSet.of(Unknown,NotCytosine,NotGuanine,NotAdenine,Keto, Pyrimidine,Weak), 
                t.getNucleotides());
    }
    @Test
    public void gap(){
        assertEquals(Nucleotide.getGlyphFor('-'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap)));
    }
    
    @Test
    public void extendedAShouldReturnA(){
        assertEquals(Nucleotide.getGlyphFor('A'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap, Adenine)));
    }
    @Test
    public void extendedAShouldReturnC(){
        assertEquals(Nucleotide.getGlyphFor('C'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap, Cytosine)));
    }
    @Test
    public void extendedAShouldReturnG(){
        assertEquals(Nucleotide.getGlyphFor('G'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap, Guanine)));
    }
    @Test
    public void extendedAShouldReturnT(){
        assertEquals(Nucleotide.getGlyphFor('T'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap, Thymine)));
    }
}
