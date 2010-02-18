/*
 * Created on Feb 20, "2009" +
 *
 * @author "dkatzel" +
 */
package org.jcvi.fasta;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.junit.Test;

public abstract class AbstractTestSequenceFastaMapWithNoComment extends AbstractTestSequenceFastaDataStoreWithNoComment{
    
    @Test
    public void parseStream() throws IOException, DataStoreException{
        DataStore<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>> sut = buildMap(new File(AbstractTestSequenceFastaMap.class.getResource(FASTA_FILE_PATH).getFile()));
        assertEquals(1, sut.size());
        assertEquals(hrv_61, sut.get("hrv-61"));
    }

}
