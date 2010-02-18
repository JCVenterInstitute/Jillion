/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit.placed;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.sequence.SequenceDirection;

public abstract class AbstractPlacedReadEdit<P extends PlacedRead> implements PlacedReadEdit<P> {

    protected abstract P createNewPlacedRead(NucleotideEncodedGlyphs basecalls, long offset, Range validRange, SequenceDirection dir);
}
