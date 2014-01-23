package org.jcvi.jillion.sam.header;
/**
 * Sorting Order of alignments.
 * @author dkatzel
 *
 */
public enum SortOrder {
	/**
	 * Sort order is unknown.
	 * This is the default if no sort order is specified.
	 */
	UNKNOWN,
	UNSORTED,
	/**
	 * Order by query name (read name).
	 */
	QUERY_NAME,
	/**
	 * The major sort key is the Reference Name,
	 * with the order defined by the order of the
	 * References defined in the sequence dictionary. 
	 * And the minor sort key is the {@link org.jcvi.jillion.sam.SamRecord#getStartOffset()}
	 * field.
	 * Alignments with the same reference and start offset,
	 * the order is arbitrary.
	 * All alignemnts that do not map to a reference follow
	 * alignments with some other value but otherwise are in arbitrary order.
	 */
	COORDINATE;
}
