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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFCodecs;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfDecoderException;
import org.junit.Test;
public class TestActualSCFCodec {

	 private final static ResourceHelper RESOURCES = new ResourceHelper(TestActualSCFCodec.class);
	   
    private SCFCodec sut = SCFCodecs.VERSION_3;
    @Test
    public void decodeAndEncodeMatch() throws ScfDecoderException, IOException{
        ScfChromatogram decoded = new ScfChromatogramBuilder("id", RESOURCES.getFile("files/GBKAK82TF.scf"))
        							.build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        sut.write(decoded, out);
        ScfChromatogramBuilder builder = new ScfChromatogramBuilder("id", new ByteArrayInputStream(out.toByteArray()));
    	ScfChromatogram decodedAgain = builder.build();        
        assertEquals(decoded, decodedAgain);
        
    }
}
