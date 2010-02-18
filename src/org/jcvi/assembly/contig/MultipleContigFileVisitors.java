/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.ArrayList;
import java.util.Collection;

import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;
/**
 * <code>MultipleContigFileVisitors</code> is a decorator
 * that can wraps several {@link ContigFileVisitor}s
 * so they are all invoked from a the corresponding method
 * of MultipleContigFileVisitors is invoked.
 * @author dkatzel
 *
 *
 */
public class MultipleContigFileVisitors implements ContigFileVisitor {
    Collection<ContigFileVisitor> visitors = new ArrayList<ContigFileVisitor>();
    /**
     * Wrap the given ContigFileVisitors behind a single ContigFileVisitor.
     * @param visitors collection of visitors (can not be null).
     * @throws NullPointerException if visitors is null.
     */
    public MultipleContigFileVisitors(Collection<ContigFileVisitor> visitors){
        if(visitors == null){
            throw new NullPointerException("visitor collection can not be null");
        }
        this.visitors.addAll(visitors);
    }
    @Override
    public void visitBasecallsLine(String line) {
        for(ContigFileVisitor visitor : visitors){
            visitor.visitBasecallsLine(line);
        }
    }

    @Override
    public void visitEndOfFile() {
        for(ContigFileVisitor visitor : visitors){
            visitor.visitEndOfFile();
        }
    }

    @Override
    public void visitLine(String line) {
        for(ContigFileVisitor visitor : visitors){
            visitor.visitLine(line);
        }
    }

    @Override
    public void visitNewContig(String contigId) {
        for(ContigFileVisitor visitor : visitors){
            visitor.visitNewContig(contigId);
        }
    }

    @Override
    public void visitNewRead(String readId, int offset, Range validRange,
            SequenceDirection dir) {
        for(ContigFileVisitor visitor : visitors){
            visitor.visitNewRead(readId, offset, validRange, dir);
        }
    }
    @Override
    public void visitFile() {
        for(ContigFileVisitor visitor : visitors){
            visitor.visitFile();
        }
        
    }

}
