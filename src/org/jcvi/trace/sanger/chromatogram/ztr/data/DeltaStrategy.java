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
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
/**
 * There are several different possible Delta strategies
 * that can be used to compute the delta between 2 consecutive
 * values.
 * @author dkatzel
 *
 *
 */
public interface DeltaStrategy {
    /**
     * use the delta strategy computation
     * to uncompress the next value from the given compressed buffer
     * and write it to the given out buffer. 
     * @param compressed buffer containing compressed data.
     * @param out buffer to write uncompressed (undelta'ed) value.
     */
    void unCompress(ByteBuffer compressed, ByteBuffer out);
}
