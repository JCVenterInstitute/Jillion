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
package org.jcvi.jillion.align;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
/**
 * {@code AminoAcidSubstitutionMatrix}
 * is a marker interface for a {@link SubstitutionMatrix}
 * of {@link AminoAcid}s.
 * @author dkatzel
 */
public interface AminoAcidSubstitutionMatrix extends SubstitutionMatrix<AminoAcid>{
    /**
     * Create a new {@link AminoAcidSubstitutionMatrixBuilder}
     * initialized using the current values of this matrix.
     * @return a new builder.
     * @since 6.1.7
     */
    default AminoAcidSubstitutionMatrixBuilder toBuilder(){
        return new AminoAcidSubstitutionMatrixBuilder(this);
    }
}
