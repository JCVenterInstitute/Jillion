/*
 * Created on Oct 10, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFReadData;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFReadDataCodec;

public abstract class AbstractTestSFFReadDataCodec {
    protected int numberOfFlows = 5;
    protected int numberOfBases=4;

    protected byte[] qualities = new byte[]{20,30,40,35};
    protected short[] values = new short[]{100,8,97,4,200};
    protected byte[] indexes = new byte[]{1,2,2,0};
    protected String bases = "TATT";


    protected DefaultSFFReadDataCodec sut = new DefaultSFFReadDataCodec();

    protected DefaultSFFReadData expectedReadData = new DefaultSFFReadData(bases, indexes,  values,
                                            qualities);
}
