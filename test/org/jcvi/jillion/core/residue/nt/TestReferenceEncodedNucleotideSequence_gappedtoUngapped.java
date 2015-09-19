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
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
public class TestReferenceEncodedNucleotideSequence_gappedtoUngapped {

    String reference       = "ACGTACGTTACGTTACGT";
    String gappedBasecalls = "ACGTACGT-ACGTACG-T";
    String ungappedBasecalls = "ACGTACGTACGTACGT";
    BasicNucleotideCodec codec = BasicNucleotideCodec.INSTANCE;
    NucleotideSequence encodedReference = new NucleotideSequenceBuilder(reference).build();
    DefaultReferenceEncodedNucleotideSequence sut = new DefaultReferenceEncodedNucleotideSequence(encodedReference, gappedBasecalls,0);
    
    
    @Test
    public void convertGappedToUngapped_beforeGapsShouldReturnSameNumber(){
        assertEquals(0,sut.getUngappedOffsetFor(0));
        assertEquals(7,sut.getUngappedOffsetFor(7));
        
        assertEquals(0, sut.getGappedOffsetFor(0));
        assertEquals(7,sut.getGappedOffsetFor(7));
    }
    
    @Test
    public void indexOfGapToUngappedIndexShouldReturnIndexMinusNumGaps(){
        final int indexOfFirstGap = gappedBasecalls.indexOf('-');
        assertEquals(indexOfFirstGap -1, sut.getUngappedOffsetFor(indexOfFirstGap));
    }
    
    @Test
    public void convertGappedToUngappedOneGapShouldReturnIndexMinusOne(){
        final int indexOfFirstGap = gappedBasecalls.indexOf('-');
        assertEquals(2, sut.getNumberOfGaps());
        assertEquals(indexOfFirstGap,sut.getUngappedOffsetFor(indexOfFirstGap+1));
        assertEquals(indexOfFirstGap-1,sut.getUngappedOffsetFor(indexOfFirstGap));
    }
    @Test
    public void convertGappedToUngappedLastIndexShouldReturnLengthMinusNumberOfGaps(){
        int numberOfGaps=2;
        int gappedLength = gappedBasecalls.length();
        int lastGappedIndex = gappedLength-1;
        final int lastUngappedIndex = lastGappedIndex-numberOfGaps;
        assertEquals(lastUngappedIndex, sut.getUngappedOffsetFor(lastGappedIndex));
        assertEquals(lastGappedIndex, sut.getGappedOffsetFor(lastUngappedIndex));
        assertEquals(numberOfGaps, sut.getNumberOfGaps());
    }
    
    
}
