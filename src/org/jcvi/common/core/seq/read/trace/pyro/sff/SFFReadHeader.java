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
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.common.core.Range;
/**
 * {@code SFFReadHeader} contains
 * the information about the a specific
 * SFF Read.
 * @author dkatzel
 *
 *
 */
public interface SFFReadHeader {
    /**
     * The number of bases called for this read.
     * @return a positive number.
     */
    int getNumberOfBases();
    /**
     * The quality clip points that
     * specify the subset of the basecalls
     * that are good quality.  If no
     * clip is set, then the Range should be
     * equal to Range.create(0,0);
     * @return a Range (never null).
     */
    Range getQualityClip();
    /**
     * The adapter clip points that
     * specify the subset of the basecalls
     * that are not adapter sequence.  If no
     * clip is set, then the Range should be
     * equal to Range.create(0,0);
     * @return a Range (never null).
     */
    Range getAdapterClip();
    /**
     * The name of this read.
     * @return a non-null String.
     */
    String getName();

}
