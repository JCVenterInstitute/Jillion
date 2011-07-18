/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSFFReadHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSFFReadHeaderCodec;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFUtil;

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

    protected DefaultSFFReadHeader expectedReadHeader = new DefaultSFFReadHeader(numberOfBases,
            qualityClip, adapterClip, name);
    protected DefaultSFFReadHeaderCodec sut = new DefaultSFFReadHeaderCodec();
}
