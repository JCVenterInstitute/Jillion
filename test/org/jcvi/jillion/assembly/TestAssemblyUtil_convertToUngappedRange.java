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
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAssemblyUtil_convertToUngappedRange {
    private final Range range = Range.of(3,5);
    @Test
    public void noGapsShouldReturnSameRange(){
        assertEquals(range, AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("ACGTACGT").build(), range));
        
    }
    @Test
    public void downstreamGapsShouldReturnSameRange(){
        assertEquals(range, AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("ACGTAC-GT").build(), range));
        
    }
    @Test
    public void upstreamGapShouldShiftRange(){
        assertEquals(new Range.Builder(range).shift(-1).build(), AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("A-CGTACGT").build(), range));
    }
    
    @Test
    public void gapsInsideRangeShouldShiftEndCoordinate(){        
        Range ungappedRagne = Range.of(3,4);
        assertEquals(ungappedRagne, AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("ACGT-ACGT").build(), range));
    }
    
    @Test(expected=NullPointerException.class)
    public void nullSequenceShouldThrowNPE(){
        AssemblyUtil.toUngappedRange(null, range);
    }
    @Test(expected=NullPointerException.class)
    public void nullRangeShouldThrowNPE(){
        AssemblyUtil.toUngappedRange(
                new NucleotideSequenceBuilder("ACGTACGT").build()
                , null);
    }
}
