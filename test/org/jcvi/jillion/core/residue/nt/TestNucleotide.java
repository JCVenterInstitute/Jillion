/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import java.util.Arrays;
import java.util.Collection;


import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import static org.jcvi.jillion.core.residue.nt.Nucleotide.*;
@RunWith(Parameterized.class)
public class TestNucleotide {
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

    public TestNucleotide(Nucleotide glyph,Nucleotide reverseGlyph){
        this.glyph = glyph;
        this.reverseGlyph = reverseGlyph;
    }

    @Test
    public void reverseCompliment(){
        assertEquals(reverseGlyph, glyph.complement());
        assertEquals(glyph, reverseGlyph.complement());
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
    
   


}
