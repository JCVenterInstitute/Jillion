/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
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
