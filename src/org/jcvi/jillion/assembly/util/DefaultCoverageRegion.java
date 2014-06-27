/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.util.MapUtil;

final class  DefaultCoverageRegion<T extends Rangeable> implements CoverageRegion<T> {
    private final Collection<T> elements;
    private final Range range;
    /**
     * Build an instance of DefaultCoverageRegion.
     * @param start 0-based start offset of this coverage region.
     * @param length length of this region.
     * @param elements collection of elements in the region, guaranteed to be non-null.
     */
    private DefaultCoverageRegion(Range range, Collection<T> elements){
        this.elements = elements;
        this.range = range;
    }
    @Override
    public int getCoverageDepth() {
        return elements.size();
    }

   
    
    
    @Override
	public Stream<T> streamElements() {
		return elements.stream();
	}
	@Override
	public long getLength() {
		return range.getLength();
	}
	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}
    
    @Override
    public String toString() {
        return new StringBuilder(50)
			        .append("coverage region : ")
			        .append(range)
			        .append(" coverage = ")
			        .append(getCoverageDepth())
			        .toString();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + range.hashCode();
        //need to use hashSet to give same elements in different order
        //same hashcode
        result = prime * result + new HashSet<T>(elements).hashCode();

        return result;
    }
    
    /**
     * Iterate thru our elements and the other elements
     * to see if they match
     * @param otherRegion
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean elementsAreEqual(CoverageRegion<?> otherRegion){
    	
    	int otherDepth = otherRegion.getCoverageDepth();
    	//must have same # of elements
    	if(otherDepth !=elements.size()){
    		return false;
    	}
		Set set =new HashSet(MapUtil.computeMinHashMapSizeWithoutRehashing(otherDepth));
		
    	
    	Iterator otherIterator = otherRegion.iterator();
    	while(otherIterator.hasNext()){
    		set.add(otherIterator.next());
    	}
    	return new HashSet<T>(elements).equals(set);
    	
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof CoverageRegion)){
            return false;
        }
        CoverageRegion<?>  other = (CoverageRegion<?>) obj;
        return range.equals(other.asRange()) && elementsAreEqual(other);

    }   



    static final class Builder<T extends Rangeable> implements CoverageRegionBuilder<T>{
        private long start;
        private long end;
        private Queue<T> elements;
        private boolean endIsSet;
        
        @Override
        public  boolean isEndIsSet() {
            return endIsSet;
        }
        public Builder(long start, Iterable<T> elements){
            this(start,elements,null);
        }
        public Builder(long start, Iterable<T> elements, Integer maxAllowedCoverage){
            this(start,
            		elements==null?null: elements.iterator(),
            				maxAllowedCoverage);
        }

        private Builder(long start, Iterator<T> elements, Integer maxAllowedCoverage){
            if(elements ==null){
                throw new IllegalArgumentException("elements can not be null");
            }
            this.start=start;
            if(maxAllowedCoverage ==null){
                this.elements = new ArrayDeque<T>();
            }else{
                this.elements = new ArrayBlockingQueue<T>(maxAllowedCoverage);
            }
            while(elements.hasNext()){
            	 this.elements.offer(elements.next());
            }
        }
        @Override
        public Builder<T> shift(long shift){
        	start+=shift;
        	end+=shift;
        	return this;
        }
        public long start(){
            return start;
        }
        public boolean canSetEndTo(long end){
            return end>=start-1;
        }
        public long end(){
            if(!isEndIsSet()){
                throw new IllegalArgumentException("end not yet set");
            }
            return end;
        }
        public Builder<T>  end(long end){
            if(!canSetEndTo(end)){
                throw new IllegalArgumentException("end must be >= than "+ (start + 1) + " but was "+ end);
            }
            this.end = end;
            endIsSet=true;
            return this;
        }
        public Builder<T>  add(T element){
            elements.offer(element);
            return this;
        }
        public boolean  offer(T element){
            return elements.offer(element);
        }
        public Builder<T>  remove(T element){
            elements.remove(element);
            return this;
        }
        public Builder<T> removeAll(Collection<T> elements){
            this.elements.removeAll(elements);
            return this;
        }
        public DefaultCoverageRegion<T> build(){
            if(!endIsSet){
                throw new IllegalStateException("end must be set");
            }
            return new DefaultCoverageRegion<T>(Range.of(start, end), elements);
        }
        /**
         * 
        * {@inheritDoc}
         */
		@Override
		public Collection<T> getElements() {
			return new ArrayList<T>(elements);
		}
		@Override
		public int getCurrentCoverageDepth() {
			return elements.size();
		}
		@Override
		public boolean hasSameElementsAs(CoverageRegionBuilder<T> other) {
			if(other==null){
				return false;
			}
			
			if(elements.size() != other.getCurrentCoverageDepth()) {				
				return false;
			}
			return elements.containsAll(other.getElements());
		}
		@Override
		public Range asRange() {
			return Range.of(start, end);
		}
		@Override
		public boolean rangeIsEmpty() {
			return end == start -1;
		}
		@Override
		public void forceAdd(T element) {
			try{
				elements.add(element);
			}catch(IllegalStateException e){
				//we are capacity restricted
				//and hit that limit.
				
				//make new unrestricted list
				elements = new ArrayDeque<T>(elements);
				//add the new one
				elements.add(element);
			}
			
		}
		
		
		
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return range;
    }

}
