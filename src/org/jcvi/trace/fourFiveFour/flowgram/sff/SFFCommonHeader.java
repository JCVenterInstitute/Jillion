/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.math.BigInteger;

public interface SFFCommonHeader {

    BigInteger getIndexOffset();
    long getIndexLength();
    long getNumberOfReads();
    int getHeaderLength();
    int getNumberOfFlowsPerRead();
    String getFlow();
    String getKeySequence();

}
