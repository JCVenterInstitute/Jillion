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

package org.jcvi.common.core.assembly.clc.cas.var;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;

/**
 * {@code DefaultVariation} is a default implementation 
 * of {@link Variation}.
 * @author dkatzel
 *
 *
 */
public class DefaultVariation implements Variation{

    private final long coordinate;
    private final List<Nucleotide> consensus;
    private final Nucleotide reference;
    private final Type type;
    private final Map<List<Nucleotide>, Integer> histogram;
    
    
    /**
     * @param coordinate
     * @param type
     * @param reference
     * @param consensus
     * @param histogram
     */
    protected DefaultVariation(long coordinate, Type type,
            Nucleotide reference, List<Nucleotide> consensus,
            Map<List<Nucleotide>, Integer> histogram) {
        this.coordinate = coordinate;
        this.type = type;
        this.consensus = consensus;
        this.reference = reference;
        this.histogram = histogram;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<Nucleotide> getConsensusBase() {
        return consensus;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getCoordinate() {
        return coordinate;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Map<List<Nucleotide>, Integer> getHistogram() {
        return histogram;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Nucleotide getReferenceBase() {
        return reference;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Type getType() {
        return type;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int compareTo(Variation o) {
        return Long.valueOf(getCoordinate()).compareTo(o.getCoordinate());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                +  consensus.hashCode();
        result = prime * result + (int) (coordinate ^ (coordinate >>> 32));
        result = prime * result
                + histogram.hashCode();
        result = prime * result
                + reference.hashCode();
        result = prime * result + type.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultVariation)) {
            return false;
        }
        DefaultVariation other = (DefaultVariation) obj;
        if (!consensus.equals(other.consensus)) {
            return false;
        }
        if (coordinate != other.coordinate) {
            return false;
        }
        if (!histogram.equals(other.histogram)) {
            return false;
        }
        if (!reference.equals(other.reference)) {
            return false;
        }
        if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString(){
        StringBuilder variationList = new StringBuilder();
        for(Nucleotide base : Nucleotides.parse("ACGTN-")){
            final List<Nucleotide> asList = Arrays.asList(base);
            if(histogram.containsKey(asList)){
                variationList.append(String.format("\t%s: %d", base, histogram.get(asList)));
            }
        }
        return String.format("%d %s %s -> %s%s",coordinate,type,reference,consensus, variationList.toString());
    }
    public static class Builder implements org.jcvi.common.core.util.Builder<DefaultVariation>{
        private final long coordinate;
        private final List<Nucleotide> consensus;
        private final Nucleotide reference;
        private final Type type;
        private final Map<List<Nucleotide>, Integer> histogram = new HashMap<List<Nucleotide>, Integer>();
        
        public Builder(long coordinate, Type type,
                Nucleotide reference,
                List<Nucleotide> consensus ){
            if(consensus ==null){
                throw new NullPointerException("consensus can not be null");
            }
            if(consensus.isEmpty()){
                throw new NullPointerException("consensus can not be empty");
            }
            if(type ==null){
                throw new NullPointerException("type can not be null");
            }
            if(reference ==null){
                throw new NullPointerException("reference can not be null");
            }
            if(coordinate <0){
                throw new IllegalArgumentException("coordinate can not be <0");
            }
            this.consensus = consensus;
            this.coordinate = coordinate;
            this.reference = reference;
            this.type = type;
        }
        
        public Builder addHistogramRecord(List<Nucleotide> base, int count){
            histogram.put(base, count);
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultVariation build() {
            return new DefaultVariation(coordinate, type, reference, consensus, histogram);
        }
        
    }

}
