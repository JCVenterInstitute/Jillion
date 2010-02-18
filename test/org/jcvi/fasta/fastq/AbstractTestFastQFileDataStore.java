/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.jcvi.CommonUtil;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;

public abstract class AbstractTestFastQFileDataStore {
    static final SolexaFastQQualityCodec QUALITY_CODEC = new SolexaFastQQualityCodec(
            new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE));
    FastQFileVisitor sut = new DefaultFastQFileDataStore(QUALITY_CODEC);
    String file = "files/example.fastq";
    ResourceFileServer resources = new ResourceFileServer(
            TestDefaultFastQFileDataStore.class);
    DefaultFastQRecord solexa_1489 = new DefaultFastQRecord(
            "SOLEXA1:4:1:12:1489#0/1",
            new DefaultNucleotideEncodedGlyphs(
                    NucleotideGlyph
                            .getGlyphsFor("TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT")),
            QUALITY_CODEC
                    .decode("abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S"));

    DefaultFastQRecord solexa_1692 = new DefaultFastQRecord(
            "SOLEXA1:4:1:12:1692#0/1",
            new DefaultNucleotideEncodedGlyphs(
                    NucleotideGlyph
                            .getGlyphsFor("ACGCCTGCGTTATGGTNTAACAGGCATTCCGCCCCAGACAAACTCCCCCCCTAACCATGTCTTTCGCAAAAATCAGTCAATAAATGACCTTAACTTTAGA")),
            QUALITY_CODEC
                    .decode("`a\\a`^\\a^ZZa[]^WB_aaaa^^a`]^a`^`aaa`]``aXaaS^a^YaZaTW]a_aPY\\_UVY[P_ZHQY_NLZUR[^UZ\\TZWT_[_VWMWaRFW]BB"),
            "example comment");

    protected abstract FastQFileVisitor createFastQFileDataStore(File file,FastQQualityCodec qualityCodec);
    public void setup() throws IOException{
        sut = createFastQFileDataStore(resources.getFile(file), QUALITY_CODEC);
    }
    @Test
    public void parse() throws IOException, DataStoreException {
        FastQFileParser.parse(resources.getFileAsStream(file), sut);
        DataStore<FastQRecord> dataStore = (DataStore<FastQRecord>)sut;
        assertEquals(2, dataStore.size());
        assertTrue(dataStore.contains(solexa_1489.getId()));
        assertFalse(dataStore.contains("notInDataStore"));
        assertFastQRecordsEqual(solexa_1489, dataStore.get(solexa_1489.getId()));
        assertFastQRecordsEqual(solexa_1692, dataStore.get(solexa_1692.getId()));
        dataStore.close();
        try{
            dataStore.get(solexa_1489.getId());
            fail("should throw exception when get called when already closed");
        }catch(DataStoreException e){
            //pass
        }
    }

    private void assertFastQRecordsEqual(FastQRecord expected,
            FastQRecord actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getNucleotides().decode(), actual
                .getNucleotides().decode());
        assertEquals(expected.getQualities().decode(), actual.getQualities()
                .decode());
        assertTrue(CommonUtil.similarTo(expected.getComment(), actual
                .getComment()));
    }
}
