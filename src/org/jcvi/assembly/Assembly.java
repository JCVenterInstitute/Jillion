/*
 * Created on Jan 7, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.io.File;
import java.util.List;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public interface Assembly<C extends Contig, D extends DataStore<C>> {

    D getContigDataStore();
    DataStore<EncodedGlyphs<PhredQuality>> getQualityDataStore();
    List<File> getQualityFiles();
    
    DataStore<NucleotideEncodedGlyphs> getNucleotideDataStore();
    List<File> getNuceotideFiles();
    
}
