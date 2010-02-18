/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.Range;
import org.jcvi.io.FileVisitor;
import org.jcvi.io.TextFileVisitor;
import org.jcvi.sequence.SequenceDirection;

public interface ContigFileVisitor extends TextFileVisitor{

    void visitNewContig(String contigId);    
    void visitBasecallsLine(String line);
    
    void visitNewRead(String seqId, int offset, Range validRange, SequenceDirection dir);
}
