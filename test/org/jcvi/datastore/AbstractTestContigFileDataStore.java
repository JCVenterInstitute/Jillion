/*
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestContigFileDataStore extends TestContigFileParser{

    @Test
    public void thereAre4Contigs() throws FileNotFoundException, DataStoreException{
        ContigDataStore<PlacedRead, Contig<PlacedRead>> dataStore = buildContigFileDataStore(getFile());
        assertEquals(4, dataStore.size());
    }
    @Override
    protected Contig getContig925From(File file) throws FileNotFoundException {
        ContigDataStore<PlacedRead, Contig<PlacedRead>> dataStore = buildContigFileDataStore(file);
        return getContig(dataStore, "925");
    }
    @Override
    protected Contig getContig928From(File file) throws Exception{
        ContigDataStore<PlacedRead, Contig<PlacedRead>> dataStore = buildContigFileDataStore(file);
        return getContig(dataStore, "928");
    }
    private Contig getContig(
            ContigDataStore<PlacedRead, Contig<PlacedRead>> dataStore, String id) {
        try {
            return dataStore.get(id);
        } catch (DataStoreException e) {
            e.printStackTrace();
            throw new RuntimeException("error getting contig "+id,e);
        }
    }
    protected abstract ContigDataStore<PlacedRead, Contig<PlacedRead>> buildContigFileDataStore(
            File file) throws FileNotFoundException;

}
