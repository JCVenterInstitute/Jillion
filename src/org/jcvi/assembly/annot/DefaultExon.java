/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot;

import org.jcvi.CommonUtil;
import org.jcvi.Range;

public class DefaultExon implements Exon {
    private Frame frame;
    private Range range;
    public DefaultExon(Frame frame, Range range){
        if(frame ==null){
            throw new IllegalArgumentException("frame can not be null");
        }
        this.frame = frame;
        this.range= range.copy();
    }
    public DefaultExon(Frame frame, long start, long end){
      this(frame, Range.buildRange(start, end));
    }
    
    @Override
    public long getEndPosition() {
        return range.getEnd();
    }

    @Override
    public Frame getFrame() {
        return frame;
    }

    @Override
    public long getStartPosition() {
        return range.getStart();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + frame.hashCode();
        result = prime * result + range.hashCode();      
        
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
       
        if (!(obj instanceof Exon)){
            return false;
        }
        DefaultExon other = (DefaultExon) obj;
        return CommonUtil.similarTo(getFrame(), other.getFrame())
        && CommonUtil.similarTo(getStartPosition(), other.getStartPosition())
        && CommonUtil.similarTo(getEndPosition(), other.getEndPosition());
        
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("DefaultExon : ");
        result.append("frame = ");
        result.append(getFrame());
        result.append(" range " );
        result.append(range);
        return result.toString();
    }
    
    

}
