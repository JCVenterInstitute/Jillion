/*
 * Created on Apr 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Tests to see if SCF parser can handle 
 * data that is not A,C,G or T.  SCF spec
 * says that kind  of data is stored in the T channel.
 * @author dkatzel
 *
 *
 */
public class TestSCFChromatogramWithGaps {

    private static final String File_path = "files/containsGaps.scf";
    
    @Test
    public void parse() throws SCFDecoderException{
        SCFChromatogram actual =new Version3SCFCodec().decode(TestSCFChromatogramWithGaps.class.getResourceAsStream(File_path));
        assertEquals(NucleotideGlyph.convertToString(actual.getBasecalls().decode()), "-----");
        
    }
}
