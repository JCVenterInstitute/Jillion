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
