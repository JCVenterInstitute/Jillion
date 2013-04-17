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
package org.jcvi.jillion.assembly.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;

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
	public Iterator<T> iterator() {
		return elements.iterator();
	}
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(50);
        builder.append("coverage region : ");
        builder.append(range);
        builder.append(" coverage = ");
        builder.append(getCoverageDepth());
        return builder.toString();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + range.hashCode();
        result = prime * result + computeElementHashCode();

        return result;
    }
    /**
     * Computes hashcode of collection without
     * having to worry about implementation
     * details of how collection is stored.
     * @return
     */
    private int computeElementHashCode(){
    	int hashCode = 1;
    	Iterator<T> i = elements.iterator();
    	while (i.hasNext()) {
    	    T obj = i.next();
    	    hashCode = 31*hashCode + (obj==null ? 0 : obj.hashCode());
    	}
    	return hashCode;
    }
    /**
     * Iterate thru our elements and the other elements
     * to see if they match
     * @param otherRegion
     * @return
     */
    private boolean elementsAreEqual(CoverageRegion<?> otherRegion){
		Iterator<?> otherIterator = otherRegion.iterator();
		Iterator<?> iter = iterator();
    	while(iter.hasNext() && otherIterator.hasNext()){
    		if(!iter.next().equals(otherIterator.next())){
    			return false;
    		}
    	}
    	if(
    			(iter.hasNext() && !otherIterator.hasNext())
    			||
    			(!iter.hasNext() && otherIterator.hasNext())
    			){
    		return false;
    	}
    	return true;
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
        private final long start;
        private long end;
        private Queue<T> elements;
        private boolean endIsSet;
        
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
        public Builder(long start, Iterator<T> elements){
        	this(start, elements, null);
        }
        public Builder(long start, Iterator<T> elements, Integer maxAllowedCoverage){
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
        
        public Builder<T>  offer(T element){
            elements.offer(element);
            return this;
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
        
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return range;
    }

}
