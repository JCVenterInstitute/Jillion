/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.Range;

public interface SFFReadHeader {
    short getHeaderLength();
    int getNumberOfBases();
    Range getQualityClip();
    Range getAdapterClip();
    String getName();

}
