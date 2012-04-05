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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.jcvi.common.core.Placed;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.util.CommonUtil;

public final class  DefaultCoverageRegion<T extends Placed> implements CoverageRegion<T> {
    private Collection<T> elements;
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
    public int getCoverage() {
        return elements.size();
    }

    @Override
    public long getLength() {
        return range.getLength();
    }

    @Override
    public long getBegin() {
        return range.getBegin();
    }
    @Override
    public long getEnd() {
        return range.getEnd();
    } 

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("coverage region : ");
        builder.append(Range.create(getBegin(), getEnd()));
        builder.append(" coverage = ");
        builder.append(getCoverage());
        return builder.toString();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + range.hashCode();
        result = prime * result + new ArrayList<T>(elements).hashCode();

        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultCoverageRegion)){
            return false;
        }
        DefaultCoverageRegion other = (DefaultCoverageRegion) obj;
        return CommonUtil.similarTo(new ArrayList<T>(elements),
                new ArrayList<T>(other.elements)) &&
                CommonUtil.similarTo(range.getLength(), other.getLength()) 
                && CommonUtil.similarTo(range.getBegin(), other.getBegin());
    }
    @Override
    public CoverageRegion<T> shiftLeft(int units) {
        return new DefaultCoverageRegion<T>(this.range.shiftLeft(units), this.elements);
    }
    @Override
    public CoverageRegion<T> shiftRight(int units) {
        return new DefaultCoverageRegion<T>(this.range.shiftRight(units), this.elements);
    }


    



    public static class Builder<T extends Placed> implements CoverageRegionBuilder<T>{
        private final long start;
        private long end;
        private Queue<T> elements;
        private boolean endIsSet;
        public final boolean isEndIsSet() {
            return endIsSet;
        }
        public Builder(long start, Iterable<T> elements){
            this(start,elements,null);
        }
        public Builder(long start, Iterable<T> elements, Integer maxAllowedCoverage){
            if(elements ==null){
                throw new IllegalArgumentException("elements can not be null");
            }
            this.start=start;
            if(maxAllowedCoverage ==null){
                this.elements = new ArrayDeque<T>();
            }else{
                this.elements = new ArrayBlockingQueue<T>(maxAllowedCoverage);
            }
            
            for(T e : elements){
                this.elements.offer(e);
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
        public Builder end(long end){
            if(!canSetEndTo(end)){
                for(Placed element : elements){
                    System.out.println(element.getBegin() +" , "+ element.getEnd());
                }
                throw new IllegalArgumentException("end must be >= than "+ (start + 1) + " but was "+ end);
            }
            this.end = end;
            endIsSet=true;
            return this;
        }
        public List<T> elements(){
            return new ArrayList<T>(elements);
        }
        public Builder offer(T element){
            elements.offer(element);
            return this;
        }
        public Builder remove(T element){
            elements.remove(element);
            return this;
        }
        public Builder removeAll(Collection<T> elements){
            this.elements.removeAll(elements);
            return this;
        }
        public DefaultCoverageRegion build(){
            if(!endIsSet){
                throw new IllegalStateException("end must be set");
            }
            return new DefaultCoverageRegion(Range.create(start, end), elements);
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
    public int compareTo(T o) {
        return range.compareTo(o.asRange());
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public Range asRange() {
        return range;
    }

}
