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
package org.jcvi.common.core.assembly.util.coverage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Rangeable;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.ArrayIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;


public final class CoverageMapFactory {

	public static <V extends Rangeable> CoverageMap<V> createFromCoverageRegions(List<CoverageRegion<V>> coverageRegions){
		return new DefaultCoverageMapImpl<V>(coverageRegions);
		
	}
    public static <V extends Rangeable> CoverageMap<V> 
            create(Collection<V> elements){
        return new Builder<V>(elements).build();
    }
    public static <V extends Rangeable,T extends CoverageRegion<V>> CoverageMap<V> 
            create(CloseableIterator<V> elements){
        return new Builder<V>(elements).build();
    }
    public static <V extends Rangeable,T extends CoverageRegion<V>> CoverageMap<V> 
            create(Collection<V> elements, int maxAllowedCoverage){
        return new Builder<V>(elements,maxAllowedCoverage).build();
    }
    public static <PR extends AssembledRead,C extends Contig<PR>, T extends CoverageRegion<PR>> CoverageMap<PR> 
        createGappedCoverageMapFromContig(C contig){
            return new Builder<PR>(contig.getReadIterator()).build();
    }
    /**
     * Create a coverage map in <strong>ungapped consensus coordinate space</strong>
     * of the given contig.
     * @param <PR> the type of {@link AssembledRead}s used in the contig.
     * @param <C> the type of {@link Contig}
     * @param contig the contig to create an ungapped coverage map for.
     * @return a new {@link CoverageMap} but where the coordinates in the coverage map
     * refer to ungapped consensus coordinates instead of gapped coordinates.
     */
    public static <PR extends AssembledRead,C extends Contig<PR>, T extends CoverageRegion<PR>> CoverageMap<PR> 
    createUngappedCoverageMapFromContig(C contig){
    	CoverageMap<PR> gappedCoverageMap = createGappedCoverageMapFromContig(contig);
    	if(contig.getConsensus().getNumberOfGaps()==0){
    		//no gaps so we don't need to recompute anything
    		return gappedCoverageMap;
    	}
    	return createUngappedCoverageMap(contig.getConsensus(), gappedCoverageMap);
    }
    /**
     * Create a coverage map in <strong>ungapped consensus coordinate space</strong>
     * of the given reads aligned to the given consensus.
     * @param <PR> the type of {@link AssembledRead} used.
     * @param consensus the gapped consensus the reads aligned to.
     * @param reads the reads to generate a coverage map for.
     * @return a new {@link CoverageMap} but where the coordinates in the coverage map
     * refer to ungapped coordinates instead of gapped coordinates.
     * 
     */
    public static <PR extends AssembledRead,C extends Contig<PR>, T extends CoverageRegion<PR>> CoverageMap<PR> 
    createUngappedCoverageMap(NucleotideSequence gappedConsensus, Collection<PR> reads){
    	CoverageMap<PR> gappedCoverageMap = create(reads);
    	return createUngappedCoverageMap(gappedConsensus, gappedCoverageMap);
    }
    private static <PR extends AssembledRead,C extends Contig<PR>> CoverageMap<PR> createUngappedCoverageMap(
            NucleotideSequence consensus, CoverageMap<PR> gappedCoverageMap) {
        List<CoverageRegion<PR>> ungappedCoverageRegions = new ArrayList<CoverageRegion<PR>>();
        for(CoverageRegion<PR> gappedCoverageRegion : gappedCoverageMap){
            Range gappedRange = gappedCoverageRegion.asRange();
            Range ungappedRange = AssemblyUtil.toUngappedRange(consensus,gappedRange);
            List<PR> reads = new ArrayList<PR>(gappedCoverageRegion.getCoverage());
            for(PR read : gappedCoverageRegion){
                reads.add(read);
            }
            
            ungappedCoverageRegions.add(
                    new DefaultCoverageRegion.Builder<PR>(ungappedRange.getBegin(),reads)
                                .end(ungappedRange.getEnd())
                                .build());
        }
        
        return new DefaultCoverageMapImpl<PR>(ungappedCoverageRegions);
    }
    public static <PR extends AssembledRead,C extends Contig<PR>, T extends CoverageRegion<PR>> CoverageMap<PR>    
        createGappedCoverageMapFromContig(C contig, int maxAllowedCoverage){
            return new Builder<PR>(contig.getReadIterator(),maxAllowedCoverage).build();
    }
    private static class RangeableStartComparator <T extends Rangeable> implements Comparator<T>,Serializable {       
        /**
         * 
         */
        private static final long serialVersionUID = -8517894363563047881L;

        @Override
        public int compare(T o1, T o2) {           
            return Long.valueOf(o1.asRange().getBegin()).compareTo(o2.asRange().getBegin());
        }

    }
    
    private static class RangeableEndComparator<T extends Rangeable> implements Comparator<T>, Serializable {       
        /**
         * 
         */
        private static final long serialVersionUID = 5135449151100427846L;

