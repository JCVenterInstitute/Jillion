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
 * Created on Mar 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Ranges;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;

public final class DefaultScaffold  implements Scaffold{
	
	public static ScaffoldBuilder createBuilder(String id){
		return new Builder(id);
	}
	
    private final String id;
    private final Set<PlacedContig> placedContigs;
    private final Map<String, PlacedContig> contigbyId;
    CoverageMap<CoverageRegion<PlacedContig>> contigMap;
    private final long length;
    
    private  DefaultScaffold(String id, Set<PlacedContig> placedContigs){
        this.id = id;
        this.placedContigs = placedContigs;
        contigbyId = new HashMap<String, PlacedContig>();
        for(PlacedContig contig : placedContigs){
            contigbyId.put(contig.getContigId(), contig);
        }
        List<Range> ranges = new ArrayList<Range>(placedContigs.size());
        for(PlacedContig contig : placedContigs){
            ranges.add(Range.create(contig.getBegin(), contig.getEnd()));
        }
        length = Ranges.createInclusiveRange(ranges).getLength();
        DefaultCoverageMap.Builder<PlacedContig> builder =
            new DefaultCoverageMap.Builder<PlacedContig>(placedContigs);
        contigMap = builder.build();
    }
    @Override
    public PlacedContig getPlacedContig(String id) {
        return contigbyId.get(id);
    }

    @Override
    public Set<PlacedContig> getPlacedContigs() {
        return placedContigs;
    }
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public long getLength() {
        return length;
    }
    @Override
    public int getNumberOfContigs() {
        return placedContigs.size();
    }
    @Override
    public CoverageMap<CoverageRegion<PlacedContig>> getContigCoverageMap() {
        return contigMap;
    }
    /**
     * {@inheritDoc}
     */
     @Override
     public boolean hasContig(String contigId) {
         return contigbyId.containsKey(contigId);
     }
    /**
     * Converts contig range coordinates into scaffold range coordinates
     * based on contig's scaffold location and orientation
     * @param placedContigId target scaffold contig
     * @param placedContigRange contig coordinate range to convert of scaffold coordinates
     * @return scaffold coordinates corresponding to input contig id/range values
     * @throws NoSuchElementException if scaffold does not contain target contig
     * @throws IllegalArgumentException if target contig is not oriented in the forward
     * or reverse direction or if the range to be converted is not a subrange of the scaffold's
     * placed contig
     */
    @Override
    public Range convertContigRangeToScaffoldRange(String placedContigId, Range placedContigRange){
        PlacedContig placedContig = getPlacedContig(placedContigId);

        // make sure the source contig exists in the scaffold
        if ( placedContig == null ) {
            throw new NoSuchElementException("Scaffold " + getId()
                + " does not contain the placed contig " + placedContigId);
        }

        // make sure the specified range falls within the placed contig's range
        Range normalizedPlacedContigRange = Range.create(0,placedContig.getLength()-1);
        if ( !placedContigRange.isSubRangeOf(normalizedPlacedContigRange) ) {
            throw new IllegalArgumentException("Specified contig range " + placedContigRange
                + " is not a subrange of its parent placed contig " + placedContig
                + "(normalized range " + normalizedPlacedContigRange + ")");
        }

        if ( placedContig.getDirection() == Direction.FORWARD ) {
            long rightShift = placedContig.getBegin();
            return Range.create(
                    rightShift+placedContigRange.getBegin(),
                    rightShift+placedContigRange.getEnd());
        } else if ( placedContig.getDirection() == Direction.REVERSE ) {
            long leftShift = placedContig.getEnd()-placedContigRange.getBegin();
            return Range.create(
                    leftShift-(placedContigRange.getLength()-1),
                    leftShift);
        } else {
            throw new IllegalArgumentException("Do not know how to convert a(n) " +
                placedContig.getDirection() + " oriented placed contig range " +
                "to its equivalent parent scaffold range");
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((contigbyId == null) ? 0 : contigbyId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (length ^ (length >>> 32));
        result = prime * result
                + ((placedContigs == null) ? 0 : placedContigs.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof DefaultScaffold)){
            return false;
        }
        DefaultScaffold other = (DefaultScaffold) obj;
        if (contigbyId == null) {
            if (other.contigbyId != null){
                return false;
            }
        } else if (!contigbyId.equals(other.contigbyId)){
            return false;
        }
        if (id == null) {
            if (other.id != null){
                return false;
            }
        } else if (!id.equals(other.id)){
            return false;
        }
        if (length != other.length){
            return false;
        }
        if (placedContigs == null) {
            if (other.placedContigs != null){
                return false;
            }
        } else if (!placedContigs.equals(other.placedContigs)){
            return false;
        }
        if (contigMap == null) {
            if (other.contigMap != null ){
                return false;
            }
        } else if (!contigMap.equals(other.contigMap)){
            return false;
        }
        return true;
    }


    private static final class Builder implements ScaffoldBuilder{
        private final String id;
        private Set<PlacedContig> contigs;
        private boolean shiftContigs=false;
        private Builder(String id){
            this.id =id;
            contigs = new TreeSet<PlacedContig>();
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public Builder add(PlacedContig placedContig){
            contigs.add(placedContig);
            return this;
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public Builder add(String contigId, Range contigRange, Direction contigDirection){
           return add(new DefaultPlacedContig(contigId, contigRange,contigDirection));
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public Builder add(String contigId, Range contigRange){
            return add(contigId, contigRange, Direction.FORWARD);
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public ScaffoldBuilder shiftContigs(boolean shiftContigs){
            this.shiftContigs = shiftContigs;
            return this;
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public DefaultScaffold build(){
            if(shiftContigs && !contigs.isEmpty()){
                Set<PlacedContig> shiftedContigs = new TreeSet<PlacedContig>();
                PlacedContig firstContig = contigs.iterator().next();
                long shiftOffset = firstContig.getBegin();
                for(PlacedContig contig : contigs){
                    shiftedContigs.add(new DefaultPlacedContig(contig.getContigId(), contig.getValidRange().shiftLeft(shiftOffset)));
                }
                contigs = shiftedContigs;
            }
            return new DefaultScaffold(id, contigs);
        }
        
    }


    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<String> getContigIds() {
        return contigbyId.keySet().iterator();
    }
    @Override
    public String toString() {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("DefaultScaffold [id=").append(id).append(", length=")
                .append(length).append(", placedContigs=")
                .append(placedContigs).append("]");
        return builder2.toString();
    }


   
   

   

}
