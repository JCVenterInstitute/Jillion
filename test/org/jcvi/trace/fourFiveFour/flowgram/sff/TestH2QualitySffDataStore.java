/*
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;

public class TestH2QualitySffDataStore extends AbstractTestSffQualityDataStore{

    @Override
    protected QualityDataStore createSut(File sffFile) throws SFFDecoderException, FileNotFoundException, DataStoreException {
        return new H2QualitySffDataStore(sffFile, 
                new H2QualityDataStore(), false);
    }

}
