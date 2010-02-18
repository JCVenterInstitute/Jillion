/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSffFileDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultNucleotideSffDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.QualitySffDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;

public class DefaultSffCasDataStoreFactory  implements
                        CasDataStoreFactory {

    private final Map<String, SffDataStore> sffDataStores = new HashMap<String, SffDataStore>();


    @Override
    public synchronized NucleotideDataStore getNucleotideDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        
        addDataStoreIfNeeded(pathToDataStore);
        return new DefaultNucleotideSffDataStore(sffDataStores.get(pathToDataStore),true);
    }

    private void addDataStoreIfNeeded(String pathToDataStore)
            throws CasDataStoreFactoryException {
        if(!sffDataStores.containsKey(pathToDataStore)){            
            SffDataStore dataStore = parseSffDataStore(pathToDataStore);
            sffDataStores.put(pathToDataStore, dataStore);
        }
    }

    @Override
    public synchronized QualityDataStore getQualityDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        addDataStoreIfNeeded(pathToDataStore);
        return new QualitySffDataStore(sffDataStores.get(pathToDataStore),true);
    }

    private SffDataStore parseSffDataStore(String pathToDataStore)
            throws CasDataStoreFactoryException {
        DefaultSffFileDataStore dataStore =new DefaultSffFileDataStore(
                    RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
        InputStream in = null;
        try {
            in = new FileInputStream(pathToDataStore);
            SffParser.parseSFF(in, dataStore);
           
        } catch (Exception e) {
            throw new CasDataStoreFactoryException("could not create sff nucleotide datastore", e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        return dataStore;
    }


}
