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
/*
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.assembly.util.columns.AssemblyColumnConsensusCaller;
/**
 * <code>ConsensusCaller</code> compute the
 * {@link ConsensusResult} for the given Slice.
 * @author dkatzel
 *
 *
 */
public interface ConsensusCaller extends AssemblyColumnConsensusCaller<SliceElement, Slice>{
    /**
     * Compute the {@link ConsensusResult} for the given Slice.
     * @param slice the Slice to compute the consensus for.
     * @return a {@link ConsensusResult} will never be <code>null</code>
     * @throws NullPointerException if slice is null.
     */
    ConsensusResult callConsensus(Slice slice);
    
    
}
