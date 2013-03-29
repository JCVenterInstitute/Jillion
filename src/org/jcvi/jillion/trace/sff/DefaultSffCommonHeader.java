/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.math.BigInteger;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.ObjectsUtil;

final class DefaultSffCommonHeader implements SffCommonHeader {

    private final BigInteger indexOffset;
    private final long indexLength;
    private final long numberOfReads;
    private final int numberOfFlowsPerRead;
    private final NucleotideSequence flow;
    private final NucleotideSequence keySequence;
    
    /**
     * @param indexOffset
     * @param indexLength
     * @param numberOfReads
     * @param numberOfFlowsPerRead
     * @param flow
     * @param keySequence
     */
    DefaultSffCommonHeader(BigInteger indexOffset, long indexLength,
            long numberOfReads, int numberOfFlowsPerRead, NucleotideSequence flow,
            NucleotideSequence keySequence) {
        this.indexOffset = indexOffset;
        this.indexLength = indexLength;
        this.numberOfReads = numberOfReads;
        this.numberOfFlowsPerRead = numberOfFlowsPerRead;
        this.flow = flow;
        this.keySequence = keySequence;
    }



    @Override
    public NucleotideSequence getFlowSequence() {
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
    public NucleotideSequence getKeySequence() {
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
        final DefaultSffCommonHeader other = (DefaultSffCommonHeader) obj;
        return ObjectsUtil.nullSafeEquals(getFlowSequence(), other.getFlowSequence())
       && ObjectsUtil.nullSafeEquals(getIndexLength(), other.getIndexLength())
        && ObjectsUtil.nullSafeEquals(getIndexOffset(), other.getIndexOffset())
        && ObjectsUtil.nullSafeEquals(getKeySequence(), other.getKeySequence())
        && ObjectsUtil.nullSafeEquals(getNumberOfFlowsPerRead(), other.getNumberOfFlowsPerRead())
        && ObjectsUtil.nullSafeEquals(getNumberOfReads(), other.getNumberOfReads());
    }



    @Override
    public String toString() {
        return "DefaultSFFCommonHeader [flow=" + flow + ", indexLength=" + indexLength
                + ", indexOffset=" + indexOffset + ", keySequence="
                + keySequence + ", numberOfFlowsPerRead="
                + numberOfFlowsPerRead + ", numberOfReads=" + numberOfReads
                + "]";
    }
    
    public static class Builder implements org.jcvi.jillion.core.util.Builder<DefaultSffCommonHeader>{

        private BigInteger indexOffset;
        private long indexLength;
        private long numberOfReads;
        private int numberOfFlowsPerRead;
        private NucleotideSequence flow;
        private NucleotideSequence keySequence;
        /**
         * Creates a new Builder instance 
         * where all the fields need to be set.
         */
        public Builder(){
        	//creates an empty builder 
        }
        /**
         * Creates a new Builder instance
         * that initially contains
         * the same values as the fields
         * in the given copy.
         * @param copy the SffCommonHeader to copy;
         * can not be null.
         * @throws NullPointerException if copy is null.
         */
        public Builder(SffCommonHeader copy){
            indexOffset = copy.getIndexOffset();
            indexLength = copy.getIndexLength();
            numberOfReads = copy.getNumberOfReads();
            numberOfFlowsPerRead = copy.getNumberOfFlowsPerRead();
            flow = copy.getFlowSequence();
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
        
        public Builder keySequence(NucleotideSequence keySequence){
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
        public Builder flow(NucleotideSequence flow){
            this.flow = flow;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultSffCommonHeader build() {
            if(flow==null){
            	NucleotideSequenceBuilder flowBuilder = new NucleotideSequenceBuilder(numberOfFlowsPerRead);
            	int keyLength = (int)keySequence.getLength();
                for(int i=0; i< numberOfFlowsPerRead; i+=keyLength){
                    flowBuilder.append(keySequence);
                }
                flow=flowBuilder.build();
            }
            return new DefaultSffCommonHeader(indexOffset, indexLength,
                    numberOfReads, numberOfFlowsPerRead, flow,
                    keySequence);
        }
        
    }


}