        @Override
        public int compare(T o1, T o2) {           
            return Long.valueOf(o1.asRange().getEnd()).compareTo(o2.asRange().getEnd());
        }
            
    }
    
    private CoverageMapFactory(){}
    
    private static class DefaultCoverageMapImpl<V extends Rangeable> implements CoverageMap<V>{
	    private CoverageRegion<V>[] regions;
	    /**
	     *
	     * Creates a new <code>AbstractCoverageMap</code>.
	     * @param amplicons A {@link Collection} of {@link Coordinated}s.
	     */
	    @SuppressWarnings("unchecked")
		private DefaultCoverageMapImpl(List<CoverageRegion<V>> regions){
	        this.regions = regions.toArray(new CoverageRegion[0]);
	    }
	    @Override
	    public int getNumberOfRegions() {
	        return regions.length;
	    }
	    @Override
	    public CoverageRegion<V> getRegion(int i) {
	        return regions[i];
	    }
	
	  
	    @Override
	    public boolean equals(Object obj) {
	        if(this == obj){
	            return true;
	        }
	        if(obj instanceof CoverageMap){
	        	CoverageMap<?> other = (CoverageMap<?>) obj;
	            if(getNumberOfRegions() !=other.getNumberOfRegions()){
	                return false;
	            }
	            for( int i=0; i<getNumberOfRegions(); i++){
	                if(!getRegion(i).equals(other.getRegion(i))){
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
	          for(CoverageRegion<V> region : regions){
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
	        for(CoverageRegion<V> region : regions){
	            buf.append(region);
	            buf.append("\n");
	        }
	        return buf.toString();
	    }
	    
	    @Override
	    public Iterator<CoverageRegion<V>> iterator() {
	        return new ArrayIterator<CoverageRegion<V>>(regions);
	    }
	
	    
	    
	    @Override
	    public List<CoverageRegion<V>> getRegionsWhichIntersect(Range range) {
	        List<CoverageRegion<V>> selectedRegions = new ArrayList<CoverageRegion<V>>();
	        for(CoverageRegion<V> region : regions){
	            Range regionRange = region.asRange();
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
	    public CoverageRegion<V> getRegionWhichCovers(long consensusIndex) {
	        Range range = Range.create(consensusIndex, consensusIndex);
	        final List<CoverageRegion<V>> intersectedRegion = getRegionsWhichIntersect(range);
	        if(intersectedRegion.isEmpty()){
	            return null;
	        }
	        return intersectedRegion.get(0);
	    }
	    
	    @Override
	    public List<CoverageRegion<V>> getRegions() {
	        return Arrays.asList(regions);
	    }
	   
	   
	   
	   
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public long getLength() {
	        if(isEmpty()){
	            return 0L;
	        }
	        return regions[regions.length-1].asRange().getEnd()+1;
	    }
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public boolean isEmpty() {
	        return regions.length==0;
	    }

    }
    
    private static  class Builder<P extends Rangeable> extends AbstractCoverageMapBuilder<P>{
        private final List<P> startCoordinateSortedList = new ArrayList<P>();
        private final List<P> endCoordinateSortedList = new ArrayList<P>();
        
        public Builder(Collection<P> elements, int maxAllowedCoverage){
            super(maxAllowedCoverage);
            initialize(elements);
        }
        public Builder(Collection<P> elements) {
            initialize(elements);
            
        }
        
        public Builder(CloseableIterator<P> elements, int maxAllowedCoverage){
            super(maxAllowedCoverage);
            initialize(elements);
        }
        public Builder(CloseableIterator<P> elements) {
            initialize(elements);
        }
        private final void initialize(Collection<P> collection){
        	initialize(CloseableIteratorAdapter.adapt(collection.iterator()));
        }
        private final void initialize(CloseableIterator<P> elements){
        	try{
        		while(elements.hasNext()){
        			P element = elements.next();
        			startCoordinateSortedList.add(element);
        			endCoordinateSortedList.add(element);
        		}
        	}finally{
        		IOUtil.closeAndIgnoreErrors(elements);
        	}
            filterAmpliconsWithoutCoordinates(startCoordinateSortedList);
            filterAmpliconsWithoutCoordinates(endCoordinateSortedList);
            Collections.sort(startCoordinateSortedList,
                    new RangeableStartComparator<P>());
            Collections.sort(endCoordinateSortedList, new RangeableEndComparator<P>());
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
                if (entry.asRange().getLength() == 0) {
                    it.remove();
                }
            }
        }
        @Override
        protected CoverageRegionBuilder<P> createNewCoverageRegionBuilder(
                Collection<P> elements, long start, Integer maxAllowedCoverage) {
            return new DefaultCoverageRegion.Builder<P>(start, elements,maxAllowedCoverage);
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
        protected CoverageMap<P> build(
                List<CoverageRegionBuilder<P>> coverageRegionBuilders) {
            return new DefaultCoverageMapImpl<P>(
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

  

}



