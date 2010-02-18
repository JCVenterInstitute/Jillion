/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

public interface SFFReadData {

    short[] getFlowgramValues();
    byte[] getFlowIndexPerBase();
    String getBasecalls();
    byte[] getQualities();

}
