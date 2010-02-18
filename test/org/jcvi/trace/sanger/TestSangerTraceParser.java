/*
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.scf.Version3SCFCodec;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramParser;
import org.jcvi.trace.sanger.phd.PhdCodec;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSangerTraceParser {

    private static final String ZTR_FILE = "chromatogram/ztr/files/GBKAK82TF.ztr";
    private static final String SCF3_FILE = "chromatogram/scf/files/GBKAK82TF.scf";
    private static final String PHD_FILE = "phd/files/1095595674585.phd.1";
    
    SangerTraceParser sut = SangerTraceParser.getInstance();
    
    @Test
    public void parseZTR() throws TraceDecoderException{
        SangerTrace actual =sut.decode(TestSangerTraceParser.class.getResourceAsStream(ZTR_FILE));
        SangerTrace expected = new ZTRChromatogramParser().decode(TestSangerTraceParser.class.getResourceAsStream(ZTR_FILE));
        assertEquals(expected, actual);
    }
    @Test
    public void parseSCF_v3() throws TraceDecoderException{
        SangerTrace actual =sut.decode(TestSangerTraceParser.class.getResourceAsStream(SCF3_FILE));
        SangerTrace expected = new Version3SCFCodec().decode(TestSangerTraceParser.class.getResourceAsStream(SCF3_FILE));
        assertEquals(expected, actual);
    }
    @Test
    public void parsePhd() throws TraceDecoderException{
        SangerTrace actual =sut.decode(TestSangerTraceParser.class.getResourceAsStream(PHD_FILE));
        SangerTrace expected = new PhdCodec().decode(TestSangerTraceParser.class.getResourceAsStream(PHD_FILE));
        assertEquals(expected, actual);
    }
}
