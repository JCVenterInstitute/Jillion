package org.jcvi.assembly.agp;

import org.jcvi.io.TextFileVisitor;
import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;

/**
 * User: aresnick
 * Date: Sep 9, 2009
 * Time: 2:36:28 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public interface AgpFileVisitor extends TextFileVisitor {
    /**
     *
     * @param scaffoldId
     * @param contigRange
     * @param contigId
     * @param dir
     */
    void visitContigEntry(String scaffoldId, Range contigRange, String contigId, SequenceDirection dir);
}
