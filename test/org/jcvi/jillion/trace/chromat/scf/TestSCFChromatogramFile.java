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

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.trace.chromat.scf.ScfChromatogramImpl;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.chromat.ChromatogramXMLSerializer;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestSCFChromatogramFile {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestSCFChromatogramFile.class);
    private static final ScfChromatogramImpl EXPECTED_SCF;
    static{
        try {
            Trace fromXML = ChromatogramXMLSerializer.fromXML(RESOURCES.getFileAsStream("files/GBKAK82TF.scf.xml"));
            EXPECTED_SCF= (ScfChromatogramImpl)fromXML;
        } catch (Exception e) {
            throw new IllegalStateException("could not parse expected chromatogram",e);
        }
    }

    @Test
    public void parseScfFile() throws IOException{
        File scfFile = RESOURCES.getFile("files/GBKAK82TF.scf");
        ScfChromatogram actual = new ScfChromatogramBuilder("id", scfFile)
									.build();
        assertEquals(EXPECTED_SCF, actual);
    }
    
    @Test
    public void scfWithGaps() throws IOException{
        File scfFile = RESOURCES.getFile("files/containsGaps.scf");
        ScfChromatogram actual = new ScfChromatogramBuilder("id", scfFile)
									.build();
        assertEquals(actual.getNucleotideSequence().toString(), "-----");
        
    }
    
   
}
