/**
 * Aligner.java Created: Aug 13, 2009 - 3:54:18 PM (jsitz) Copyright 2009 J.
 * Craig Venter Institute
 */
package org.jcvi.align;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;


/**
 * A <code>Aligner</code> is an object capable of generating {@link Alignment}s based on a 
 * given algorithm.  Though implementations may allow for more advanced behaviors, the API 
 * is declared for a standard alignment between just two sequences.  These sequences may be
 * nucleotide sequences (with or without ambiguities), amino acid sequences, or any other 
 * sequence of data fitting the input.
 *
 * @author jsitz@jcvi.org
 */
public interface Aligner<G extends Glyph>
{
    /**
     * Align a query sequence against this 
     * 
     * @param referenceSequence The base sequence for alignment.
     * @param querySequence The sequence to align to the reference.
     * @return The {@link Alignment} of the query against the reference.
     */
    Alignment alignSequence(EncodedGlyphs<G> referenceSequence, EncodedGlyphs<G> querySequence);
}
