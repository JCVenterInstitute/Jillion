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
package org.jcvi.jillion.sam.header;
/**
 * {@code SamReferenceSequence} is an object
 * representation of the metadata for a 
 * reference used in a SAM or BAM file.
 * 
 * @author dkatzel
 *
 */
public interface SamReferenceSequence {

    /**
     * Get the human readable name of this reference sequence.
     * @return a String; will never be null.
     */
    String getName();

    /**
     * Get the number of bases in this reference sequence.
     * @return the number of bases; will always be > 0.
     */
    int getLength();

    String getGenomeAssemblyId();

    String getSpecies();

    String getUri();

    String getMd5();

}
