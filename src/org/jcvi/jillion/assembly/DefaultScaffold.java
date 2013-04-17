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
 * Created on Mar 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageMapBuilder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
/**
 * {@code DefaultScaffold} is a {@link Scaffold}
 * implementation that stores all {@link PlacedContig}s
 * data internally in various {@link Map}s and {@link Set}s.
 * @author dkatzel
 *
 */
public final class DefaultScaffold  implements Scaffold{
	
	
	
    private final String id;
    private final SortedSet<PlacedContig> placedContigs;
    private final Map<String, PlacedContig> contigbyId;
    CoverageMap<PlacedContig> contigMap;
    private final long length;
    /**
     * Create a new {@link ScaffoldBuilder} instance.  
     * This instance will not yet have any contigs.
     * @param id the id of the Scaffold to be built.
     * This will be the value returned by {@link Scaffold#getId()}.
     * @return a new empty {@link ScaffoldBuilder}; never null.
     */
    public static ScaffoldBuilder createBuilder(String id){
		return new Builder(id);
	}
    private  DefaultScaffold(String id, SortedSet<PlacedContig> placedContigs){
        this.id = id;
        this.placedContigs= placedContigs;
        contigbyId = new HashMap<String, PlacedContig>();
        for(PlacedContig contig : placedContigs){
            contigbyId.put(contig.getContigId(), contig);
        }
        List<Range> ranges = new ArrayList<Range>(placedContigs.size());
        for(PlacedContig contig : placedContigs){
            ranges.add(Range.of(contig.getBegin(), contig.getEnd()));
        }
        length = Ranges.createInclusiveRange(ranges).getLength();
        contigMap =new CoverageMapBuilder<PlacedContig>(placedContigs).build();
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
    public CoverageMap<PlacedContig> getContigCoverageMap() {
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
        Range normalizedPlacedContigRange = Range.of(0,placedContig.getLength()-1);
        if ( !placedContigRange.isSubRangeOf(normalizedPlacedContigRange) ) {
            throw new IllegalArgumentException("Specified contig range " + placedContigRange
                + " is not a subrange of its parent placed contig " + placedContig
                + "(normalized range " + normalizedPlacedContigRange + ")");
        }
        final Range.Builder rangeBuilder; 
        if ( placedContig.getDirection() == Direction.FORWARD ) {
        	rangeBuilder= new Range.Builder(placedContigRange);
        } else{
        	//contig is reversed
        	Range reverseComplementRange = AssemblyUtil.reverseComplementValidRange(placedContigRange, placedContig.getLength());
			rangeBuilder= new Range.Builder(reverseComplementRange);
        }
        rangeBuilder.shift(placedContig.getBegin());
        return rangeBuilder.build();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + contigbyId.hashCode();
        result = prime * result + id.hashCode();
        result = prime * result + (int) (length ^ (length >>> 32));
        result = prime * result
                + placedContigs.hashCode();
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
        if(!contigbyId.equals(other.contigbyId)){
            return false;
        }
        if (!id.equals(other.id)){
            return false;
        }
        if (length != other.length){
            return false;
        }
        if (!placedContigs.equals(other.placedContigs)){
            return false;
        }
        if (!contigMap.equals(other.contigMap)){
            return false;
        }
        return true;
    }
    private static final class PlacedContigComparator implements
		Comparator<PlacedContig> , Serializable{

		private static final long serialVersionUID = 101208868003843457L;

	@Override
	public int compare(PlacedContig o1, PlacedContig o2) {
		int rangeCmp = Range.Comparators.ARRIVAL.compare(o1.asRange(), o2.asRange());
		if(rangeCmp !=0){
			return rangeCmp;
		}
		return o1.getContigId().compareTo(o2.getContigId());
	}
}

    private static final class Builder implements ScaffoldBuilder{
      
		private final String id;
        private SortedSet<PlacedContig> contigs;
        private boolean shiftContigs=false;
        private boolean built=false;
        
        private Builder(String id){
            this.id =id;
            contigs = new TreeSet<PlacedContig>( new PlacedContigComparator());
        }
        
        private synchronized void throwErrorIfBuilt(){
        	if(built){
        		throw new IllegalStateException("already built");
        	}
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public synchronized Builder add(PlacedContig placedContig){
        	throwErrorIfBuilt();
        	if(placedContig ==null){
        		throw new NullPointerException("placed contig can not be null");
        	}
            contigs.add(placedContig);
            return this;
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public synchronized Builder add(String contigId, Range contigRange, Direction contigDirection){
           return add(new DefaultPlacedContig(contigId, contigRange,contigDirection));
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public synchronized Builder add(String contigId, Range contigRange){
            return add(contigId, contigRange, Direction.FORWARD);
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public synchronized ScaffoldBuilder shiftContigsToOrigin(boolean shiftContigs){
        	throwErrorIfBuilt();
            this.shiftContigs = shiftContigs;
            return this;
        }
        /**
		 * {@inheritDoc}
		 */
        @Override
		public synchronized DefaultScaffold build(){
        	throwErrorIfBuilt();
            if(shiftContigs && !contigs.isEmpty()){
                SortedSet<PlacedContig> shiftedContigs = new TreeSet<PlacedContig>(new PlacedContigComparator());
                PlacedContig firstContig = contigs.first();
                long shiftOffset = -firstContig.getBegin();
                for(PlacedContig contig : contigs){
                    shiftedContigs.add(
                    		new DefaultPlacedContig(contig.getContigId(),
                    				new Range.Builder(contig.getValidRange())
                    							.shift(shiftOffset)
                    							.build(),
                    				contig.getDirection()));
                }
                contigs = shiftedContigs;
            }
            built=true;
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
        StringBuilder builder2 = new StringBuilder(75);
        builder2.append("DefaultScaffold [id=").append(id).append(", length=")
                .append(length).append(", placedContigs=")
                .append(placedContigs).append(']');
        return builder2.toString();
    }


   
   

   

}
