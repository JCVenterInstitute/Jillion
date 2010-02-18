/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram;


import org.jcvi.Range;
import org.jcvi.trace.Trace;

public interface Flowgram extends Trace {

    Range getAdapterClip();
    Range getQualitiesClip();
    int getSize();
    float getValueAt(int index);
}
