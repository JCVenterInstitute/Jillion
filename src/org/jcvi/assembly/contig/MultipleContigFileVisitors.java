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
