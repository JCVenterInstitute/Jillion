/*
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr;


import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChromatogramXMLSerializer;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramImpl;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramParser;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestZTRChromatogramParser {

    ZTRChromatogramParser sut = new ZTRChromatogramParser();
    static ZTRChromatogramImpl expected = (ZTRChromatogramImpl)ChromatogramXMLSerializer.fromXML(TestZTRChromatogramParser.class.getResourceAsStream("files/GBKAK82TF.ztr.xml"));

    @Test
    public void parse() throws TraceDecoderException{
        ZTRChromatogramImpl actual =sut.decode(TestZTRChromatogramParser.class.getResourceAsStream("files/GBKAK82TF.ztr"));
        assertEquals(expected,actual);
        assertNull(actual.getComment());
        assertEquals(actual.getClip(), expected.getClip());
    }
}
