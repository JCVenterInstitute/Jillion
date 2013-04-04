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
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.coverage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * {@code CoverageMapFactory} is a factory class
 * that is able to build various kinds of
 * {@link CoverageMap}s.
 * 
 * @author dkatzel
 *
 */
final class CoverageMapFactory {

	/**
	 * Create a new {@link CoverageMap} using the given
	 * {@link Rangeable}s.
	 * @param elements the elements to create a coverage map of.
	 * @return a new {@link CoverageMap}; never null.
	 * @param <R> The type of {@link Rangeable} used in this map.
	 */
    public static <R extends Rangeable> CoverageMap<R> 
            create(Collection<R> elements){
        return new Builder<R>(elements).build();
    }
    /**
	 * Create a new {@link CoverageMap} using the given
	 * {@link Rangeable}s but limiting the max coverage
	 * in the map to {@code maxAllowedCoverage}.  
	 * @param elements the elements to create a coverage map of.
	 * @param maxAllowedCoverage Any
	 * elements that would cause the max coverage to exceed this threshold
	 * will be ignored.
	 * @return a new {@link CoverageMap}; never null.
	 * @param <R> The type of {@link Rangeable} used in this map.
	 */
    public static <R extends Rangeable> CoverageMap<R> 
            create(Collection<R> elements, int maxAllowedCoverage){
        return new Builder<R>(elements,maxAllowedCoverage).build();
    }
    /**
     * Create a new coverage map of the {@link AssembledRead}s
     * from the given contig.  The {@link CoverageRegion}'s coordinates
     * will be consensus gapped coordinates.
     * @param contig the contig to build a {@link CoverageMap} of.
     * @param <R> the type of {@link AssembledRead}s used in the contig.
     * @param <C> the type of {@link Contig}
     * @return a new {@link CoverageMap}; never null.
     */
    public static <R extends AssembledRead,C extends Contig<R>> CoverageMap<R> 
        createGappedCoverageMapFromContig(C contig){
            return new Builder<R>(contig.getReadIterator()).build();
    }
    /**
     * Create a coverage map in <strong>ungapped consensus coordinate space</strong>
     * of the given contig.
     * @param <R> the type of {@link AssembledRead}s used in the contig.
     * @param <C> the type of {@link Contig}
     * @param contig the contig to create an ungapped coverage map for.
     * @return a new {@link CoverageMap} but where the coordinates in the coverage map
     * refer to ungapped consensus coordinates instead of gapped coordinates.
     */
    public static <R extends AssembledRead,C extends Contig<R>> CoverageMap<R> 
    createUngappedCoverageMapFromContig(C contig){
    	CoverageMap<R> gappedCoverageMap = createGappedCoverageMapFromContig(contig);
    	if(contig.getConsensusSequence().getNumberOfGaps()==0){
    		//no gaps so we don't need to recompute anything
    		return gappedCoverageMap;
    	}
    	return createUngappedCoverageMap(contig.getConsensusSequence(), gappedCoverageMap);
    }
    /**
     * Create a coverage map in <strong>ungapped consensus coordinate space</strong>
     * of the given reads aligned to the given consensus.
     * @param gappedConsensus the gapped consensus the reads aligned to.
     * @param reads the reads to generate a coverage map for.
     * @return a new {@link CoverageMap} but where the coordinates in the coverage map
     * refer to ungapped coordinates instead of gapped coordinates.
     * @param <R> the type of {@link AssembledRead}s used in the contig.
     */
    public static <R extends AssembledRead> CoverageMap<R> 
    createUngappedCoverageMap(NucleotideSequence gappedConsensus, Collection<R> reads){
    	CoverageMap<R> gappedCoverageMap = create(reads);
    	return createUngappedCoverageMap(gappedConsensus, gappedCoverageMap);
    }
    public static <R extends AssembledRead> CoverageMap<R> createUngappedCoverageMap(
            NucleotideSequence consensus, CoverageMap<R> gappedCoverageMap) {
        List<CoverageRegion<R>> ungappedCoverageRegions = new ArrayList<CoverageRegion<R>>();
        for(CoverageRegion<R> gappedCoverageRegion : gappedCoverageMap){
            Range gappedRange = gappedCoverageRegion.asRange();
            Range ungappedRange = AssemblyUtil.toUngappedRange(consensus,gappedRange);
            List<R> reads = new ArrayList<R>(gappedCoverageRegion.getCoverageDepth());
            for(R read : gappedCoverageRegion){
                reads.add(read);
            }
            
            ungappedCoverageRegions.add(
                    new DefaultCoverageRegion.Builder<R>(ungappedRange.getBegin(),reads)
                                .end(ungappedRange.getEnd())
                                .build());
        }
        
        return new CoverageMapImpl<R>(ungappedCoverageRegions);
    }
    /**
     * Create a new coverage map of the {@link AssembledRead}s
     * from the given contig but limiting the max coverage
	 * in the map to {@code maxAllowedCoverage}.  The {@link CoverageRegion}'s coordinates
     * will be consensus gapped coordinates.
     * @param contig the contig to build a {@link CoverageMap} of.
     * @param maxAllowedCoverage Any
	 * elements that would cause the max coverage to exceed this threshold
	 * will be ignored.
     * @param <R> the type of {@link AssembledRead}s used in the contig.
     * @param <C> the type of {@link Contig}
     * @return a new {@link CoverageMap}; never null.
     */
    public static <R extends AssembledRead,C extends Contig<R>> CoverageMap<R>    
        createGappedCoverageMapFromContig(C contig, int maxAllowedCoverage){
            return new Builder<R>(contig.getReadIterator(),maxAllowedCoverage).build();
    }
    private static class RangeableStartComparator <T extends Rangeable> implements Comparator<T>,Serializable {       
        /**
         * 
         */
        private static final long serialVersionUID = -8517894363563047881L;

