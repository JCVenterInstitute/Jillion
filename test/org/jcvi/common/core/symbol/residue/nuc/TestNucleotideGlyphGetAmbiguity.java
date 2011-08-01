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
        final Nucleotide n = Nucleotide.parse('N');
        assertEquals(n, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Cytosine,Guanine,Thymine)));
        assertEquals(EMPTY_SET, n.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void V(){
        final Nucleotide v = Nucleotide.parse('V');
        assertEquals(v, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Cytosine,Guanine)));
        assertEquals(EMPTY_SET, v.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void H(){
        final Nucleotide h = Nucleotide.parse('H');
        assertEquals(h, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Cytosine,Thymine)));
        assertEquals(EMPTY_SET, h.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void D(){
        final Nucleotide d = Nucleotide.parse('D');
        assertEquals(d, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Guanine,Thymine)));
        assertEquals(EMPTY_SET, d.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void B(){
        final Nucleotide b = Nucleotide.parse('B');
        assertEquals(b, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Cytosine,Guanine,Thymine)));
        assertEquals(EMPTY_SET, b.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void W(){
        final Nucleotide w = Nucleotide.parse('W');
        assertEquals(w, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Thymine)));
        assertEquals(EMPTY_SET, w.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void M(){
        final Nucleotide m = Nucleotide.parse('M');
        assertEquals(m, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Cytosine)));
        assertEquals(EMPTY_SET, m.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void R(){
        final Nucleotide r = Nucleotide.parse('R');
        assertEquals(r, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine,Guanine)));
        assertEquals(EMPTY_SET, r.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void S(){
        final Nucleotide s = Nucleotide.parse('S');
        assertEquals(s, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Cytosine,Guanine)));
        assertEquals(EMPTY_SET, s.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void Y(){
        final Nucleotide y = Nucleotide.parse('Y');
        assertEquals(y, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Cytosine,Thymine)));
        assertEquals(EMPTY_SET, y.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void K(){
        final Nucleotide k = Nucleotide.parse('K');
        assertEquals(k, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Guanine,Thymine)));
        assertEquals(EMPTY_SET, k.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void A(){
        final Nucleotide a = Nucleotide.parse('A');
        assertEquals(a, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Adenine)));
        assertEquals(EnumSet.of(Unknown,NotThymine,NotGuanine,NotCytosine,Weak, Amino,Purine), 
                a.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void G(){
        final Nucleotide g = Nucleotide.parse('G');
        assertEquals(g, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Guanine)));
        assertEquals(EnumSet.of(Unknown,NotThymine,NotCytosine,NotAdenine,Strong, Keto,Purine), 
                g.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void C(){
        final Nucleotide c = Nucleotide.parse('C');
        assertEquals(c, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Cytosine)));
        assertEquals(EnumSet.of(Unknown,NotThymine,NotGuanine,NotAdenine,Amino, Pyrimidine,Strong), 
                c.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void T(){
        final Nucleotide t = Nucleotide.parse('T');
        assertEquals(t, 
                Nucleotide.getAmbiguityFor(Arrays.asList(Thymine)));
        assertEquals(EnumSet.of(Unknown,NotCytosine,NotGuanine,NotAdenine,Keto, Pyrimidine,Weak), 
                t.getUnAmbiguousNucleotidesFor());
    }
    @Test
    public void gap(){
        assertEquals(Nucleotide.parse('-'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap)));
    }
    
    @Test
    public void extendedAShouldReturnA(){
        assertEquals(Nucleotide.parse('A'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap, Adenine)));
    }
    @Test
    public void extendedAShouldReturnC(){
        assertEquals(Nucleotide.parse('C'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap, Cytosine)));
    }
    @Test
    public void extendedAShouldReturnG(){
        assertEquals(Nucleotide.parse('G'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap, Guanine)));
    }
    @Test
    public void extendedAShouldReturnT(){
        assertEquals(Nucleotide.parse('T'), 
                Nucleotide.getAmbiguityFor(Arrays.asList(Gap, Thymine)));
    }
}
