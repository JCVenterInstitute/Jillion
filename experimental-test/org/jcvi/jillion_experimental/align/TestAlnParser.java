/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion_experimental.align.AlnVisitor.ConservationInfo;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAlnParser {

    private final ResourceHelper resources = new ResourceHelper(TestAlnParser.class);
    private AlnVisitor sut;
    @Before
    public void setup(){
        sut = createMock(AlnVisitor.class);
    }
    @Test
    public void testInputStream() throws IOException{
        setupExpectations();
        replay(sut);
        InputStream in =null;
        try{
            in = resources.getFileAsStream("files/example.aln");
            AlnParser.parse(in, sut);
            verify(sut);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    @Test
    public void testFile() throws IOException{
        setupExpectations();
        replay(sut);
        AlnParser.parse(resources.getFile("files/example.aln"), sut);
        verify(sut);
    }
    /**
     * 
     */
    private void setupExpectations() {
        sut.visitFile();
        sut.visitLine(isA(String.class));
        expectLastCall().times(29);
        
        sut.visitBeginGroup();
        sut.visitAlignedSegment("gi|304633245|gb|HQ003817.1|",  "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        sut.visitAlignedSegment("gi|317140354|gb|HQ413315.1|",  "-ATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        sut.visitAlignedSegment("gi|33330439|gb|AF534906.1|",   "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        sut.visitAlignedSegment("gi|9626158|ref|NC_001405.1|",  "CATCATCA-TAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        sut.visitAlignedSegment("gi|56160492|ref|AC_000007.1|", "CATCATCA-TAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        sut.visitAlignedSegment("gi|33465830|gb|AY339865.1|",   "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        sut.visitAlignedSegment("gi|58177684|gb|AY601635.1|",   "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        sut.visitConservationInfo(parseConservationInfoFor(" ******* *****************************************"));
        sut.visitEndGroup();
        
        
        sut.visitBeginGroup();
        sut.visitAlignedSegment("gi|304633245|gb|HQ003817.1|",  "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        sut.visitAlignedSegment("gi|317140354|gb|HQ413315.1|",  "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        sut.visitAlignedSegment("gi|33330439|gb|AF534906.1|",   "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        sut.visitAlignedSegment("gi|9626158|ref|NC_001405.1|",  "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        sut.visitAlignedSegment("gi|56160492|ref|AC_000007.1|", "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        sut.visitAlignedSegment("gi|33465830|gb|AY339865.1|",   "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        sut.visitAlignedSegment("gi|58177684|gb|AY601635.1|",   "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        sut.visitConservationInfo(parseConservationInfoFor("**************************************************"));
        sut.visitEndGroup();
        
        sut.visitBeginGroup();
        sut.visitAlignedSegment("gi|304633245|gb|HQ003817.1|",  "AAGGTATATTATTGATGATG");
        sut.visitAlignedSegment("gi|317140354|gb|HQ413315.1|",  "AAGGTATATTATTGATGATG");
        sut.visitAlignedSegment("gi|33330439|gb|AF534906.1|",   "AAGGTATATTATTGATGATG");
        sut.visitAlignedSegment("gi|9626158|ref|NC_001405.1|",  "AAGGTATATTAT-GATGATG");
        sut.visitAlignedSegment("gi|56160492|ref|AC_000007.1|", "AAGGTATATTAT-GATGATG");
        sut.visitAlignedSegment("gi|33465830|gb|AY339865.1|",   "AAGGTATATTATTGATGATG");
        sut.visitAlignedSegment("gi|58177684|gb|AY601635.1|",   "AAGGTATATTATTGATGATG");
        sut.visitConservationInfo(parseConservationInfoFor("************ *******"));
        sut.visitEndGroup();
        sut.visitEndOfFile();
        
    }
    
    private List<ConservationInfo> parseConservationInfoFor(String info){
        List<ConservationInfo> result = new ArrayList<AlnVisitor.ConservationInfo>(info.length());
        for(int i=0; i< info.length(); i++){
            switch(info.charAt(i)){
                case '*' :  result.add(ConservationInfo.IDENTICAL);
                            break;
                case ':' :  result.add(ConservationInfo.CONSERVED_SUBSITUTION);
                            break;
                case '.' :  result.add(ConservationInfo.SEMI_CONSERVED_SUBSITUTION);
                            break;
                default:    result.add(ConservationInfo.NOT_CONSERVED);
                            break;
            }
        }
        return result;
    }
}
