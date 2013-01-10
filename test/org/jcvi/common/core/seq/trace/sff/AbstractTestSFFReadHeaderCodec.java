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
package org.jcvi.common.core.seq.trace.sff;

import org.jcvi.common.core.seq.trace.sff.DefaultSffReadHeader;
import org.jcvi.common.core.seq.trace.sff.DefaultSffReadHeaderDecoder;
import org.jcvi.common.core.seq.trace.sff.SffUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;

public class AbstractTestSFFReadHeaderCodec {
    protected int numberOfBases=100;
    protected int qual_left = 5;
    protected int qual_right= 100;
    protected  int adapter_left = 10;
    protected  int adapter_right= 100;
    protected  Range qualityClip = Range.of(CoordinateSystem.RESIDUE_BASED, qual_left, qual_right);
    protected Range adapterClip= Range.of(CoordinateSystem.RESIDUE_BASED, adapter_left, adapter_right);
    protected String name = "sequence name";
    protected short headerLength= (short)(16+name.length()+SffUtil.caclulatePaddedBytes(16+name.length()));

    protected DefaultSffReadHeader expectedReadHeader = new DefaultSffReadHeader(numberOfBases,
            qualityClip, adapterClip, name);
    protected DefaultSffReadHeaderDecoder sut = DefaultSffReadHeaderDecoder.INSTANCE;
}
