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
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.frg;

import java.io.IOException;
import java.util.Arrays;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.read.trace.frg.Frg2Parser;
import org.jcvi.common.core.seq.read.trace.frg.Frg2Visitor;
import org.jcvi.common.core.seq.read.trace.frg.Frg2Visitor.FrgAction;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;

import static org.easymock.EasyMock.*;
public class TestFrg2Parser {

    private static final String FILE = "files/example.frg2";
    
    private final static ResourceFileServer RESOURCES = new ResourceFileServer(TestFrg2Parser.class);
    
    
    @Test
    public void constructor(){
        new Frg2Parser();
    }
    @Test
    public void parseExampleFile() throws IOException{
        Frg2Parser sut = new Frg2Parser();
        Frg2Visitor mockVisitor = createMock(Frg2Visitor.class);
        mockVisitor.visitLine(isA(String.class));
        expectLastCall().anyTimes();
        mockVisitor.visitLibrary(FrgAction.ADD, ".", MateOrientation.INNIE, 
                Distance.buildDistance(5821F, 1513F));
        final Range clearRangeFor678 = Range.of(0,835);
        final Range clearRangeFor061 = Range.of(0,650);
        mockVisitor.visitFragment(FrgAction.ADD, 
                "334369678", ".", 
                new NucleotideSequenceBuilder(
                                "ATGATCGGCAGTGAATTGTATACGACTCACTATAGGGCGAATTGGAGCTCCACGCGGTGGCGGCCGCTCTAGAACTAGTGGATCCCCCGGGCTGCAGGAA" +
"TTCGATTAGGTGGAGGCCACGCTGCGCGACCCCAGCGCCCAGTCCGTAACGCACGTGCTGCAGGCAGGTGCCGGTCAGTGTGTGTGTGGTGGGGGCGGCG" +
"GCAGGGGGGTTGCGTACAGCATGGTGCTTGAAATTGGAAAGGAAGGAAGTCAGCCGTCAATGGAAGACACGAGTTAGTGCGGGCTTGCCCACATCATTGG" +
"CTGTGTATGGGGGGGGCGGTCATGGCTCAGAACGGAGTGATTACAGGCGCCATAGGCCGCCTGGCACAGCTTGACACAGGAGCACTCCCGCATGCATGCA" +
"CTGTCTCTGTCAGGTGTGACAGAGACAGTGTCACACCTGACATGCCGTGTTGCTCTCCTGTGTGTCCGGTGCCGCAGGAGCGCTCGCGCAAGCTGTCCTC" +
"GGACGTCAGCTCGCTCAAGCGCCAGCTGGGGGAGCGCGACAAGCAGGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGC" +
"GTGTGCGTGCGTGTGTGTGCGTGTGCGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGTGCGTGAGACGGAAAGAGCCAAG" +
"AAGAGCGCGAACTAAAGGAACAACATGGAAATAGGCGCGGCACCAAAGGTGAACCCTGGGCAACCCCATGGAATCCACAGGGAATCCCGTGTAAACCAAG" +
"GGACCTGAGGAGAGCACCAACAAGATCAGACGANNA" 
).build(), 

FrgTestUtil.decodeQualitySequence(
                        "555566;;;666;;<<<;;<<?CDDDB?<??<<<AADDHHHPVSUUKKG;98:<<>>=???B=;;=>@CDDB?BEDDDIKDVVVKKDDDDDKKKSNNQXP"+
                                "OLMMMUOPPPSNQJJKKKKKQbXNNPWJJJKKDHEEESYLLFGFFLbb^^^^WWW\\\\\\^\\\\XXX[NQSYYSSSSSSJJTTT[[dZZZYY[gg[[[[[XXR"+
                                "[YTGGGGGW`YYYYYRRRRR[YYY[dVdd\\YP``PPSMMPPPPMMNSZZ```````\\[YYYYdgggggggddgdddbb``gggdbZZZ\\gggggggggg`"+
                                "dddddddd``g`gg`````ggg`g`ggdd````````Z`g``bZZZgggggg`````g````````````Z\\\\ZZ`d``gg```dgddd````g``gg``"+
                                "gggggggggd`````dddd``ZZZ``````ddddddddZ``dggg`\\ZZZZZ```d`````ZZ`Z\\ZZZZ````````````dgg``g``gg[````gdZ"+
                                "ZZZZZdZZZYY`````gg`gg`````P`ggggg````````[gSXXgg``dVVVYT[][[[XXXggggggg]][ggggggggggg[[[ggggggggZSYY"+
                                "YYOOOOOO[[[[^^^^^^^^^^VVVQQPSPKKMEDD>DDJDGJEEGJJIDDEEEECAAHFGGJJJJLPLL<<;<<HE@::88786666667866667966"+
                                "6666877778744696657544466664546699877766667667<<766766778888866666789988868666886666666866677787778<"+
                                "9:99:8876666678776667666669987575005"
                )
        , clearRangeFor678, clearRangeFor678,
        "#  Not a comment; this is annotation about where the fragment came from\n");
        
        mockVisitor.visitFragment(FrgAction.ADD, 
                "334370061", ".", 
                new NucleotideSequenceBuilder(
					"ACTCAGCCTAAATACCTCACTAAGGGAACAAAGCTGGTACGGGCCCCCCCTCGAGGTCGACGGTATCGATAAGCTTGATCGGCTGGTCCCATTCGCCTTC" +
					"CCATTCCAATTCCCGTATTCCCATCCCCACTCCGATCCCCATTCGCAGATTCCCATTCCCATATTCACCATTCCCAGCCCCAGGCCACGCACCAGCGAGC" +
					"CCGAGAGCTCCGGCAGCAGCAGCGCAGCGGAGCCGCTCGGCGACATCCCCGCCGCCGCCCCGCCCAGCAGCTGCGACTGCGACGGCTGCGAGCCCGAGCT" +
					"CGAGCCCGTGAAGCCGCCTCCCGCCGCCGCAGCCGCGCCCCGCCCGCCTCCTCCGCCTCCGCCTGCGCCTCCGCCGGTGGCGTGCGTGGCTGCTGCTGTG" +
					"GCGAGATGCTCCTCCAGCTGCGCCACCAGCTGTGCCCGGTGCGCCAGGTCCGACTCCAGCGCCCGGATCTTGGAGCCCAGCTCGCCGATCTGCGGCGTGG" +
					"AGCCGTGGGTTGGTTGCGCGGTCCTCAGGGTCCCGTGGGGGTGATCAGTTGCATACCCGTGGGGATGCCATGGGGGATGGCGCAGGGTTCGACCGTGTGG" +
					"AGGGCGGGCGCAGAACCAGGGCGCAGGCACTAAGGCGCGCGCATCATGGGN").build(),
					FrgTestUtil.decodeQualitySequence(
		                "6689;;6687;>BG>?<??;:9??>NL?;::?9><??<??<::???G@C>888;;AGGGHKKKKKKHHKKKKPCCCCCASK=C=??COM[[bQS]bbbUU"+
		                "UbbbbbGGCCCCCCFLCFKKFFMSSSbbVVVVKGGGGGOOOOOMUUVVIIIIGGMMMKIKLULIKLbGGLLKKMMMUUUVSVSKKMVVNNNNNNNNSKKG"+
		                "KHHNNNGKKKKKSVVVSSS\\\\VVVXVVVV\\\\VQKHHHHHNSSRGGGGGKQJDD<;ADBEHJHMPWSSSUUUSSSSVVSSPSXVVKLBJ@JJQXXSQNVbV"+
		                "NNNNNURQOOHGGCBAA?DKGG?K?GEEJIGC===@@NSKJ=<=B@DDR[\\VVNKMMSSVLKNNKQQSWWOOGGEGGDDDDDGVNSSSNKKNNNSVNNSV"+
		                "VVPOOSUSUUV[[WSSSNSKQJGGEEEGGNGJHQMOOUUUUUQUUNSVSKPKKKVVSQQVVV\\XXSSRXVbbVVV\\bVSSVSSSUUVUUVVUUUPOOKGE"+
		                "EEEEEEEEIFHD==?BBDGNOUOVKEAAADDDDEEGGFJIGGJHJGJKMLMJHHKKKNOLVJGB=>@@@>EEEIBIIJMGG><778>ADFJJLLGCCA@>"+
		                ">==BDGGGG??B===@A>==@??<<<<<<<;999;;BBBBBBBBGB=4440"		
							),
		clearRangeFor061,clearRangeFor061,"");
		
		mockVisitor.visitLink(FrgAction.ADD, Arrays.asList("334370061","334369678"));
		mockVisitor.visitEndOfFile();        
		replay(mockVisitor);
        sut.parse(RESOURCES.getFileAsStream(FILE), mockVisitor);
        
        verify(mockVisitor);
    }
    
    
}
