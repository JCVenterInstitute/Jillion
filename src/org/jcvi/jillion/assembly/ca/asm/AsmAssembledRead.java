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

import org.jcvi.jillion.assembly.AssembledRead;

/**
 * @author dkatzel
 *
 *
 */
public interface AsmAssembledRead extends AssembledRead{
    /**
     * Is this read a repeat surrogate
     * unitig which was cautiously placed
     * by Celera Assembler at one or more
     * locations.
     * 
     * @return {@code true} if this read is a 
     * repeat surrogate; {@code false} otherwise.
     */
    boolean isRepeatSurrogate();
}
