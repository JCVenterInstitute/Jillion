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
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.assembly.AssembledReadBuilder;

/**
 * {@code AceAssembledReadBuilder} is a {@link Builder}
 * for {@link AceAssembledRead}s for a specific {@link AceContig}.
 * Methods in this interface can modify the {@link NucleotideSequence}
 * of this read or shift where on the contig
 * this read aligns.
 * @author dkatzel
 *
 *
 */
public interface AceAssembledReadBuilder extends AssembledReadBuilder<AceAssembledRead>{
   

    /**
     * Get the {@link PhdInfo}
     * for this read.
     * @return the phdInfo
     * (never null).
     */
    PhdInfo getPhdInfo();
    /**
     * 
     * {@inheritDoc}
     */
    AceAssembledReadBuilder copy();

    
}
