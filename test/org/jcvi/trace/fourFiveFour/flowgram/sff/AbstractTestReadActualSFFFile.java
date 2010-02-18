/*
 * Created on Feb 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import static org.junit.Assert.assertEquals;

import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public class AbstractTestReadActualSFFFile {

    protected void assertSameValues(Flowgram expected, Flowgram actual){
        assertEquals(expected.getBasecalls(), actual.getBasecalls());
        assertEquals(expected.getQualities(), actual.getQualities());
        assertEquals(expected.getSize(), actual.getSize());
        assertEquals(expected.getQualitiesClip(), actual.getQualitiesClip());
        assertEquals(expected.getAdapterClip(), actual.getAdapterClip());
        for(int i=0; i< expected.getSize(); i++){
            assertEquals(i+"th value", expected.getValueAt(i), actual.getValueAt(i), .01F);
        }
        
    }
}
