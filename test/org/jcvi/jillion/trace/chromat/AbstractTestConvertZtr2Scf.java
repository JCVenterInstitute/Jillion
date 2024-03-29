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
 * Created on Jan 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
public abstract class AbstractTestConvertZtr2Scf {
    private static final ResourceHelper RESOURCES = new ResourceHelper(AbstractTestConvertZtr2Scf.class);
    
    @Test
    public void ztr2scf() throws IOException, IOException{
        
        Chromatogram decodedZTR = new ZtrChromatogramBuilder("id", RESOURCES.getFile("ztr/files/GBKAK82TF.ztr"))
        											.build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChromatogramWriter writer = createScfWriter(out);
        writer.write(new ScfChromatogramBuilder(decodedZTR).build());
        writer.close();
        out.close();
        
        Chromatogram encodedScf = new ScfChromatogramBuilder("id", new ByteArrayInputStream(out.toByteArray()))
								.build();

        assertEquals(decodedZTR, encodedScf);
    }

	protected abstract ChromatogramWriter createScfWriter(OutputStream out);
    
    @Test
    public void scfequalsZtr() throws IOException, IOException{
        Chromatogram decodedScf = new ScfChromatogramBuilder("id", RESOURCES.getFile("scf/files/GBKAK82TF.scf"))
        							.build();
        Chromatogram decodedZTR = new ZtrChromatogramBuilder("id", RESOURCES.getFile("ztr/files/GBKAK82TF.ztr"))
											.build();
        assertEquals(decodedZTR, decodedScf);        
    }
    /**
     * ZTR files can have no qualities (ex: trash data)
     * but SCF requires the same # of qualities as basecalls
     * so just set them to 0.
     * @throws IOException 
     * @throws IOException 
     */
    @Test
    public void ztrWithNoQualitiesShouldGetPaddedQualitiesInScf() throws IOException, IOException{
        Chromatogram ztr = new ZtrChromatogramBuilder("id", RESOURCES.getFile("ztr/files/515866_G07_AFIXF40TS_026.ab1.afg.trash.ztr"))
												.build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        ChromatogramWriter writer = createScfWriter(out);
        writer.write(new ScfChromatogramBuilder(ztr).build());
        writer.close();
        
        Chromatogram encodedScf = new ScfChromatogramBuilder("id", new ByteArrayInputStream(out.toByteArray()))
										.build();
        
        int numberOfBases = (int)encodedScf.getNucleotideSequence().getLength();
        QualitySequence expectedQualities = new QualitySequenceBuilder(new byte[numberOfBases]).build();
        
        assertEquals(expectedQualities,encodedScf.getQualitySequence());
    }
    
    
}
