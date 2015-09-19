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
package org.jcvi.jillion.trace.sff;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.sff.SffUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestSffUtil_Linkers {

    @Test
    public void flx(){
        String seq = "GTTGGAACCGAAAGGGTTTGAATTCAAACCCTTTCGGTTCCAAC";
        assertMatches(seq, SffUtil.Linkers.FLX.getForwardSequence());
        assertMatches(seq, SffUtil.Linkers.FLX.getReverseSequence());
    }
    @Test
    public void titanium(){
        assertMatches("TCGTATAACTTCGTATAATGTATGCTATACGAAGTTATTACG", SffUtil.Linkers.TITANIUM.getForwardSequence());
        assertMatches("CGTAATAACTTCGTATAGCATACATTATACGAAGTTATACGA", SffUtil.Linkers.TITANIUM.getReverseSequence());
        
    }
    
    private void assertMatches(String expected, NucleotideSequence actual){
        assertEquals(expected, actual.toString());
    }
}
