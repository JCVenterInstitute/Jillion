/*
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;

public interface SliceElement {

    String getName();
    NucleotideGlyph getBase();
    PhredQuality getQuality();
    SequenceDirection getSequenceDirection();
}