        @Override
        public int compare(T o1, T o2) {           
        	 long o1End= o1.asRange().getBegin();
             long o2End = o2.asRange().getBegin();
             if(o1End ==o2End){
             	return 0;
             }
             if(o1End < o2End){
             	return -1;
             }
             return 1;
        }

    }
    
    private static class RangeableEndComparator<T extends Rangeable> implements Comparator<T>, Serializable {       
        /**
         * 
         */
        private static final long serialVersionUID = 5135449151100427846L;

        @Override
        public int compare(T o1, T o2) {    
        	
            long o1End= o1.asRange().getEnd();
            long o2End = o2.asRange().getEnd();
            if(o1End ==o2End){
            	return 0;
            }
            if(o1End < o2End){
            	return -1;
            }
            return 1;
        }
            
    }
    
    private CoverageMapFactory(){}
    
    private static final class CoverageMapImpl<V extends Rangeable> implements CoverageMap<V>{
	    private final CoverageRegion<V>[] regions;
	    /**
	     * The avg coverage of this coverage map
	     * we will lazy load this value
	     * since it could be expensive to compute.
	     */
	    private Double avgCoverage =null;
	    
	    /**
	     *
	     * Creates a new <code>CoverageMapImpl</code>.
	     * @param amplicons A {@link Collection} of {@link Coordinated}s.
	     */
	    @SuppressWarnings("unchecked")
		private CoverageMapImpl(List<CoverageRegion<V>> regions){
	        this.regions = regions.toArray(new CoverageRegion[regions.size()]);
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
	    public synchronized double getAverageCoverage(){
	        if(avgCoverage ==null){
		    	long totalLength = 0L;
		        long totalCoverage =0L;
		        for(CoverageRegion<?> region : this){
		        	long regionLength = region.asRange().getLength();
					totalLength +=regionLength;
					totalCoverage += region.getCoverageDepth() * regionLength;
		        }
		        if(totalLength==0L){
		        	avgCoverage=0D;
		        }else{
		        	avgCoverage = totalCoverage/(double)totalLength;
		        }
	        }
	        return avgCoverage;
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
	            buf.append('\n');
	        }
	        return buf.toString();
	    }
	    @Override
	    public List<CoverageRegion<V>> getRegionsWhichIntersect(Range range) {
	    	if(range ==null){
	    		throw new NullPointerException("range can not be null");
	    	}
	    	if(range.isEmpty()){
	    		//empty ranges never intersect anything
	    		return Collections.emptyList();
	    	}
	    	if(regions[0].asRange().getBegin() > range.getEnd()){
	    		//region is entirely before coverage map
	    		return Collections.emptyList();
	    	}
	    	if(regions[regions.length-1].asRange().getEnd() < range.getBegin()){
	    		//region is entirely after coverage map
	    		return Collections.emptyList();
	    	}
	    	CoverageRegion<V> fakeRegion = new DefaultCoverageRegion.Builder<V>(range.getBegin(), Collections.<V>emptyList())
	    											.end(range.getEnd())
	    											.build();
	    	
	    	int beginIndex =Arrays.binarySearch(regions, fakeRegion, CoverageRegionComparators.BY_BEGIN);
	    	int endIndex =Arrays.binarySearch(regions, fakeRegion, CoverageRegionComparators.BY_END);
	    	
	    	
	    	//Arrays.binarySearch will return a negative
	    	//(index+1) if the key isn't found but the 
	    	//absolute value -1 is where the key 
	    	//WOULD be if it was in the array
	    	//which is good enough for our intersection
	    	//so we need to adjust the offset by either 1 if it's
	    	//the end index or 2
	    	//if its the beginIndex
	    	//to get
	    	//the flanking region to be included
	    	
	    	int correctedBeginIndex = Math.max(0, beginIndex<0? Math.abs(beginIndex) -2 : beginIndex);
	    	int correctedEndIndex = Math.min(regions.length -1, endIndex <0? Math.abs(endIndex)-1  : endIndex);
	    	
	    	
	    	int numberOfRegionsIntersected = correctedEndIndex-correctedBeginIndex +1;
	    	
	    	List<CoverageRegion<V>> intersectedRegions = new ArrayList<CoverageRegion<V>>(numberOfRegionsIntersected);
	    	for(int i=correctedBeginIndex; i<=correctedEndIndex; i++){
	    		if(i< regions.length){
	    			intersectedRegions.add(regions[i]);
	    		}
	    	}
	    	return intersectedRegions;	    	
	    }
	    @Override
	    public CoverageRegion<V> getRegionWhichCovers(long consensusIndex) {
	        Range range = Range.of(consensusIndex, consensusIndex);
	        final List<CoverageRegion<V>> intersectedRegion = getRegionsWhichIntersect(range);
	        if(intersectedRegion.isEmpty()){
	            return null;
	        }
	        return intersectedRegion.get(0);
	    }
	    
