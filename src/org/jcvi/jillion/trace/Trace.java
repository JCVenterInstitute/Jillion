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
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * A {@code Trace} is an abstraction of a piece of
 * genomic sequence data that has been
 * sequenced on some kind of sequencing machine.
 * @author dkatzel
 *
 *
 */
public interface Trace{
	/**
     * Get the id of this read.
     * @return the id as a String; will never be null.
     */
    String getId();
    /**
     * Get the {@link NucleotideSequence} of this read.
     * @return the {@link NucleotideSequence} of this read; will
     * never be null.
     */
    NucleotideSequence getNucleotideSequence();
    /**
     * Get the quality data of this trace as a {@link QualitySequence}.
     * @return a {@link QualitySequence}, should never be null.
     */
    QualitySequence getQualitySequence();
    /**
     * Get the length of the Trace.  This should be the length
     * of the {@link NucleotideSequence} and {@link QualitySequence}.
     * @return the length as a long.
     * 
     * @since 5.2
     */
    default long getLength(){
        return getNucleotideSequence().getLength();
    }
}
