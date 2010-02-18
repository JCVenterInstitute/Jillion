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

import org.jcvi.CommonUtil;

public class DefaultSFFCommonHeader implements SFFCommonHeader {

    private final BigInteger indexOffset;
    private final long indexLength;
    private final long numberOfReads;
    private final int numberOfFlowsPerRead;
    private final String flow;
    private final String keySequence;
    private final int headerLength;



   



    /**
     * @param indexOffset
     * @param indexLength
     * @param numberOfReads
     * @param numberOfFlowsPerRead
     * @param flow
     * @param keySequence
     * @param headerLength
     */
    public DefaultSFFCommonHeader(BigInteger indexOffset, long indexLength,
            long numberOfReads, int numberOfFlowsPerRead, String flow,
            String keySequence, int headerLength) {
        this.indexOffset = indexOffset;
        this.indexLength = indexLength;
        this.numberOfReads = numberOfReads;
        this.numberOfFlowsPerRead = numberOfFlowsPerRead;
        this.flow = flow;
        this.keySequence = keySequence;
        this.headerLength = headerLength;
    }



    @Override
    public String getFlow() {
        return flow;
    }



    @Override
    public long getIndexLength() {
        return indexLength;
    }

    @Override
    public BigInteger getIndexOffset() {
        return indexOffset;
    }


    @Override
    public String getKeySequence() {
        return keySequence;
    }

    @Override
    public int getNumberOfFlowsPerRead() {
        return numberOfFlowsPerRead;
    }

    @Override
    public long getNumberOfReads() {
        return numberOfReads;
    }


    @Override
    public int getHeaderLength() {
        return headerLength;
    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((flow == null) ? 0 : flow.hashCode());
        result = prime * result + headerLength;
        result = prime * result + (int) (indexLength ^ (indexLength >>> 32));
        result = prime * result
                + ((indexOffset == null) ? 0 : indexOffset.hashCode());
        result = prime * result
                + ((keySequence == null) ? 0 : keySequence.hashCode());
        result = prime * result + numberOfFlowsPerRead;
        result = prime * result
                + (int) (numberOfReads ^ (numberOfReads >>> 32));
        return result;
    }





    



    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DefaultSFFCommonHeader other = (DefaultSFFCommonHeader) obj;
        return CommonUtil.similarTo(getFlow(), other.getFlow())
        && CommonUtil.similarTo(getHeaderLength(), other.getHeaderLength())
       && CommonUtil.similarTo(getIndexLength(), other.getIndexLength())
        && CommonUtil.similarTo(getIndexOffset(), other.getIndexOffset())
        && CommonUtil.similarTo(getKeySequence(), other.getKeySequence())
        && CommonUtil.similarTo(getNumberOfFlowsPerRead(), other.getNumberOfFlowsPerRead())
        && CommonUtil.similarTo(getNumberOfReads(), other.getNumberOfReads());
    }



    @Override
    public String toString() {
        return "DefaultSFFCommonHeader [flow=" + flow + ", headerLength="
                + headerLength + ", indexLength=" + indexLength
                + ", indexOffset=" + indexOffset + ", keySequence="
                + keySequence + ", numberOfFlowsPerRead="
                + numberOfFlowsPerRead + ", numberOfReads=" + numberOfReads
                + "]";
    }


}
