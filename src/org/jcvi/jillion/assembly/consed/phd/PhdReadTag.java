package org.jcvi.jillion.assembly.consed.phd;

import java.util.Date;

import org.jcvi.jillion.core.Range;
/**
 * {@code PhdReadTag} is specially
 * formatted optional additional information
 * about a particular range of bases of a phd record.
 * @author dkatzel
 *
 */
public interface PhdReadTag {
	/**
	 * Get the type of read tag this is.
	 * @return a free format String describing
	 * the type of tag; will never be null.
	 * The type will usually not be empty
	 * but that can't be guaranteed.
	 */
	String getType();
	/**
	 * Get the type of source program that generated
	 * this tag.
	 * @return a free format String describing
	 * the program that generated this tag
	 * (usually the program name); will never be null.
	 * The source will usually not be empty
	 * but that can't be guaranteed.
	 */
	String getSource();
	/**
	 * Get the ungapped {@link Range}
	 * into the read that this tag refers.
	 * @return a Range; will never be null.
	 */
	Range getUngappedRange();
	/**
	 * Get the {@link Date}
	 * that this tag was generated. 
	 * @return a new {@link Date}
	 * will never be null.
	 */
	Date getDate();
	/**
	 * Get an optional comment for this tag.
	 * @return a String (possibly multi-line)
	 * for this comment; or {@code null}
	 * if this tag does not have a comment.
	 */
	String getComment();
	/**
	 * Get an optional free form data for this tag.
	 * @return a String (possibly multi-line)
	 * for this free form data; or {@code null}
	 * if this tag does not have any free form data.
	 */
	String getFreeFormData();

}