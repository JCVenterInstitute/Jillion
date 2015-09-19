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
 * Created on Mar 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class AssemblyTestUtil {

    public static void assertPlacedReadCorrect(AssembledRead expected,
            AssembledRead actual) {
        String id = expected.getId();
		assertEquals("ids", id, actual.getId());
        assertEquals(id + " startOffset", expected.getGappedStartOffset(), actual.getGappedStartOffset());
        assertEquals(id + " gapped length", expected.getGappedLength(), actual.getGappedLength());
        assertEquals(id, expected.getDirection(), actual.getDirection());
        assertEquals(id, expected.getReadInfo(), actual.getReadInfo());
        final NucleotideSequence expectedEncodedGlyphs = expected.getNucleotideSequence();
        final NucleotideSequence actualEncodedGlyphs = actual.getNucleotideSequence();
        assertEquals(id, expectedEncodedGlyphs, actualEncodedGlyphs);
        
        
    }
}
