/*
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;

public abstract class AbstractFastaDataStore <G extends Glyph, T extends EncodedGlyphs<G>,F extends FastaRecord<T>> implements FastaDataStore<G,T,F>{

}
