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
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nuc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import static org.jcvi.common.core.symbol.residue.nuc.Nucleotide.*;
@RunWith(Parameterized.class)
public class TestNucleotideGlyph {
    private Nucleotide glyph,reverseGlyph;
    @Parameters
    public static Collection<?> data(){

        return Arrays.asList(new Object[][]{
                {Gap, Gap},
                {Unknown, Unknown},
                {Adenine, Thymine},
                {Cytosine, Guanine},

                {Purine, Pyrimidine},
                {Amino, Keto},
                {Weak, Weak},
                {Strong, Strong},

                {NotAdenine, NotThymine},
                {NotCytosine, NotGuanine}
        });

    }

    public TestNucleotideGlyph(Nucleotide glyph,Nucleotide reverseGlyph){
        this.glyph = glyph;
        this.reverseGlyph = reverseGlyph;
    }

    @Test
    public void reverseCompliment(){
        assertEquals(reverseGlyph, glyph.compliment());
        assertEquals(glyph, reverseGlyph.compliment());
        assertEquals(glyph.isAmbiguity(), reverseGlyph.isAmbiguity());
    }
    @Test
    public void isGap(){
        assertEquals(glyph == Gap, glyph.isGap());
    }
    
    @Test
    public void isAmbiguity(){
        
        assertEquals(computeExpectedIsAmbiguity(glyph), glyph.isAmbiguity());
        assertEquals(computeExpectedIsAmbiguity(reverseGlyph), reverseGlyph.isAmbiguity());
    }

    private boolean computeExpectedIsAmbiguity(Nucleotide glyph) {
        boolean expectedIsAmbiguity = glyph != Gap && glyph!=Adenine 
        && glyph!=Thymine && glyph !=Cytosine && glyph != Guanine;
        return expectedIsAmbiguity;
    }
    
    @Test
    public void reverseComplimentList(){
        List<Nucleotide> forward = Arrays.asList(Adenine,Cytosine,Guanine, Thymine, Gap,Cytosine);
        List<Nucleotide> expectedReversed = Arrays.asList(Guanine, Gap, Adenine,Cytosine,Guanine, Thymine);
        
        assertEquals(expectedReversed, Nucleotide.reverseCompliment(forward));
    }


}
