/*
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class TestLargeSequenceFastaMap  extends AbstractTestSequenceFastaMap {

    @Override
    protected DataStore<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> buildSequenceFastaMap(File file)
            throws IOException {
        return new LargeNucleotideFastaFileDataStore(file);
    }

}
