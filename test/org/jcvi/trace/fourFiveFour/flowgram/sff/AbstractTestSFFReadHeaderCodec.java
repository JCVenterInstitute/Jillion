/*
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFReadHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFReadHeaderCodec;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFUtil;

public class AbstractTestSFFReadHeaderCodec {
    protected int numberOfBases=100;
    protected int qual_left = 5;
    protected int qual_right= 100;
    protected  int adapter_left = 10;
    protected  int adapter_right= 100;
    protected  Range qualityClip = Range.buildRange(CoordinateSystem.RESIDUE_BASED, qual_left, qual_right);
    protected Range adapterClip= Range.buildRange(CoordinateSystem.RESIDUE_BASED, adapter_left, adapter_right);
    protected String name = "sequence name";
    protected short headerLength= (short)(16+name.length()+SFFUtil.caclulatePaddedBytes(16+name.length()));

    protected DefaultSFFReadHeader expectedReadHeader = new DefaultSFFReadHeader(headerLength, numberOfBases,
            qualityClip, adapterClip, name);
    protected DefaultSFFReadHeaderCodec sut = new DefaultSFFReadHeaderCodec();
}
