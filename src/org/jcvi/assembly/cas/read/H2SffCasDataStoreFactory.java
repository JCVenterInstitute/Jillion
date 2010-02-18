/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.H2NucleotideSffDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.H2QualitySffDataStore;

public class H2SffCasDataStoreFactory implements CasDataStoreFactory{

    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {       
        if(!"sff".equals(FilenameUtils.getExtension(pathToDataStore))){
            throw new CasDataStoreFactoryException("not a sff file");
        }
        try {
            return new H2NucleotideSffDataStore(new File(pathToDataStore), 
                    new H2NucleotideDataStore(),
                    true);
        } catch (Exception e) {
           throw new CasDataStoreFactoryException("could not create H2 Sff Nucleotide DataStore for "+ pathToDataStore,e);
        } 
    }

    @Override
    public QualityDataStore getQualityDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        if(!"sff".equals(FilenameUtils.getExtension(pathToDataStore))){
            throw new CasDataStoreFactoryException("not a sff file");
        }
        try {
            return new H2QualitySffDataStore(new File(pathToDataStore), 
                    new H2QualityDataStore(),
                    true);
        } catch (Exception e) {
           throw new CasDataStoreFactoryException("could not create H2 Sff Quality DataStore for "+ pathToDataStore,e);
        } 
    }

}
