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
package org.jcvi.jillion.trace.chromat.ztr;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRChromatogramImpl;
import org.jcvi.jillion.trace.chromat.ChromatogramXMLSerializer;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestZTRChromatogramFile {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestZTRChromatogramFile.class);
    private static final ZTRChromatogramImpl EXPECTED_ZTR;
    static{
        
         EXPECTED_ZTR= (ZTRChromatogramImpl)ChromatogramXMLSerializer.fromXML(RESOURCES.getFileAsStream("files/GBKAK82TF.ztr.xml"));
         if(EXPECTED_ZTR ==null){
        	 throw new IllegalStateException("could not find expected chromatogram");
         }
    }
    
    @Test
    public void parseZtrFile() throws IOException{
        File ztrFile = RESOURCES.getFile("files/GBKAK82TF.ztr");
        ZtrChromatogram actual = new ZtrChromatogramBuilder("id",ztrFile).build();
        assertEquals(EXPECTED_ZTR, actual);
    }
    
   
}
