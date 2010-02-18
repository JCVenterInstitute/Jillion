/*
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestMateOrientation {

    @Test
    public void parseChar(){
        assertEquals(MateOrientation.INNIE, MateOrientation.parseMateOrientation('I'));
        assertEquals(MateOrientation.OUTTIE, MateOrientation.parseMateOrientation('O'));
        assertEquals(MateOrientation.NORMAL, MateOrientation.parseMateOrientation('N'));
        assertEquals(MateOrientation.UNORIENTED, MateOrientation.parseMateOrientation('U'));
        assertEquals(MateOrientation.UNORIENTED, MateOrientation.parseMateOrientation('X'));
    }
    @Test
    public void parseString(){
        assertEquals(MateOrientation.INNIE, MateOrientation.parseMateOrientation("I"));
        assertEquals(MateOrientation.OUTTIE, MateOrientation.parseMateOrientation("O"));
        assertEquals(MateOrientation.NORMAL, MateOrientation.parseMateOrientation("N"));
        assertEquals(MateOrientation.UNORIENTED, MateOrientation.parseMateOrientation("U"));
        assertEquals(MateOrientation.UNORIENTED, MateOrientation.parseMateOrientation("X"));
    }
}
