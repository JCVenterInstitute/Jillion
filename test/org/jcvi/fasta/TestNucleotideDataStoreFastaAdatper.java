/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.datastore.NucleotideDataStoreAdapter;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestNucleotideDataStoreFastaAdatper extends AbstractTestSequenceFastaDataStoreWithNoComment{

    @Override
    protected DataStore<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> buildMap(
            File file) throws IOException {
        return new DefaultNucleotideFastaFileDataStore(file);
    }

    @Test
    public void adaptFasta() throws IOException, DataStoreException{
        NucleotideDataStore sut=
        new NucleotideDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(buildMap(
                new File(TestNucleotideDataStoreFastaAdatper.class.getResource(FASTA_FILE_PATH).getFile()))));
    
        assertEquals(
                sut.get("hrv-61").decode(), hrv_61.getValues().decode());
    }
}
