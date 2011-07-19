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
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.cas.align;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultCasAlignment implements CasAlignment {

    private final long contigSequenceId;
    private final long startOfMatch;
    private final boolean readIsReversed;
    private final List<CasAlignmentRegion> alignmentRegions;
    
    
    /**
     * @param contigSequenceId
     * @param numberOfDeletions
     * @param numberOfInserts
     * @param numberOfMatchesAndMismatches
     * @param startOfMatch
     * @param readIsReversed
     * @param alignmentRegions
     */
    public DefaultCasAlignment(long contigSequenceId,
            long startOfMatch, boolean readIsReversed,
            List<CasAlignmentRegion> alignmentRegions) {
        this.contigSequenceId = contigSequenceId;
        this.startOfMatch = startOfMatch;
        this.readIsReversed = readIsReversed;
        this.alignmentRegions = new ArrayList<CasAlignmentRegion>(alignmentRegions);
    }

    @Override
    public long contigSequenceId() {
        return contigSequenceId;
    }

    @Override
    public List<CasAlignmentRegion> getAlignmentRegions() {
        return Collections.unmodifiableList(alignmentRegions);
    }

  

    @Override
    public long getStartOfMatch() {
        return startOfMatch;
    }

    @Override
    public boolean readIsReversed() {
        return readIsReversed;
    }
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((alignmentRegions == null) ? 0 : alignmentRegions.hashCode());
        result = prime * result
                + (int) (contigSequenceId ^ (contigSequenceId >>> 32));
        result = prime * result + (readIsReversed ? 1231 : 1237);
        result = prime * result + (int) (startOfMatch ^ (startOfMatch >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultCasAlignment)) {
            return false;
        }
        DefaultCasAlignment other = (DefaultCasAlignment) obj;
        if (alignmentRegions == null) {
            if (other.alignmentRegions != null) {
                return false;
            }
        } else if (!alignmentRegions.equals(other.alignmentRegions)) {
            return false;
        }
        if (contigSequenceId != other.contigSequenceId) {
            return false;
        }
        if (readIsReversed != other.readIsReversed) {
            return false;
        }
        if (startOfMatch != other.startOfMatch) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultCasAlignment [contigSequenceId=" + contigSequenceId
                + ", startOfMatch=" + startOfMatch + ", readIsReversed="
                + readIsReversed + ", alignmentRegions=" + alignmentRegions
                + "]";
    }



    public static class Builder implements org.jcvi.common.core.util.Builder<DefaultCasAlignment>{
        private final long contigSequenceId;
        private final long startOfMatch;
        private final boolean readIsReversed;
        
        List<CasAlignmentRegion> regions = new ArrayList<CasAlignmentRegion>();
        private CasAlignmentRegionType currentType;
        private long currentLength;
        /**
         * @param contigSequenceId
         * @param startOfMatch
         * @param readIsReversed
         */
        public Builder(long contigSequenceId, long startOfMatch,
                boolean readIsReversed) {
            this.contigSequenceId = contigSequenceId;
            this.startOfMatch = startOfMatch;
            this.readIsReversed = readIsReversed;
            resetCurrentRegion();
        }

        private synchronized void resetCurrentRegion(){
            currentType=null;
            currentLength=0;
        }

        public synchronized Builder addRegion(CasAlignmentRegionType type, long length){
            if(type== null){
                throw new IllegalArgumentException("type can not be null");
            }
            if(length<1){
                throw new IllegalArgumentException("length can not < 1");
            }
            if(type == currentType){
                currentLength+=length;
            }
            else{
                createAndResetCurrentRegion();
                currentType = type;
                currentLength= length;
            }
            return this;
        }
        
        public synchronized Builder addPhaseChange(byte colorSpacePhaseChange){
            createAndResetCurrentRegion();
            regions.add(new PhaseChangeCasAlignmentRegion(colorSpacePhaseChange));
            return this;
        }

        private void createAndResetCurrentRegion() {
            if(currentType!=null){
                createCurrentRegion();
                resetCurrentRegion();
            }
        }
        
        
        private synchronized void createCurrentRegion() {
            regions.add(new DefaultCasAlignmentRegion(currentType, currentLength));            
        }

        @Override
        public DefaultCasAlignment build() {
            createAndResetCurrentRegion();            
            return new DefaultCasAlignment(contigSequenceId,startOfMatch,readIsReversed, regions);
        }
        
    }

}
