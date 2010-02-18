/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class MultiCasDataStoreFactory implements
        CasDataStoreFactory {

    private final List<CasDataStoreFactory> factories;
    
    public MultiCasDataStoreFactory(CasDataStoreFactory...casNucleotideDataStoreFactories){
        this(Arrays.asList(casNucleotideDataStoreFactories));
    }
    
    public  MultiCasDataStoreFactory(List<CasDataStoreFactory> casNucleotideDataStoreFactories){
        this.factories = new ArrayList<CasDataStoreFactory>(casNucleotideDataStoreFactories );
    }

    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        for(CasDataStoreFactory factory : factories){
            try{
                return factory.getNucleotideDataStoreFor(pathToDataStore);
            }
            catch(CasDataStoreFactoryException e){
                //ignore error must not be correct format...
            }
        }
       throw new CasDataStoreFactoryException("could not get nucleotide datastore for "+ pathToDataStore);
    }

    @Override
    public QualityDataStore getQualityDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        for(CasDataStoreFactory factory : factories){
            try{
                return factory.getQualityDataStoreFor(pathToDataStore);
            }
            catch(CasDataStoreFactoryException e){
                //ignore error must not be correct format...
            }
        }
       throw new CasDataStoreFactoryException("could not get quality datastore for "+ pathToDataStore);
    }
    
    
}
