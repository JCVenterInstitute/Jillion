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
 * Created on Oct 13, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.math.BigInteger;

import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSFFCommonHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSFFCommonHeaderCodec;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFUtil;

public class AbstractTestDefaultSFFCommonHeaderCodec {

    protected BigInteger indexOffset=BigInteger.valueOf(100000L);
    protected int indexLength = 2000;
    protected int numberOfReads = 5;
    protected short numberOfFlowsPerRead = 12;
    protected String flow = "TCAGTCAGTCAG";
    protected String keySequence = "TCAG";
    protected short headerLength = (short)(31+numberOfFlowsPerRead+SFFUtil.caclulatePaddedBytes(31+numberOfFlowsPerRead));

    protected DefaultSFFCommonHeader expectedHeader = new DefaultSFFCommonHeader(indexOffset,  indexLength,
             numberOfReads,  numberOfFlowsPerRead,  flow,
             keySequence);

    protected DefaultSFFCommonHeaderCodec sut = new DefaultSFFCommonHeaderCodec();
}
