/*
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;

public class TestH2NucleotideSffDataStore extends AbstractTestSffNucleotideDataStore{

    @Override
    protected NucleotideDataStore createSut(File sffFile) throws SFFDecoderException, FileNotFoundException, DataStoreException {
        return new H2NucleotideSffDataStore(sffFile, 
                new H2NucleotideDataStore(), false);
    }

}
