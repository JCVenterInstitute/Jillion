/*
 * Created on Nov 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.io.FileVisitor;

public interface SffFileVisitor extends FileVisitor {

    boolean visitCommonHeader(SFFCommonHeader commonHeader);
    
    boolean visitReadHeader(SFFReadHeader readHeader);
    
    boolean visitReadData(SFFReadData readData);
}
