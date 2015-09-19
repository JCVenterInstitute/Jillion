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
package org.jcvi.jillion.trace.chromat.scf;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFCodecs;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestVersion2Parser {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestVersion2Parser.class);
    
    @Test
    public void version2MatchesVersion3() throws IOException{
        Chromatogram version2 = (Chromatogram) SCFCodecs.VERSION_2.decode(RESOURCES.getFile("files/version2.scf"));
        Chromatogram version3 = (Chromatogram) SCFCodecs.VERSION_3.decode(RESOURCES.getFile("files/version3.scf"));
        assertEquals(version3.getNucleotideSequence(),version2.getNucleotideSequence());
        assertEquals(version3.getQualitySequence(),version2.getQualitySequence());
        assertEquals(version3.getPeakSequence(),version2.getPeakSequence());
        assertEquals(version3.getNumberOfTracePositions(), version2.getNumberOfTracePositions());
    
        assertEquals(version3.getChannelGroup(), version2.getChannelGroup());
    }
}
