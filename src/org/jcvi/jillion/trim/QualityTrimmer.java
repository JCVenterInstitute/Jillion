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
package org.jcvi.jillion.trim;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public interface QualityTrimmer {

	Range trim(QualitySequence qualities);
	
	 /**
         * Find the Good Range to keep for the given
         * {@link QualitySequenceBuilder}. <strong>Note:</strong>
         * The builder is not modified by this trimming operation.
         * 
         * @implSec the default implementation builds a new QualitySequence
         * from the current builder's state and performs the trim operation on that.
         * <pre>
         * {@code trim(builder.build());}
         * </pre>
         * But this method may be overridden to use a more efficient implementation.
         * 
         * @param builder the {@link QualitySequenceBuilder} to examine for trimming;
         * can not be null.
         * @return a {@link Range} of the portion of the sequence to keep.
         * will never be null but may be empty if there is no portion
         * of the sequence to keep.
         * 
         * @throws NullPointerException if the given builder is null.
         * 
         * @since 5.2
         */
    default Range trim(QualitySequenceBuilder builder){
        return trim(builder.build());
    }
}
