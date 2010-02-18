/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.SCFDecoderException;
import org.jcvi.trace.sanger.chromatogram.scf.Version3SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.header.DefaultSCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.DefaultSectionCodecFactory;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestActualSCFCodec {

    private SCFCodec sut = new Version3SCFCodec(new DefaultSCFHeaderCodec(), new DefaultSectionCodecFactory());
    @Test
    public void decodeAndEncodeMatch() throws SCFDecoderException, IOException{
        InputStream in = TestActualSCFCodec.class.getResourceAsStream("files/GBKAK82TF.scf");
        SCFChromatogram decoded = sut.decode(new DataInputStream(in));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        sut.encode(decoded, out);
        SCFChromatogram decodedAgain = sut.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));

        assertEquals(decoded, decodedAgain);
    }
}
