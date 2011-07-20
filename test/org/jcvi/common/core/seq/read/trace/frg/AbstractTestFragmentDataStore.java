/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.frg;

import java.io.File;
import java.io.FileInputStream;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.read.trace.frg.AbstractFragmentDataStore;
import org.jcvi.common.core.seq.read.trace.frg.DefaultFragment;
import org.jcvi.common.core.seq.read.trace.frg.Fragment;
import org.jcvi.common.core.seq.read.trace.frg.Frg2Parser;
import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.TigrQualitiesEncodedGyphCodec;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class  AbstractTestFragmentDataStore {
    private static final String FILE = "files/example.frg2";
    
    private static final TigrQualitiesEncodedGyphCodec QUALITY_CODEC = TigrQualitiesEncodedGyphCodec.getINSTANCE();
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);

    Library library = new DefaultLibrary(".",Distance.buildDistance(5821F, 1513F), MateOrientation.INNIE);
    final Range clearRangeFor678 = Range.buildRange(0,835);
    final Range clearRangeFor061 = Range.buildRange(0,650);
    
    Fragment fragEndingIn78 = new DefaultFragment(
            "334369678",
            new DefaultNucleotideSequence(
                    NucleotideGlyph.getGlyphsFor(
                            "ATGATCGGCAGTGAATTGTATACGACTCACTATAGGGCGAATTGGAGCTCCACGCGGTGGCGGCCGCTCTAGAACTAGTGGATCCCCCGGGCTGCAGGAA" +
                            "TTCGATTAGGTGGAGGCCACGCTGCGCGACCCCAGCGCCCAGTCCGTAACGCACGTGCTGCAGGCAGGTGCCGGTCAGTGTGTGTGTGGTGGGGGCGGCG" +
                            "GCAGGGGGGTTGCGTACAGCATGGTGCTTGAAATTGGAAAGGAAGGAAGTCAGCCGTCAATGGAAGACACGAGTTAGTGCGGGCTTGCCCACATCATTGG" +
                            "CTGTGTATGGGGGGGGCGGTCATGGCTCAGAACGGAGTGATTACAGGCGCCATAGGCCGCCTGGCACAGCTTGACACAGGAGCACTCCCGCATGCATGCA" +
                            "CTGTCTCTGTCAGGTGTGACAGAGACAGTGTCACACCTGACATGCCGTGTTGCTCTCCTGTGTGTCCGGTGCCGCAGGAGCGCTCGCGCAAGCTGTCCTC" +
                            "GGACGTCAGCTCGCTCAAGCGCCAGCTGGGGGAGCGCGACAAGCAGGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGC" +
                            "GTGTGCGTGCGTGTGTGTGCGTGTGCGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGCGTGAGACGGAAAGAGCCAAG" +
                            "AAGAGCGCGAACTAAAGGAACAACATGGAAATAGGCGCGGCACCAAAGGTGAACCCTGGGCAACCCCATGGAATCCACAGGGAATCCCGTGTAAACCAAG" +
                            "GGACCTGAGGAGAGCACCAACAAGATCAGACGANNA")), 
            new EncodedQualitySequence(RUN_LENGTH_CODEC,
                    QUALITY_CODEC.decode(
                            (
                                    "555566;;;666;;<<<;;<<?CDDDB?<??<<<AADDHHHPVSUUKKG;98:<<>>=???B=;;=>@CDDB?BEDDDIKDVVVKKDDDDDKKKSNNQXP"+
                                "OLMMMUOPPPSNQJJKKKKKQbXNNPWJJJKKDHEEESYLLFGFFLbb^^^^WWW\\\\\\^\\\\XXX[NQSYYSSSSSSJJTTT[[dZZZYY[gg[[[[[XXR"+
                                "[YTGGGGGW`YYYYYRRRRR[YYY[dVdd\\YP``PPSMMPPPPMMNSZZ```````\\[YYYYdgggggggddgdddbb``gggdbZZZ\\gggggggggg`"+
                                "dddddddd``g`gg`````ggg`g`ggdd````````Z`g``bZZZgggggg`````g````````````Z\\\\ZZ`d``gg```dgddd````g``gg``"+
                                "gggggggggd`````dddd``ZZZ``````ddddddddZ``dggg`\\ZZZZZ```d`````ZZ`Z\\ZZZZ````````````dgg``g``gg[````gdZ"+
                                "ZZZZZdZZZYY`````gg`gg`````P`ggggg````````[gSXXgg``dVVVYT[][[[XXXggggggg]][ggggggggggg[[[ggggggggZSYY"+
                                "YYOOOOOO[[[[^^^^^^^^^^VVVQQPSPKKMEDD>DDJDGJEEGJJIDDEEEECAAHFGGJJJJLPLL<<;<<HE@::88786666667866667966"+
                                "6666877778744696657544466664546699877766667667<<766766778888866666789988868666886666666866677787778<"+
                                "9:99:8876666678776667666669987575005"
                            ).getBytes())),

            clearRangeFor678, clearRangeFor678,library,"#  Not a comment; this is annotation about where the fragment came from\n"
        );
    Fragment fragEndingIn61 = new DefaultFragment(
            "334370061",
            new DefaultNucleotideSequence(
                    NucleotideGlyph.getGlyphsFor(
                            "ACTCAGCCTAAATACCTCACTAAGGGAACAAAGCTGGTACGGGCCCCCCCTCGAGGTCGACGGTATCGATAAGCTTGATCGGCTGGTCCCATTCGCCTTC" +
                            "CCATTCCAATTCCCGTATTCCCATCCCCACTCCGATCCCCATTCGCAGATTCCCATTCCCATATTCACCATTCCCAGCCCCAGGCCACGCACCAGCGAGC" +
                            "CCGAGAGCTCCGGCAGCAGCAGCGCAGCGGAGCCGCTCGGCGACATCCCCGCCGCCGCCCCGCCCAGCAGCTGCGACTGCGACGGCTGCGAGCCCGAGCT" +
                            "CGAGCCCGTGAAGCCGCCTCCCGCCGCCGCAGCCGCGCCCCGCCCGCCTCCTCCGCCTCCGCCTGCGCCTCCGCCGGTGGCGTGCGTGGCTGCTGCTGTG" +
                            "GCGAGATGCTCCTCCAGCTGCGCCACCAGCTGTGCCCGGTGCGCCAGGTCCGACTCCAGCGCCCGGATCTTGGAGCCCAGCTCGCCGATCTGCGGCGTGG" +
                            "AGCCGTGGGTTGGTTGCGCGGTCCTCAGGGTCCCGTGGGGGTGATCAGTTGCATACCCGTGGGGATGCCATGGGGGATGGCGCAGGGTTCGACCGTGTGG" +
                            "AGGGCGGGCGCAGAACCAGGGCGCAGGCACTAAGGCGCGCGCATCATGGGN")), 
            new EncodedQualitySequence(RUN_LENGTH_CODEC,
                    QUALITY_CODEC.decode(
                            (
                                    "6689;;6687;>BG>?<??;:9??>NL?;::?9><??<??<::???G@C>888;;AGGGHKKKKKKHHKKKKPCCCCCASK=C=??COM[[bQS]bbbUU"+
                                    "UbbbbbGGCCCCCCFLCFKKFFMSSSbbVVVVKGGGGGOOOOOMUUVVIIIIGGMMMKIKLULIKLbGGLLKKMMMUUUVSVSKKMVVNNNNNNNNSKKG"+
                                    "KHHNNNGKKKKKSVVVSSS\\\\VVVXVVVV\\\\VQKHHHHHNSSRGGGGGKQJDD<;ADBEHJHMPWSSSUUUSSSSVVSSPSXVVKLBJ@JJQXXSQNVbV"+
                                    "NNNNNURQOOHGGCBAA?DKGG?K?GEEJIGC===@@NSKJ=<=B@DDR[\\VVNKMMSSVLKNNKQQSWWOOGGEGGDDDDDGVNSSSNKKNNNSVNNSV"+
                                    "VVPOOSUSUUV[[WSSSNSKQJGGEEEGGNGJHQMOOUUUUUQUUNSVSKPKKKVVSQQVVV\\XXSSRXVbbVVV\\bVSSVSSSUUVUUVVUUUPOOKGE"+
                                    "EEEEEEEEIFHD==?BBDGNOUOVKEAAADDDDEEGGFJIGGJHJGJKMLMJHHKKKNOLVJGB=>@@@>EEEIBIIJMGG><778>ADFJJLLGCCA@>"+
                                    ">==BDGGGG??B===@A>==@??<<<<<<<;999;;BBBBBBBBGB=4440"
                            ).getBytes())),

                            clearRangeFor061, clearRangeFor061,library,""
        );
    AbstractFragmentDataStore sut;
    
    ResourceFileServer RESOURCES = new ResourceFileServer(AbstractTestFragmentDataStore.class);
    
    @Before
    public void setup() throws Exception{
        File fileToParse = RESOURCES.getFile(FILE);
        sut = createFragmentDataStore(fileToParse);
        new Frg2Parser().parse(new FileInputStream(fileToParse), sut);
    }
    
    protected abstract AbstractFragmentDataStore createFragmentDataStore(File file) throws Exception;
    @Test
    public void assertFragEndingIn61isCorrect() throws DataStoreException{
        Fragment fragment = sut.get(fragEndingIn61.getId());
        assertValuesCorrect(fragEndingIn61, fragment);
    }
    @Test
    public void assertFragEndingIn78IsCorrect() throws DataStoreException{
        Fragment fragment = sut.get(fragEndingIn78.getId());
        assertValuesCorrect(fragEndingIn78, fragment);
    }
    
    @Test
    public void hasMate() throws DataStoreException{
        assertTrue(sut.hasMate(fragEndingIn78));
        assertTrue(sut.hasMate(fragEndingIn61));
    }
    @Test
    public void getMateOf() throws DataStoreException{
        assertEquals(fragEndingIn78, sut.getMateOf(fragEndingIn61));
        assertEquals(fragEndingIn61, sut.getMateOf(fragEndingIn78));
    }
    @Test
    public void contains() throws DataStoreException{
        assertTrue(sut.contains(fragEndingIn78.getId()));
        assertTrue(sut.contains(fragEndingIn61.getId()));
    }
    @Test
    public void containsLibrary() throws DataStoreException{
        assertTrue(sut.containsLibrary(library.getId()));
    }

    private void assertValuesCorrect(Fragment expectedFragment, Fragment actualFragment) {
        assertEquals(expectedFragment.getId(), actualFragment.getId());
        assertEquals(expectedFragment.getBasecalls(), actualFragment.getBasecalls());
        assertEquals(expectedFragment.getEncodedGlyphs(), actualFragment.getEncodedGlyphs());
        assertEquals(expectedFragment.getQualities(), actualFragment.getQualities());
        assertEquals(expectedFragment.getValidRange(), actualFragment.getValidRange());
        assertEquals(expectedFragment.getVectorClearRange(), actualFragment.getVectorClearRange());
        assertEquals(library, actualFragment.getLibrary());
        assertEquals(expectedFragment.getComment(), actualFragment.getComment());
        assertEquals(expectedFragment.getLength(), actualFragment.getLength());
        assertEquals(expectedFragment.getLibraryId(), actualFragment.getLibraryId());
        
    }
    
}
