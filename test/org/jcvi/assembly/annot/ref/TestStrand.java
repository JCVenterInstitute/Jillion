/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import org.jcvi.assembly.annot.Strand;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestStrand {
    
    @Test
    public void testToString(){
        assertEquals("+", Strand.FORWARD.toString());
        assertEquals("-", Strand.REVERSE.toString());
    }
    
    @Test
    public void oppositeStrand(){
        assertEquals(Strand.FORWARD, Strand.REVERSE.oppositeStrand());
        assertEquals(Strand.REVERSE, Strand.FORWARD.oppositeStrand());
    }
}
