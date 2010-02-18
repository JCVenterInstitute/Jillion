/*
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestReadTriplet {

    @Test
    public void readTriplet(){
        int expectedTriplet = 5071214;
        int actualTriplet = Base64.readTriplet(new byte[]{77,97,110}, 0);
        assertEquals(expectedTriplet, actualTriplet);
        
    }
}
