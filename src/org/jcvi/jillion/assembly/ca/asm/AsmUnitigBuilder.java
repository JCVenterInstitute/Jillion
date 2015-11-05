/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.ca.asm;

import org.jcvi.jillion.assembly.ContigBuilder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;

public interface AsmUnitigBuilder extends ContigBuilder<AsmAssembledRead, AsmUnitig>{

    
    /**
     * Add a read to this contig with the given values.  This read
     * can later get modified via the {@link #getAssembledReadBuilder(String)}.
     * 
     * @param readId the Id this read should have
     * @param validBases the gapped bases of this read that align (however well/badly)
     * to this contig and will be used as underlying sequence data for this contig.
     * @param offset the gapped start offset of this read into the contig
     * consensus.
     * @param dir the {@link Direction} of this read.
     * @param clearRange the ungapped clear range of the valid bases
     * relative to the full length non-trimmed raw full length
     * read from the sequence machine.
     * 
     * @param ungappedFullLength the ungapped full length
     * non-trimmed raw full length
     * read from the sequence machine.
     * 
     * @param isSurrogate {@code true} if this read is a surrogate Read;
     * {@code false} otherwise.
     * 
     * @return this.
     */
    AsmUnitigBuilder addRead(String readId, String validBases, int offset,
            Direction dir, Range clearRange, int ungappedFullLength, boolean isSurrogate);

}
