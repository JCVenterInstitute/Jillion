/*
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;

public class TestLargePositionsFastaMap extends AbstractTestPositionsFastaMap{

    @Override
    protected DataStore<PositionFastaRecord<EncodedGlyphs<ShortGlyph>>> createPositionFastaMap(File fastaFile)
            throws Exception {
        return new LargePositionFastaFileDataStore(fastaFile);
    }

}
