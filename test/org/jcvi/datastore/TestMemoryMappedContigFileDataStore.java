/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.assembly.Contig;

public class TestMemoryMappedContigFileDataStore extends AbstractTestContigFileDataStore{
   
    @Override
    protected MemoryMappedContigFileDataStore buildContigFileDataStore(
            File file) throws FileNotFoundException {
        return new MemoryMappedContigFileDataStore(file);
    }
    @Override
    protected Contig getContig928From(File file) throws FileNotFoundException, DataStoreException{
        return buildContigFileDataStore(file).get("928");
    }
}
