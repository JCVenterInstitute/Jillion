package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;
import org.junit.Test;
import static org.junit.Assert.*;
public class DeflineChainedConverterTest {

    @Test
    public void mapBoth(){
        Defline defline = Defline.of("foo", "bar");
        DeflineConverter sut = DeflineConverters.concatenateComment()
                .andThen(DeflineConverters.convertAllSpacesTo("_"));

        Defline actual = sut.apply(defline);

        assertEquals(Defline.of("foo_bar"), actual);
    }

    @Test
    public void nullConverterShouldThrowNPE(){

        assertThrows(NullPointerException.class,()-> DeflineConverters.concatenateComment().andThen((DeflineConverter) null));
    }
}
