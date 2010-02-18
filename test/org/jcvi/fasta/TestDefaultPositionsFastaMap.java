/*
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;


public class TestDefaultPositionsFastaMap extends AbstractTestPositionsFastaMap{


    @Override
    protected DataStore<PositionFastaRecord<EncodedGlyphs<ShortGlyph>>> createPositionFastaMap(File fastaFile) throws IOException {
        return new DefaultPositionFastaFileDataStore(fastaFile);
    }
}
