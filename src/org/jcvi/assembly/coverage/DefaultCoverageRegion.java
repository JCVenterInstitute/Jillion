/*
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.CommonUtil;
import org.jcvi.Range;
import org.jcvi.assembly.Placed;

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
    public Collection<T> getElements() {
        return elements;
    }

    @Override
    public long getLength() {
        return range.size();
    }

    @Override
    public long getStart() {
        return range.getStart();
    }
    @Override
    public long getEnd() {
        return range.getEnd();
    } 

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("coverage region : ");
        builder.append(Range.buildRange(getStart(), getEnd()));
        builder.append(" coverage = ");
        builder.append(getCoverage());
        return builder.toString();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + range.hashCode();
        result = prime * result + elements.hashCode();

        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DefaultCoverageRegion))
            return false;
        DefaultCoverageRegion other = (DefaultCoverageRegion) obj;
        return  CommonUtil.similarTo(elements,other.getElements()) &&
                CommonUtil.similarTo(range.size(), other.getLength()) 
                && CommonUtil.similarTo(range.getStart(), other.getStart());
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
        private Collection<T> elements;
        private boolean endIsSet;
        public final boolean isEndIsSet() {
            return endIsSet;
        }

        public Builder(long start, Collection<T> elements){
            if(elements ==null){
                throw new IllegalArgumentException("elements can not be null");
            }
            this.start=start;
            this.elements = new ArrayList<T>(elements);
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
                    System.out.println(element.getStart() +" , "+ element.getEnd());
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
        public Builder add(T element){
            elements.add(element);
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
            return new DefaultCoverageRegion(Range.buildRange(start, end), elements);
        }
        
    }
}
