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

import java.math.BigInteger;

import org.jcvi.CommonUtil;

public class DefaultSFFCommonHeader implements SFFCommonHeader {

    private final BigInteger indexOffset;
    private final long indexLength;
    private final long numberOfReads;
    private final int numberOfFlowsPerRead;
    private final String flow;
    private final String keySequence;



   



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
            String keySequence) {
        this.indexOffset = indexOffset;
        this.indexLength = indexLength;
        this.numberOfReads = numberOfReads;
        this.numberOfFlowsPerRead = numberOfFlowsPerRead;
        this.flow = flow;
        this.keySequence = keySequence;
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((flow == null) ? 0 : flow.hashCode());
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
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        final DefaultSFFCommonHeader other = (DefaultSFFCommonHeader) obj;
        return CommonUtil.similarTo(getFlow(), other.getFlow())
       && CommonUtil.similarTo(getIndexLength(), other.getIndexLength())
        && CommonUtil.similarTo(getIndexOffset(), other.getIndexOffset())
        && CommonUtil.similarTo(getKeySequence(), other.getKeySequence())
        && CommonUtil.similarTo(getNumberOfFlowsPerRead(), other.getNumberOfFlowsPerRead())
        && CommonUtil.similarTo(getNumberOfReads(), other.getNumberOfReads());
    }



    @Override
    public String toString() {
        return "DefaultSFFCommonHeader [flow=" + flow + ", indexLength=" + indexLength
                + ", indexOffset=" + indexOffset + ", keySequence="
                + keySequence + ", numberOfFlowsPerRead="
                + numberOfFlowsPerRead + ", numberOfReads=" + numberOfReads
                + "]";
    }
    
    public static class Builder implements org.jcvi.Builder<DefaultSFFCommonHeader>{

        private BigInteger indexOffset;
        private long indexLength;
        private long numberOfReads;
        private int numberOfFlowsPerRead;
        private String flow;
        private String keySequence;
        
        public Builder(){}
        
        public Builder(SFFCommonHeader copy){
            indexOffset = copy.getIndexOffset();
            indexLength = copy.getIndexLength();
            numberOfReads = copy.getNumberOfReads();
            numberOfFlowsPerRead = copy.getNumberOfFlowsPerRead();
            flow = copy.getFlow();
            keySequence = copy.getKeySequence();
        }
        public Builder withNoIndex(){
            indexOffset=BigInteger.ZERO;
            indexLength=0;
            return this;
        }
        public Builder indexOffset(BigInteger indexOffset){
            this.indexOffset = indexOffset;
            return this;
        }
        public Builder indexLength(long indexLength){
            this.indexLength = indexLength;
            return this;
        }
        
        public Builder numberOfReads(long numberOfReads){
            this.numberOfReads = numberOfReads;
            return this;
        }
        public Builder numberOfFlowsPerRead(int numberOfFlowsPerRead){
            this.numberOfFlowsPerRead = numberOfFlowsPerRead;
            return this;
        }
        
        public Builder keySequence(String keySequence){
            this.keySequence = keySequence;
            return this;
        }
        /**
         * The actual flow sequence used by all the reads.
         * The length of the flow should be equal to
         * {@link #numberOfFlowsPerRead(int)}.  If not specified
         * or set to {@code null} then the default flow
         * sequence will be generated which is the keysequence
         * repeated until the flow length is the correct
         * length.
         * @param flow the flow sequence; or {@code null}
         * to use default.
         * @return this
         */
        public Builder flow(String flow){
            this.flow = flow;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultSFFCommonHeader build() {
            if(flow==null){
                StringBuilder flowBuilder = new StringBuilder();
                for(int i=0; i< numberOfFlowsPerRead; i+=keySequence.length()){
                    flowBuilder.append(keySequence);
                }
                flow=flowBuilder.toString();
            }
            return new DefaultSFFCommonHeader(indexOffset, indexLength,
                    numberOfReads, numberOfFlowsPerRead, flow,
                    keySequence);
        }
        
    }


}
