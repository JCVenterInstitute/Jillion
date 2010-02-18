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
 * Created on Jan 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcvi.Range;
import org.jcvi.RangeArrivalComparator;

public final class DefaultPlacedContigClone<T> implements PlacedContigClone{

    private final Clone clone;
    private final Contig contig;
    private final Placed placed;
    private final Set<T> elements;
    private static final Comparator<Range> RANGE_ARRIVAL_COMPARATOR = new RangeArrivalComparator();
    
    private DefaultPlacedContigClone(Contig contig, Clone clone, Placed placed,Set<T> elements){
        this.contig = contig;
        this.clone = clone;
        this.placed = placed;
        this.elements = elements;
    }
    @Override
    public Clone getClone() {
        return clone;
    }


    public Set<T> getElements() {
        return elements;
    }
    
    @Override
    public Contig getContig() {
        return contig;
    }
    @Override
    public long getEnd() {
        return placed.getEnd();
    }
    @Override
    public long getLength() {
        return placed.getLength();
    }
    @Override
    public long getStart() {
        return placed.getStart();
    }

    
    public static class Builder<T extends Placed>{
        private List<Range> placementRanges;
        private final Clone clone;
        private final Contig contig;
        private Set<T> placedReads;
        public Builder(Contig contig, Clone clone){
            this.contig = contig;
            this.clone = clone;
            this.placedReads = new HashSet<T>();
        }
        
        public Builder add(T placedRead){
            placedReads.add(placedRead);
            return this;
        }
        public Builder addAll(Collection<T> placedReads){
            this.placedReads.addAll(placedReads);
            return this;
        }
        
        public List<DefaultPlacedContigClone> build(){
            List<DefaultPlacedContigClone> result = new ArrayList<DefaultPlacedContigClone>();
            for(Placed placement : convertRangesIntoPlacedObjects(buildPlacementRanges(placedReads))){
                Set<T> filteredReads = filterFor(placement);
                result.add(new DefaultPlacedContigClone(contig,clone, placement, filteredReads)); 
                
            }
            return result;
        }
        
        
       private Set<T> filterFor(Placed placement) {
            Range placementRange = Range.buildRange(placement.getStart(), placement.getEnd());
            Set<T> filtered = new HashSet<T>();
            for(T element : placedReads){
                Range elementRange = Range.buildRange(element.getStart(), element.getEnd());
                if(elementRange.intersects(placementRange)){
                    filtered.add(element);
                }
            }
            return filtered;
        }

    private List<Range>  buildPlacementRanges( Set<T> placedReads){
            placementRanges = new ArrayList<Range>();
            for(T placedRead : placedReads){                
                addToPlacementRanges(placedRead);
                mergeAnyRangesThatCanBeCombined();               
            }
            return placementRanges;
        }
        private  void mergeAnyRangesThatCanBeCombined() {
            boolean merged;
            do{
                merged = false;
                for(int i=0; i<placementRanges.size()-1; i+=2){
                    Range range = placementRanges.get(i);
                    Range nextRange = placementRanges.get(i+1);
                    if(range.intersects(nextRange)){
                        replaceWithCombined(range, nextRange);
                        merged= true;
                        break;
                    }
                }
            }while(merged);
        }
        private  void replaceWithCombined(Range range, Range nextRange) {
            final Range combinedRange = Range.buildInclusiveRange(range,nextRange);
            replaceNewRangeForOld(combinedRange,range,nextRange );                            
                         
            Collections.sort(placementRanges, RANGE_ARRIVAL_COMPARATOR);
        }
        private  void remove(Range... rangesToRemove){
            for(Range range : rangesToRemove){
                placementRanges.remove(range);
            }
        }
        private  void addToPlacementRanges(Placed placedRead) {
            Range readRange = Range.buildRange(placedRead.getStart(), placedRead.getEnd());
            updateListToAccountForNewRange(readRange);
        }
        private  void updateListToAccountForNewRange(Range readRange) {
            if(cannotAddToExistingRange(readRange)){
                placementRanges.add(readRange);
            }
            Collections.sort(placementRanges, RANGE_ARRIVAL_COMPARATOR);
        }
        private  boolean cannotAddToExistingRange(Range readRange) {
            boolean cannotAdd=true;
            for(int i=0; i<placementRanges.size(); i++){
                Range range = placementRanges.get(i);
                if(readRange.intersects(range)){
                    
                    final Range newRange = Range.buildInclusiveRange(range, readRange);
                    replaceNewRangeForOld(newRange, range);
                    cannotAdd = false;
                    break;
                }
            }
            return cannotAdd;
        }
        private  void replaceNewRangeForOld(final Range newRange,
                Range... oldRanges) {
            remove(oldRanges);
            placementRanges.add(newRange);
        }
        private  List<Placed> convertRangesIntoPlacedObjects(
                List<Range> placementRanges) {
            List<Placed> placed = new ArrayList<Placed>(placementRanges.size());
               for(Range r: placementRanges){
                   placed.add(r);
               }
            return placed;
        }
    }
  

}
