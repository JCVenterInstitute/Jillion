/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.orf;

import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.aa.IupacTranslationTables;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestBasicOrfFinder {
    //used an example from wikipedia page of orf
    //https://en.wikipedia.org/w/index.php?title=Open_reading_frame&oldid=774464663
    @Test
    public void wikipediaExample(){
        NucleotideSequence seq = NucleotideSequence.of("ATGCAATGGGGAAATGTTACCAGGTCCGAACTTATTGAGGTAAGACAGATTTAA");
        
        List<Orf> actual = new OrfFinder().find(seq);
        
        List<Orf> expected = Arrays.asList(
                
                new Orf(Frame.ONE , 
                        IupacTranslationTables.STANDARD.translate(seq),                         
                        Range.ofLength(seq.getLength())),
                new Orf(Frame.TWO , 
                        IupacTranslationTables.STANDARD.translate(seq.toBuilder(Range.of(13, 42)).build()),                         
                        Range.of(13, 42)),
                new Orf(Frame.THREE , 
                        IupacTranslationTables.STANDARD.translate(seq.toBuilder(Range.of(5, 37)).build()),                         
                        Range.of(5, 37))
                
                );
        
        assertEquals(expected, actual);
    }
}
