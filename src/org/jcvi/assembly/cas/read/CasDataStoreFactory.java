/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public interface CasDataStoreFactory {

    NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore) throws CasDataStoreFactoryException;
    
    QualityDataStore getQualityDataStoreFor(String pathToDataStore) throws CasDataStoreFactoryException;
}
