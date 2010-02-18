/*
 * Created on Apr 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.junit.Test;

public class TestDefaultSequenceFastaMap extends AbstractTestSequenceFastaMap {

    @Test
    public void parseStream() throws DataStoreException{
        DefaultNucleotideFastaFileDataStore sut = new DefaultNucleotideFastaFileDataStore();
        FastaParser.parseFasta(getFileAsStream(),sut);
        assertParsedCorrectly(sut);
    }

    @Override
    protected DefaultNucleotideFastaFileDataStore buildSequenceFastaMap(File file)
            throws IOException {
        return new DefaultNucleotideFastaFileDataStore(file);
    }
    
}
