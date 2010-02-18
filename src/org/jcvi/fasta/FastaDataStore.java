/*
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;

public interface FastaDataStore<G extends Glyph, T extends EncodedGlyphs<G>,F extends FastaRecord<T>> extends DataStore<F>{

    

}
