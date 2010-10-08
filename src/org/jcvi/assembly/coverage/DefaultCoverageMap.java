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
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jcvi.Range;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedEndComparator;
import org.jcvi.assembly.PlacedStartComparator;


public class DefaultCoverageMap<V extends Placed,T extends CoverageRegion<V>> implements CoverageMap<T> {


    public static <V extends Placed,T extends CoverageRegion<V>> DefaultCoverageMap<V,T> 
            buildCoverageMap(Collection<V> elements){
        return (DefaultCoverageMap<V,T>)new Builder(elements).build();
    }

    private List<T> regions;
    private double avgCoverage;
    private boolean avgCoverageSet;
    /**
     *
     * Creates a new <code>AbstractCoverageMap</code>.
     * @param amplicons A {@link Collection} of {@link Coordinated}s.
     */
    public DefaultCoverageMap(List<T> regions){
        this.regions = regions;
    }
    @Override
    public int getNumberOfRegions() {
        return regions.size();
    }
    @Override
    public T getRegion(int i) {
        return regions.get(i);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj instanceof DefaultCoverageMap){
            DefaultCoverageMap other = (DefaultCoverageMap) obj;
            if(getNumberOfRegions() !=other.getNumberOfRegions()){
                return false;
            }
            for( int i=0; i<getNumberOfRegions(); i++){
                if(! getRegion(i).equals(other.getRegion(i))){
                    return false;
                }
            }
            return true;
        }
       return false;
    }

      public int hashCode(){
          final int prime = 37;
          int ret = 17;
          for(T region : regions){
              ret = ret*prime + region.hashCode();
          }
          return ret;
      }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for(T region : regions){
            buf.append(region);
            buf.append("\n");
        }
        return buf.toString();
    }
    
    @Override
    public Iterator<T> iterator() {
        return regions.iterator();
    }

    @Override
    public List<T> getRegionsWithin(Range range) {
        List<T> selectedRegions = new ArrayList<T>();
        for(T region : regions){
            Range regionRange = Range.buildRange(region.getStart(), region.getEnd());
            if(regionRange.isSubRangeOf(range)){
                selectedRegions.add(region);
            }
        }
        return selectedRegions;
    }
    
    @Override
    public List<T> getRegionsWhichIntersect(Range range) {
        List<T> selectedRegions = new ArrayList<T>();
        for(T region : regions){
            Range regionRange = Range.buildRange(region.getStart(), region.getEnd());
            if(range.endsBefore(regionRange)){
                break;
            }
            if(regionRange.intersects(range)){
                selectedRegions.add(region);
            }
            
        }
        return selectedRegions;
    }
    

    @Override
    public T getRegionWhichCovers(long consensusIndex) {
        Range range = Range.buildRange(consensusIndex, consensusIndex);
        final List<T> intersectedRegion = getRegionsWhichIntersect(range);
        if(intersectedRegion.isEmpty()){
            return null;
        }
        return intersectedRegion.get(0);
    }
    
    public static  class Builder<P extends Placed> extends AbstractCoverageMapBuilder<P,CoverageRegion<P>>{
        private final List<P> startCoordinateSortedList = new ArrayList<P>();
        private final List<P> endCoordinateSortedList = new ArrayList<P>();
        
        public Builder(Collection<P> elements) {
            startCoordinateSortedList.addAll(elements);
            endCoordinateSortedList.addAll(elements);
            filterAmpliconsWithoutCoordinates(startCoordinateSortedList);
            filterAmpliconsWithoutCoordinates(endCoordinateSortedList);
            Collections.sort(startCoordinateSortedList,
                    new PlacedStartComparator<P>());
            Collections.sort(endCoordinateSortedList, new PlacedEndComparator<P>());
            
        }
        
        /**
         * If there are no coordinates (start or end are null) then we remove them
         * so they don't mess up our computations.
         * 
         * @param amp
         */
        private void filterAmpliconsWithoutCoordinates(Collection<P> amp) {
            for (Iterator<P> it = amp.iterator(); it.hasNext();) {
                P entry = it.next();
                if (entry.getLength() == 0) {
                    it.remove();
                }
            }
        }
        @Override
        protected CoverageRegionBuilder<P> createNewCoverageRegionBuilder(
                List<P> elements, long start) {
            return new DefaultCoverageRegion.Builder<P>(start, elements);
        }

        private List<CoverageRegion<P>> buildAllCoverageRegions(List<CoverageRegionBuilder<P>> coverageRegionBuilders) {
            
            List<CoverageRegion<P>> regions = new ArrayList<CoverageRegion<P>>(
                    coverageRegionBuilders.size());
            for (CoverageRegionBuilder<P> builder : coverageRegionBuilders) {
                regions.add(builder.build());
            }
            return regions;
        }

        @Override
        protected CoverageMap<CoverageRegion<P>> build(
                List<CoverageRegionBuilder<P>> coverageRegionBuilders) {
            return new DefaultCoverageMap<P,CoverageRegion<P>>(
                    buildAllCoverageRegions(coverageRegionBuilders));
        }

        @Override
        protected Iterator<P> createEnteringIterator() {
            return startCoordinateSortedList.iterator();
        }

        @Override
        protected Iterator<P> createLeavingIterator() {
            return endCoordinateSortedList.iterator();
        }
        
    }

    @Override
    public CoverageMap<T> shiftLeft(int units) {
        List<T> shiftedRegions = new ArrayList<T>(getNumberOfRegions());
        for(T region : this.regions){
            shiftedRegions.add((T)region.shiftLeft(units));
        }
        return new DefaultCoverageMap<V, T>(shiftedRegions);
    }
    @Override
    public CoverageMap<T> shiftRight(int units) {
        List<T> shiftedRegions = new ArrayList<T>(getNumberOfRegions());
        for(T region : this.regions){
            shiftedRegions.add((T)region.shiftRight(units));
        }
        return new DefaultCoverageMap<V, T>(shiftedRegions);
    }
    @Override
    public List<T> getRegions() {
        return Collections.unmodifiableList(regions);
    }
    @Override
    public synchronized double getAverageCoverage() {
        if(avgCoverageSet){
            return avgCoverage;
        }        
        avgCoverage= computeAvgCoverage();
        avgCoverageSet=true;
        return avgCoverage;
    }
    private double computeAvgCoverage() {
        if(getNumberOfRegions()==0){
            //no coverage
            return 0F;
        }
        long total=0;
        long length=0;
        for(T coverageRegion : getRegions()){
            total += coverageRegion.getLength() * coverageRegion.getCoverage();
            length += coverageRegion.getLength();
        }
        return ((double)total)/length;
    }
    @Override
    public int getRegionIndexWhichCovers(long consensusIndex) {
        T region = getRegionWhichCovers(consensusIndex);
        
        return regions.indexOf(region);
    }
    @Override
    public int getMaxCoverage() {
        int maxCoverage=0;
        for(T region : regions){
            maxCoverage = Math.max(maxCoverage, region.getCoverage());
        }
        return maxCoverage;
    }
    @Override
    public int getMinCoverage() {
        if(regions.isEmpty()){
            return 0;
        }
        int minCoverage=Integer.MAX_VALUE;
        for(T region : regions){
            minCoverage = Math.min(minCoverage, region.getCoverage());
        }
        return minCoverage;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getLength() {
        if(isEmpty()){
            return 0L;
        }
        return regions.get(regions.size()-1).getEnd()+1;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isEmpty() {
        return regions.isEmpty();
    }


}



