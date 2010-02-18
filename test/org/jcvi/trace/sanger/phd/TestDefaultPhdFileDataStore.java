/*
 * Created on Nov 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.junit.Test;

public class TestDefaultPhdFileDataStore extends AbstractTestPhd{

    @Test
    public void parse() throws IOException, DataStoreException{
        DefaultPhdFileDataStore sut = new DefaultPhdFileDataStore();
        
        PhdParser.parsePhd(RESOURCE.getFileAsStream(PHD_FILE), sut);
        Phd actual = sut.get("1095595674585");
        assertEquals(expectedQualities, actual.getQualities().decode());        
        assertEquals(expectedPositions, actual.getPeaks().getData().decode());      
        assertEquals(expectedBasecalls, NucleotideGlyph.convertToString(actual.getBasecalls().decode()));
        assertEquals(expectedProperties, actual.getComments());
    }
}
