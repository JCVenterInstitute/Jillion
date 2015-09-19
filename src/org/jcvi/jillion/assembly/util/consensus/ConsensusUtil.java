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
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

final class ConsensusUtil {
    
    
    /**
     * These are the only bases that should be used
     * to consider consensus.
     */
    public static final List<Nucleotide> BASES_TO_CONSIDER = Arrays.asList(Nucleotide.Adenine,
																Nucleotide.Cytosine,
																Nucleotide.Guanine,
																Nucleotide.Thymine,
																Nucleotide.Gap);
    
    private ConsensusUtil(){
        throw new RuntimeException("should never be instantiated");
    }
    
}
