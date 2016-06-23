/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.sam;

import java.util.Collection;
import java.util.Set;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.cigar.Cigar;
/**
 * {@code SamRecord} is an interface for the object
 * representation of a single alignment in a SAM or BAM
 * encoded file.
 * 
 * @author dkatzel
 *
 */
public interface SamRecord extends SamAttributed {
	
	int NOT_SET = -1;
	String UNAVAILABLE = "*";
	String IDENTICAL = "=";
	/**
	 * Is this the primary alignment for this read.
	 * Some assemblers may also provide alternate alignments
	 * for some reads.  In that case, this flag will
	 * distinguish the primary alignment's record from the alternate records.
	 * 
	 * @return {@code true} if this record is the primary;
	 * {@code false} otherwise.
	 */
	boolean isPrimary();
	/**
	 * Should this record be used in
	 * analysis.
	 * Some records should be excluded from downstream
	 * analysis due to quality or sequencing error concerns.
	 * 
	 * @return {@code true} if this record is the should be used for 
	 * analysis; {@code false} otherwise.
	 */
	boolean useForAnalysis();
	/**
	 * Get the query name of this record, this is usually the sequence read's name;
	 * but may be set to {@link SamRecord#UNAVAILABLE} if the name is not known.
	 * 
	 * @return the Query name as a String or {@link SamRecord#UNAVAILABLE}.
	 */
	String getQueryName();

	/**
	 * Get the reference name that this
	 * record aligns to.
	 * @return The name of the reference
	 * or {@link SamRecord#UNAVAILABLE}
	 * if the record either didn't
	 * align or the reference name wasn't provided.
	 */
	String getReferenceName();

	String getNextName();
	/**
	 * Get all the {@link SamRecordFlags}s present
	 * in this record.  Please note, 
	 * that some of the common tests and checks for
	 * the presence/absence of some flags or groups
	 * of flags are methods.
	 * 
	 * @return the unmodifiable set of {@link SamRecordFlags}.
	 * 
	 * @see #mapped()
	 * @see #isPrimary()
	 * @see #useForAnalysis()
	 * @see #getDirection()
	 * 
	 */
	Set<SamRecordFlags> getFlags();

	int getStartPosition();

	int getNextOffset();

	byte getMappingQuality();

	Cigar getCigar();

	NucleotideSequence getSequence();

	QualitySequence getQualities();

	int getObservedTemplateLength();

	Collection<SamAttribute> getAttributes();

	int hashCode();

	boolean equals(Object obj);

	String toString();

	/**
	 * Did this record map to one of the references.
	 * 
	 * @return {@code true} if the record mapped somewhere;
	 * {@code false} otherwise.
	 */
	boolean mapped();

	/**
	 * Get the {@link Direction} that this read mapped in.
	 * If the read didn't map, then the direction will be {@link Direction#FORWARD}.
	 * 
	 * @return {@link Direction#REVERSE} if the read mapped in reverse;
	 * {@link Direction#FORWARD} otherwise.
	 */
	Direction getDirection();

	/**
	 * Get the alignment {@link Range} that this record
	 * mapped to along the reference.  The returned Range
	 * is the range used to compute the Bin in indexed bai files.
	 * 
	 * @return a {@link Range} for this record's alignment,
	 * or {@code null} if this record didn't map.
	 */
	Range getAlignmentRange();

}
