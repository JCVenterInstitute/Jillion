package org.jcvi.jillion.sam;

import java.util.Collection;
import java.util.EnumSet;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.cigar.Cigar;

public interface SamRecordI {

	int NOT_SET = -1;
	String UNAVAILABLE = "*";
	String IDENTICAL = "=";

	boolean isPrimary();

	boolean useForAnalysis();

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

	EnumSet<SamRecordFlags> getFlags();

	int getStartPosition();

	int getNextOffset();

	byte getMappingQuality();

	Cigar getCigar();

	NucleotideSequence getSequence();

	QualitySequence getQualities();

	int getObservedTemplateLength();

	boolean hasAttribute(SamAttributeKey key);

	SamAttribute getAttribute(SamAttributeKey key);

	Collection<SamAttribute> getAttributes();

	boolean hasAttribute(ReservedSamAttributeKeys key);

	SamAttribute getAttribute(ReservedSamAttributeKeys key);

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
	 * Get the alignment {@link Range} that his record
	 * mapped to along the reference.  The returned Range
	 * is the range used to compute the Bin in indexed bai files.
	 * 
	 * @return a {@link Range} for this record's alignment,
	 * or {@code null} if this record didn't map.
	 */
	Range getAlignmentRange();

}