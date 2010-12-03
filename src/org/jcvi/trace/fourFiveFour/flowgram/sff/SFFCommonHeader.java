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
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.math.BigInteger;
/**
 * {@code SFFCommonHeader}
 * is the common header in an sff file
 * that contains header information
 * used by all reads in the sff file.
 * @author dkatzel
 *
 *
 */
public interface SFFCommonHeader {
    /**
     * The Index offset is the offset of
     * the optional index of reads in the sff file,
     * if the index is not included, then the offset
     * should be set to 0.
     * @return the offset of the optional index block
     * as an unsigned long (BigInteger)
     */
    BigInteger getIndexOffset();
    /**
     * The Index length is the length of the optional
     * index block.  If the index is not included
     * in the file, then it should be set to 0.
     * @return the length of the optional
     * index block as a positive number.
     */
    long getIndexLength();
    /**
     * The number of reads in this Sff file.
     * @return
     */
    long getNumberOfReads();
    /**
     * The number of flows for each of the reads in the file.
     * @return a positive number.
     */
    int getNumberOfFlowsPerRead();
    /**
     * The nucleotides used for each flow of each read.
     * the lenght of this String should be equal 
     * to {@link #getNumberOfFlowsPerRead()}.
     * @return a non-null String.
     */
    String getFlow();
    /**
     * The nucleotide bases of the key seuqence used for these
     * reads.
     * @return a non-null String, usually 4 bases long.
     */
    String getKeySequence();

}
