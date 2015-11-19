/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DefaultCasAlignment implements CasAlignment {

    private final long contigSequenceId;
    private final long startOfMatch;
    private final boolean readIsReversed;
    private final List<CasAlignmentRegion> alignmentRegions;
    
    
    
    public DefaultCasAlignment(long contigSequenceId,
            long startOfMatch, boolean readIsReversed,
            List<CasAlignmentRegion> alignmentRegions) {
        this.contigSequenceId = contigSequenceId;
        this.startOfMatch = startOfMatch;
        this.readIsReversed = readIsReversed;
        this.alignmentRegions = new ArrayList<CasAlignmentRegion>(alignmentRegions);
    }

    @Override
    public long getReferenceIndex() {
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
                + alignmentRegions.hashCode();
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
        if (!alignmentRegions.equals(other.alignmentRegions)) {
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



    public static final class Builder implements org.jcvi.jillion.core.util.Builder<DefaultCasAlignment>{
        private final long contigSequenceId;
        private final long startOfMatch;
        private final boolean readIsReversed;
        
        List<CasAlignmentRegion> regions = new ArrayList<CasAlignmentRegion>();
        private CasAlignmentRegionType currentType;
        private long currentLength;
        

        public Builder(long contigSequenceId, long startOfMatch,
                boolean readIsReversed) {
            this.contigSequenceId = contigSequenceId;
            this.startOfMatch = startOfMatch;
            this.readIsReversed = readIsReversed;
            resetCurrentRegion();
        }
        public Builder(CasAlignment copy) {
            this.contigSequenceId = copy.getReferenceIndex();
            this.startOfMatch = copy.getStartOfMatch();
            this.readIsReversed = copy.readIsReversed();
            for(CasAlignmentRegion region : copy.getAlignmentRegions()){
                if(region instanceof PhaseChangeCasAlignmentRegion){
                    addPhaseChange(((PhaseChangeCasAlignmentRegion)region).getPhaseChange());
                }else{
                    addRegion(region.getType(), region.getLength());
                }
            }
        }

        private synchronized void resetCurrentRegion(){
            currentType=null;
            currentLength=0;
        }

        public final synchronized Builder addRegion(CasAlignmentRegionType type, long length){
            if(type== null){
                throw new IllegalArgumentException("type can not be null");
            }
            if(length<1){
                throw new IllegalArgumentException("length can not < 1");
            }
            if(type.equals(currentType)){
                currentLength+=length;
            }
            else{
                createAndResetCurrentRegion();
                currentType = type;
                currentLength= length;
            }
            return this;
        }
        
        public final synchronized Builder addPhaseChange(byte colorSpacePhaseChange){
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
        public final DefaultCasAlignment build() {
            createAndResetCurrentRegion();            
            return new DefaultCasAlignment(contigSequenceId,startOfMatch,readIsReversed, regions);
        }
        
    }

}