	    @Override
	    public Iterator<CoverageRegion<V>> iterator() {
	        return Arrays.asList(regions).iterator();
	    }
	
	   
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public boolean isEmpty() {
	        return regions.length==0;
	    }

    }
    
    
    private static enum CoverageRegionComparators implements Comparator<CoverageRegion<?>>{
		//Comparators can't use the Range Comparators 
    	//because those comparators use not only being and end
    	//coordinates but range length as well to determine 
    	//if comparator returns 0
    	BY_BEGIN(new Comparator<Range>(){

			@Override
			public int compare(Range o1, Range o2) {
				long l1 =o1.getBegin();
				long l2 =o2.getBegin();
				if(l1 ==l2){
					return 0;
				}
				if(l1< l2){
					return -1;
				}
				return 1;
			}			
		}
		),
		BY_END(new Comparator<Range>(){

			@Override
			public int compare(Range o1, Range o2) {
				long l1 =o1.getEnd();
				long l2 =o2.getEnd();
				if(l1 ==l2){
					return 0;
				}
				if(l1< l2){
					return -1;
				}
				return 1;
			}			
		})
    	;
    	private final Comparator<Range> rangeComparator;
    	
    	private CoverageRegionComparators(Comparator<Range> comparator){
    		this.rangeComparator = comparator;
    	}
    	
    	@Override
		public int compare(CoverageRegion<?> o1, CoverageRegion<?> o2) {
			return rangeComparator.compare(o1.asRange(), o2.asRange());
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
        
        public Builder(StreamingIterator<P> elements, int maxAllowedCoverage){
            super(maxAllowedCoverage);
            initialize(elements);
        }
        public Builder(StreamingIterator<P> elements) {
            initialize(elements);
        }
        private final void initialize(Collection<P> collection){
        	initialize(IteratorUtil.createStreamingIterator(collection.iterator()));
        }
        private final void initialize(StreamingIterator<P> elements){
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
            return new CoverageMapImpl<P>(
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



