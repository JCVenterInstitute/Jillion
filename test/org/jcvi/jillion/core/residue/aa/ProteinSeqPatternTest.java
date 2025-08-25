package org.jcvi.jillion.core.residue.aa;

import org.junit.Test;

import java.util.regex.Pattern;
import static org.junit.Assert.*;

public class ProteinSeqPatternTest {

    @Test
    public void basicSubString(){
        Pattern sut = ProteinSequencePattern.compile("ILMK");

        assertEquals("ILMK", sut.pattern());
    }

    @Test
    public void withWildCardPlus(){
        Pattern sut = ProteinSequencePattern.compile("ILMK+");

        assertEquals("ILMK+", sut.pattern());
    }

    @Test
    public void withWildCardAsterisk(){
        Pattern sut = ProteinSequencePattern.compile("ILMK.*");

        assertEquals("ILMK.*", sut.pattern());
    }
    @Test
    public void withAmbiguityOnly(){
        Pattern sut = ProteinSequencePattern.compile("Z");

        assertEquals("[ZQE]", sut.pattern());
    }

    @Test
    public void withAmbiguity(){
        Pattern sut = ProteinSequencePattern.compile("IZK");

        assertEquals("I[ZQE]K", sut.pattern());
    }
    @Test
    public void withX(){
        Pattern sut = ProteinSequencePattern.compile("X");

        assertEquals("[XILKMFTWVCQGPSYRHANDEUO]", sut.pattern());
        assertTrue(sut.matcher("N").matches());
        assertTrue(sut.matcher("X").matches());
    }

    //NX[S|T] where X != proline
    @Test
    public void glycosolation(){
        Pattern sut = ProteinSequencePattern.compile("N[^P][ST]");

        assertEquals("N[^P][ST]", sut.pattern());

        assertTrue(sut.matcher("NIS").matches());
        assertTrue(sut.matcher("NIT").matches());
        assertFalse(sut.matcher("NPT").matches());
        assertFalse(sut.matcher("PIT").matches());
    }
}
