/*
 * Created on Jan 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.Chromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramImpl;
import org.jcvi.trace.sanger.chromatogram.scf.SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.Version3SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.header.DefaultSCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.DefaultSectionCodecFactory;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramParser;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestConvertZtr2Scf {
    ZTRChromatogramParser ztrParser = new ZTRChromatogramParser();
    SCFCodec scfCodec = new Version3SCFCodec(new DefaultSCFHeaderCodec(), new DefaultSectionCodecFactory());
    
    @Test
    public void ztr2scf() throws TraceDecoderException, IOException{
        
        Chromatogram decodedZTR = ztrParser.decode(
                TestConvertZtr2Scf.class.getResourceAsStream("ztr/files/GBKAK82TF.ztr"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        scfCodec.encode(new SCFChromatogramImpl(decodedZTR), out);
        
        Chromatogram encodedScf = scfCodec.decode(new DataInputStream(new ByteArrayInputStream(out.toByteArray())));
        assertEquals(decodedZTR, encodedScf);
    }
    
    @Test
    public void scfequalsZtr() throws TraceDecoderException{
        Chromatogram decodedScf = scfCodec.decode(new DataInputStream(TestConvertZtr2Scf.class.getResourceAsStream("scf/files/GBKAK82TF.scf")));
        Chromatogram decodedZTR = ztrParser.decode(
                TestConvertZtr2Scf.class.getResourceAsStream("ztr/files/GBKAK82TF.ztr"));
        
        assertEquals(decodedZTR, decodedScf);
        
    }
}
