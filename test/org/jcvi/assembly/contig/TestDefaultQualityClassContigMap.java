/*
 * Created on Feb 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
public class TestDefaultQualityClassContigMap {
    CoverageMap<CoverageRegion<PlacedRead>> coverageMap;
    EncodedGlyphs<NucleotideGlyph> consensus;
    DataStore<EncodedGlyphs<PhredQuality>> qualityFastaMap;
    QualityClassComputer<PlacedRead, NucleotideGlyph> qualityClassComputer;
   DefaultQualityClassContigMap sut;
   @Before
   public void setup(){
       coverageMap = createMock(CoverageMap.class);
       consensus= createMock(EncodedGlyphs.class);
       qualityFastaMap= createMock(DataStore.class);
       qualityClassComputer= createMock(QualityClassComputer.class);
   }
    @Test
    public void emtpyConsensusShouldReturnEmptyMap(){
        expect(consensus.getLength()).andReturn(0L).atLeastOnce();
        replay(coverageMap,consensus,qualityFastaMap,qualityClassComputer);
        DefaultQualityClassContigMap sut = new DefaultQualityClassContigMap(coverageMap,consensus,
                qualityFastaMap,qualityClassComputer);
        assertTrue(sut.getQualityClassRegions().isEmpty());
        verify(coverageMap,consensus,qualityFastaMap,qualityClassComputer);
        
    }
}
