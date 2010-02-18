/**
 * Alignment.java Created: Aug 13, 2009 - 3:13:25 PM (jsitz) Copyright 2009 J.
 * Craig Venter Institute
 */
package org.jcvi.align;


/**
 * A <code>Alignment</code> represents a pair of regions in two genetic sequences which show
 * some degree of similarity.  These regions form a composite and an <code>Alignment</code>
 * contains the data which describes this composite, including the ranges of both sequences
 * which form the alignment, the locations of imperfections within those ranges and a number
 * of metrics for evaluating the quality of the alignment.
 *
 * @author jsitz@jcvi.org
 */
public interface Alignment
{
    /** The character which represents an alignment gap. */
    final char GAP_CHARACTER = '-';

    /**
     * Fetch the alignment data specific to the query sequence.
     * 
     * @return The {@link SequenceAlignment} object containing data for the query sequence.
     */
    SequenceAlignment getQueryAlignment();

    /**
     * Fetch the alignment data specific to the reference sequence.
     * 
     * @return The {@link SequenceAlignment} object containing data for the reference sequence.
     */
    SequenceAlignment getReferenceAlignment();

    /**
     * Fetches the identity percentage of this alignment.  This value should correspond to the
     * method used by NCBI for calculating identity.  Specifically, the identity is the 
     * percentage of pairwise locations in the alignment where the query and reference element
     * match <em>exactly</em>.
     * 
     * @return The identity as a <code>double</code>-encoded percentage between <code>0.0</code>
     *  and <code>1.0</code>.
     */
    double getIdentity();

    /**
     * Fetches the reported score assigned to the alignment.  There is no required method for
     * generating this score. It is dependant on the algorithm used to perform the alignment
     * (and in many cases, the length or even the content of the reference).  This should only
     * be used to perform relative comparison of alignments from the same algorithm.
     * 
     * @return The score as a <code>double</code>.
     */
    public abstract double getScore();

}