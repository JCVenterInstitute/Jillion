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
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.ArrayIterator;
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
	 * {@link Rangeable}s.
	 * @param elements the elements to create a coverage map of.
	 * @return a new {@link CoverageMap}; never null.
	 * @param <R> The type of {@link Rangeable} used in this map.
	 */
    public static <R extends Rangeable> CoverageMap<R> 
            create(Collection<R> elements, boolean startAtOrigin){
        return new Builder<R>(elements,startAtOrigin).build();
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
    	   return new Builder<R>(elements,maxAllowedCoverage, false).build();
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
                  create(Collection<R> elements, int maxAllowedCoverage, boolean startAtOrigin){
        	  return new Builder<R>(elements,maxAllowedCoverage, startAtOrigin).build();
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
                  create(Collection<R> elements, int maxAllowedCoverage, int minRequiredCoverage){
       	   return create(elements, maxAllowedCoverage, minRequiredCoverage, false);
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
            create(Collection<R> elements, int maxAllowedCoverage,  int minRequiredCoverage,
            		boolean startAtOrigin){
        return new Builder<R>(elements,maxAllowedCoverage, minRequiredCoverage, startAtOrigin).build();
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
	     * Statistics of this coverage map
	     * lazily computed since it is an
	     * expensive operation.
	     */
	    private CoverageMapStats stats=null;
	    
	    /**
	     *
	     * Creates a new <code>CoverageMapImpl</code>.
	     * @param amplicons A {@link Collection} of {@link Coordinated}s.
	     */
	    @SuppressWarnings({"unchecked", "rawtypes"})
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
	    public synchronized CoverageMapStats getStats(){
	    	if(stats ==null){
	    		computeStats();
	    	}
	    	return stats;
	    }
	    @Override
	    public synchronized double getAverageCoverage(){
	        if(stats ==null){
		    	computeStats();
	        }
	        return stats.getAvgCoverage();
	    }
		public synchronized void computeStats() {
			
			if(isEmpty()){
				stats = new CoverageMapStats(0, 0, 0);
				return;
			}
			/*
			long totalLength = 0L;
			long totalCoverage =0L;
			
			int minCoverage = Integer.MAX_VALUE;
			int maxCoverage = Integer.MIN_VALUE;
			/*
			for(CoverageRegion<?> region : this){
				long regionLength = region.asRange().getLength();
				totalLength +=regionLength;
				int coverageDepth = region.getCoverageDepth();
				totalCoverage += coverageDepth * regionLength;
				if(coverageDepth < minCoverage){
					minCoverage = coverageDepth;
				}
				if(coverageDepth > maxCoverage){
					maxCoverage = coverageDepth;
				}
			}
			*/
			//use java 8 streams for faster performance?
			stats = Arrays.stream(regions)
											.parallel()
											.collect(CoverageMapCollectors.computeStats());
			
		}
	  
	    
	    
	    @Override
		public synchronized int getMinCoverage() {
	    	if(stats ==null){
		    	computeStats();
	        }
			return stats.getMinCoverage();
		}
		@Override
		public synchronized int getMaxCoverage() {
	    	if(stats ==null){
		    	computeStats();
	        }
			return stats.getMaxCoverage();
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
	            buf.append(region)
	            	.append('\n');
	        }
	        return buf.toString();
	    }
	    @Override
	    public List<CoverageRegion<V>> getRegionsWhichIntersect(Range range) {
	    	if(range ==null){
	    		throw new NullPointerException("range can not be null");
	    	}
	    	if(this.isEmpty() || range.isEmpty()){
	    		//empty coverage map or
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
	        return new ArrayIterator<CoverageRegion<V>>(regions, false);
	    }
	
	   
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public boolean isEmpty() {
	        return regions.length==0;
	    }
		@Override
		public Stream<CoverageRegion<V>> regions() {
			return Arrays.stream(regions);
		}
		@Override
		public Stream<CoverageRegion<V>> regions(Range range) {
			//TODO this beginning part of the code 
			//is copy and pasted from #getRegionsWhichIntersect
			
			if(range ==null){
	    		throw new NullPointerException("range can not be null");
	    	}
	    	if(this.isEmpty() || range.isEmpty()){
	    		//empty coverage map or
	    		//empty ranges never intersect anything
	    		return Collections.<CoverageRegion<V>>emptyList().stream();
	    	}	    	
	    	if(regions[0].asRange().getBegin() > range.getEnd()){
	    		//region is entirely before coverage map
	    		return Collections.<CoverageRegion<V>>emptyList().stream();
	    	}
	    	if(regions[regions.length-1].asRange().getEnd() < range.getBegin()){
	    		//region is entirely after coverage map
	    		return Collections.<CoverageRegion<V>>emptyList().stream();
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
	    	
			return Arrays.stream(regions, correctedBeginIndex, correctedEndIndex +1);
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
        private final boolean startAtOrigin;
        
        public Builder(Collection<P> elements, int maxAllowedCoverage, boolean startAtOrigin){
            super(maxAllowedCoverage);
            initialize(elements);
            this.startAtOrigin = startAtOrigin;
        }
        public Builder(Collection<P> elements, int maxAllowedCoverage, int minRequiredCoverage, boolean startAtOrigin){
            super(maxAllowedCoverage, minRequiredCoverage);
            initialize(elements);
            this.startAtOrigin = startAtOrigin;
        }
        public Builder(Collection<P> elements, boolean startAtOrigin) {
            initialize(elements);
            this.startAtOrigin =startAtOrigin;
        }
        public Builder(Collection<P> elements) {
           this(elements, false);
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
            
        	if(coverageRegionBuilders.isEmpty()){
        		return Collections.emptyList();
        	}
        	//+1 incase we need to add an extra region to cover origin
            List<CoverageRegion<P>> regions = new ArrayList<CoverageRegion<P>>(
                    coverageRegionBuilders.size()+1);
            DefaultCoverageRegion.Builder<P> newFirst=null, newLast = null;
            
            if(startAtOrigin){            	
				long firstStart =coverageRegionBuilders.get(0).start();
				if(firstStart <0){
					//we start at negative
					//check to see if we cross the origin
					//(which means we cover it)
					long lastCoveredOffset =coverageRegionBuilders.get(coverageRegionBuilders.size()-1).end();
					if(lastCoveredOffset <0){				
						newLast = new DefaultCoverageRegion.Builder<P>(lastCoveredOffset+1, Collections.<P>emptyList());
						newLast.end(0);
						
					}
				}else if(firstStart >0){
					//don't need to shift but need to add
					//and 0x coverage region
					newFirst = new DefaultCoverageRegion.Builder<P>(0, Collections.<P>emptyList());
					newFirst.end(firstStart -1);
				}
				
            }
            if(newFirst !=null){
            	regions.add(newFirst.build());
            }
            Iterator<CoverageRegionBuilder<P>> iterator = coverageRegionBuilders.iterator();
           
        	while(iterator.hasNext()){
				regions.add(iterator.next().build());
            }
        	if(newLast !=null){
            	regions.add(newLast.build());
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



