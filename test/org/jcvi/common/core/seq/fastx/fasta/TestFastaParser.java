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

package org.jcvi.common.core.seq.fastx.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaVisitor;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestFastaParser {

    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestFastaParser.class);
    
    FastaVisitor mockVisitor;
    
    @Before
    public void setup(){
        mockVisitor = createMock(FastaVisitor.class);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullFileShouldThrowNPE() throws FileNotFoundException{
        FastaParser.parseFasta((File)null, mockVisitor);
    }
    @Test(expected = NullPointerException.class)
    public void nullInputStreamShouldThrowNPE(){
        FastaParser.parseFasta((InputStream)null, mockVisitor);
    }
    
    @Test
    public void parseFastaFile() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();
        
        mockVisitor.visitLine(">IWKNA01T07A01PB2A1101R comment1\n");
        expect(mockVisitor.visitDefline(">IWKNA01T07A01PB2A1101R comment1")).andReturn(true);
        
        expect(mockVisitor.visitBodyLine("CCTCATGTACTCTTACTTTCAATGTTTGGAGGTTGCCCGTAAGCACTTCTTCTTCCCAAT")).andReturn(true);
        expect(mockVisitor.visitBodyLine("AACTGACGACCCACTTGTTCTTTTGAAAGTGAATCCACCAAAGCTGAAAGATGAGCTAAT")).andReturn(true);
        expect(mockVisitor.visitBodyLine("CCTTAAGCCCATTGCTGCCTTGCATATGTCCACAGCTTGTTCCTCAGTTGGATTTTGTCG")).andReturn(true);
        expect(mockVisitor.visitBodyLine("AAGAATGTCTACCATCCTTATTCCCCCAATCTGTGTGCTATGGCACATCTCCAATAAAGA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("TGCTAGTGGGTCTGCTGATACCGTTGCTCTTCTTACTATGTTCCTGGCAGCAATAATCAA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("GCTTTGGTCAACATCATCATTTCTCACCTCCCCTCCTGGAGTATACATTTGCTCCCAGCA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("TGTTCCCTGGGTCAAATGCAGCACTTCAATATAGACACTGCTTGTTCCACCAGCCACTGG")).andReturn(true);
        expect(mockVisitor.visitBodyLine("GAGGAATCTCGTTTTGCGGACCAACTCTCTTTCTAGCATGTATGCAACCATTAAGGGGGC")).andReturn(true);
        expect(mockVisitor.visitBodyLine("AATTTTGCAGTCCTGGAGTTCTTCCTTCTTCTCTTTTGTTATCGTCAGCTGTGATTCCGA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("TGTTAGTATTCTAGCTCCCACTTCATTTGGGAAAACAACTTCCATGATTACATCCTGCGC")).andReturn(true);
        expect(mockVisitor.visitBodyLine("CTCTTTGGCACTGAGGTCTGCGTGGCCAGGGTTTATGTCAACCCTCCGTCTTATTTTAAC")).andReturn(true);
        expect(mockVisitor.visitBodyLine("TTGATTTCTGAAGTGGACAGGGCCAAAGGTCCCGTGTTTCAATCTTTCGACTTTTTCAAA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("ATAAGTTTTATATACCTTTGGGTAGTGAACTGTACTTGTTGTTGGTCCATTCCTATTCCA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("CCATGTTACAGCTAGAGGTGATACCATTACTCGGTCTGAGCCGGCA")).andReturn(true);
        
        mockVisitor.visitLine("CCTCATGTACTCTTACTTTCAATGTTTGGAGGTTGCCCGTAAGCACTTCTTCTTCCCAAT\n");
        mockVisitor.visitLine("AACTGACGACCCACTTGTTCTTTTGAAAGTGAATCCACCAAAGCTGAAAGATGAGCTAAT\n");
        mockVisitor.visitLine("CCTTAAGCCCATTGCTGCCTTGCATATGTCCACAGCTTGTTCCTCAGTTGGATTTTGTCG\n");
        mockVisitor.visitLine("AAGAATGTCTACCATCCTTATTCCCCCAATCTGTGTGCTATGGCACATCTCCAATAAAGA\n");
        mockVisitor.visitLine("TGCTAGTGGGTCTGCTGATACCGTTGCTCTTCTTACTATGTTCCTGGCAGCAATAATCAA\n");
        mockVisitor.visitLine("GCTTTGGTCAACATCATCATTTCTCACCTCCCCTCCTGGAGTATACATTTGCTCCCAGCA\n");
        mockVisitor.visitLine("TGTTCCCTGGGTCAAATGCAGCACTTCAATATAGACACTGCTTGTTCCACCAGCCACTGG\n");
        mockVisitor.visitLine("GAGGAATCTCGTTTTGCGGACCAACTCTCTTTCTAGCATGTATGCAACCATTAAGGGGGC\n");
        mockVisitor.visitLine("AATTTTGCAGTCCTGGAGTTCTTCCTTCTTCTCTTTTGTTATCGTCAGCTGTGATTCCGA\n");
        mockVisitor.visitLine("TGTTAGTATTCTAGCTCCCACTTCATTTGGGAAAACAACTTCCATGATTACATCCTGCGC\n");
        mockVisitor.visitLine("CTCTTTGGCACTGAGGTCTGCGTGGCCAGGGTTTATGTCAACCCTCCGTCTTATTTTAAC\n");
        mockVisitor.visitLine("TTGATTTCTGAAGTGGACAGGGCCAAAGGTCCCGTGTTTCAATCTTTCGACTTTTTCAAA\n");
        mockVisitor.visitLine("ATAAGTTTTATATACCTTTGGGTAGTGAACTGTACTTGTTGTTGGTCCATTCCTATTCCA\n");
        mockVisitor.visitLine("CCATGTTACAGCTAGAGGTGATACCATTACTCGGTCTGAGCCGGCA\n");

        expect(mockVisitor.visitRecord("IWKNA01T07A01PB2A1101R", "comment1", 
        
                "CCTCATGTACTCTTACTTTCAATGTTTGGAGGTTGCCCGTAAGCACTTCTTCTTCCCAAT\n" +
                "AACTGACGACCCACTTGTTCTTTTGAAAGTGAATCCACCAAAGCTGAAAGATGAGCTAAT\n" +
                "CCTTAAGCCCATTGCTGCCTTGCATATGTCCACAGCTTGTTCCTCAGTTGGATTTTGTCG\n" +
                "AAGAATGTCTACCATCCTTATTCCCCCAATCTGTGTGCTATGGCACATCTCCAATAAAGA\n" +
                "TGCTAGTGGGTCTGCTGATACCGTTGCTCTTCTTACTATGTTCCTGGCAGCAATAATCAA\n" +
                "GCTTTGGTCAACATCATCATTTCTCACCTCCCCTCCTGGAGTATACATTTGCTCCCAGCA\n" +
                "TGTTCCCTGGGTCAAATGCAGCACTTCAATATAGACACTGCTTGTTCCACCAGCCACTGG\n" +
                "GAGGAATCTCGTTTTGCGGACCAACTCTCTTTCTAGCATGTATGCAACCATTAAGGGGGC\n" +
                "AATTTTGCAGTCCTGGAGTTCTTCCTTCTTCTCTTTTGTTATCGTCAGCTGTGATTCCGA\n" +
                "TGTTAGTATTCTAGCTCCCACTTCATTTGGGAAAACAACTTCCATGATTACATCCTGCGC\n" +
                "CTCTTTGGCACTGAGGTCTGCGTGGCCAGGGTTTATGTCAACCCTCCGTCTTATTTTAAC\n" +
                "TTGATTTCTGAAGTGGACAGGGCCAAAGGTCCCGTGTTTCAATCTTTCGACTTTTTCAAA\n" +
                "ATAAGTTTTATATACCTTTGGGTAGTGAACTGTACTTGTTGTTGGTCCATTCCTATTCCA\n" +
                "CCATGTTACAGCTAGAGGTGATACCATTACTCGGTCTGAGCCGGCA\n"
                )
        ).andReturn(true);
        expect( mockVisitor.visitDefline(">IWKNA01T07A01PB2A1F  another comment")).andReturn(true);
        expect(mockVisitor.visitBodyLine("ATAAAAGAACTAAGAGATCTAATGTCGCAGTCTCGCACCCGCGAGATACTAACCAAAACC")).andReturn(true);
        expect(mockVisitor.visitBodyLine("ACTGTGGACCACATGGCCATAATCAAAAAATACACATCAGGAAGACAAGAGAAGAACCCC")).andReturn(true);
        expect(mockVisitor.visitBodyLine("GCACTCAGGATGAAGTGGATGATGGCAATGAAATATCCAATTACAGCAGATAAAAGAATA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("ATGGAAATGATTCCCGAAAGGAATGAACAAGGACAAACCCTCTGGAGTAAAACAAACGAT")).andReturn(true);
        expect(mockVisitor.visitBodyLine("GCCGGCTCAGACCGAGTAATGGTATCACCTCTAGCTGTAACATGGTGGAATAGGAATGGA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("CCAACAACAAGTACAGTTCACTACCCAAAGGTATATAAAACTTATTTTGAAAAAGTCGAA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("AGATTGAAACACGGGACCTTTGGCCCTGTCCACTTCAGAAATCAAGTTAAAATAAGACGG")).andReturn(true);
        expect(mockVisitor.visitBodyLine("AGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCCAAAGAGGCGCAGGATGTAATC")).andReturn(true);
        expect(mockVisitor.visitBodyLine("ATGGAAGTTGTTTTCCCAAATGAAGTGGGAGCTAGAATACTAACATCGGAATCACAGCTG")).andReturn(true);
        expect(mockVisitor.visitBodyLine("ACGATAACAAAAGAGAAGAAGGAAGAACTCCAGGACTGCAAAATTGCCCCCTTAATGGTT")).andReturn(true);
        expect(mockVisitor.visitBodyLine("GCATACATGCTAGAAAGAGAGTTGGTCCGCAAAACGAGATTCCTCCCAGTGGCTGGTGGA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("ACAAGCAGTGTCTATATTGAAGTGCTGCATTTGACCCAGGGAACATGCTGGGAGCAAATG")).andReturn(true);
        expect(mockVisitor.visitBodyLine("TATACTCCAGGAGGGGAGGTGAGAAATGATGATGTTGACCAAAGCTTGATTATTGCTGCC")).andReturn(true);
        expect(mockVisitor.visitBodyLine("AGGAACATAGTAAGAAGAGCAACGGTATCAGCAGACCCACTAGCATCTCTATTGGAGATG")).andReturn(true);
        expect(mockVisitor.visitBodyLine("TGCCATAGCACACAGATGGGGGAATAAGGATGGTAGACATTCTTCGACAAAATCCAACTG")).andReturn(true);
        expect(mockVisitor.visitBodyLine("AGGAACAAGCTGTGGACATATGCAAGGCAGCAATGGGCTTAAGGATTAGCTCATCTTTCA")).andReturn(true);
        expect(mockVisitor.visitBodyLine("GCTTTGGTGGATTCACT")).andReturn(true);
        
        mockVisitor.visitLine(">IWKNA01T07A01PB2A1F  another comment\n");
        mockVisitor.visitLine("ATAAAAGAACTAAGAGATCTAATGTCGCAGTCTCGCACCCGCGAGATACTAACCAAAACC\n");
        mockVisitor.visitLine("ACTGTGGACCACATGGCCATAATCAAAAAATACACATCAGGAAGACAAGAGAAGAACCCC\n");
        mockVisitor.visitLine("GCACTCAGGATGAAGTGGATGATGGCAATGAAATATCCAATTACAGCAGATAAAAGAATA\n");
        mockVisitor.visitLine("ATGGAAATGATTCCCGAAAGGAATGAACAAGGACAAACCCTCTGGAGTAAAACAAACGAT\n");
        mockVisitor.visitLine("GCCGGCTCAGACCGAGTAATGGTATCACCTCTAGCTGTAACATGGTGGAATAGGAATGGA\n");
        mockVisitor.visitLine("CCAACAACAAGTACAGTTCACTACCCAAAGGTATATAAAACTTATTTTGAAAAAGTCGAA\n");
        mockVisitor.visitLine("AGATTGAAACACGGGACCTTTGGCCCTGTCCACTTCAGAAATCAAGTTAAAATAAGACGG\n");
        mockVisitor.visitLine("AGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCCAAAGAGGCGCAGGATGTAATC\n");
        mockVisitor.visitLine("ATGGAAGTTGTTTTCCCAAATGAAGTGGGAGCTAGAATACTAACATCGGAATCACAGCTG\n");
        mockVisitor.visitLine("ACGATAACAAAAGAGAAGAAGGAAGAACTCCAGGACTGCAAAATTGCCCCCTTAATGGTT\n");
        mockVisitor.visitLine("GCATACATGCTAGAAAGAGAGTTGGTCCGCAAAACGAGATTCCTCCCAGTGGCTGGTGGA\n");
        mockVisitor.visitLine("ACAAGCAGTGTCTATATTGAAGTGCTGCATTTGACCCAGGGAACATGCTGGGAGCAAATG\n");
        mockVisitor.visitLine("TATACTCCAGGAGGGGAGGTGAGAAATGATGATGTTGACCAAAGCTTGATTATTGCTGCC\n");
        mockVisitor.visitLine("AGGAACATAGTAAGAAGAGCAACGGTATCAGCAGACCCACTAGCATCTCTATTGGAGATG\n");
        mockVisitor.visitLine("TGCCATAGCACACAGATGGGGGAATAAGGATGGTAGACATTCTTCGACAAAATCCAACTG\n");
        mockVisitor.visitLine("AGGAACAAGCTGTGGACATATGCAAGGCAGCAATGGGCTTAAGGATTAGCTCATCTTTCA\n");
        mockVisitor.visitLine("GCTTTGGTGGATTCACT\n");
        
        expect(mockVisitor.visitRecord("IWKNA01T07A01PB2A1F", "another comment", 
                
                "ATAAAAGAACTAAGAGATCTAATGTCGCAGTCTCGCACCCGCGAGATACTAACCAAAACC\n" +
                "ACTGTGGACCACATGGCCATAATCAAAAAATACACATCAGGAAGACAAGAGAAGAACCCC\n" +
                "GCACTCAGGATGAAGTGGATGATGGCAATGAAATATCCAATTACAGCAGATAAAAGAATA\n" +
                "ATGGAAATGATTCCCGAAAGGAATGAACAAGGACAAACCCTCTGGAGTAAAACAAACGAT\n" +
                "GCCGGCTCAGACCGAGTAATGGTATCACCTCTAGCTGTAACATGGTGGAATAGGAATGGA\n" +
                "CCAACAACAAGTACAGTTCACTACCCAAAGGTATATAAAACTTATTTTGAAAAAGTCGAA\n" +
                "AGATTGAAACACGGGACCTTTGGCCCTGTCCACTTCAGAAATCAAGTTAAAATAAGACGG\n" +
                "AGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCCAAAGAGGCGCAGGATGTAATC\n" +
                "ATGGAAGTTGTTTTCCCAAATGAAGTGGGAGCTAGAATACTAACATCGGAATCACAGCTG\n" +
                "ACGATAACAAAAGAGAAGAAGGAAGAACTCCAGGACTGCAAAATTGCCCCCTTAATGGTT\n" +
                "GCATACATGCTAGAAAGAGAGTTGGTCCGCAAAACGAGATTCCTCCCAGTGGCTGGTGGA\n" +
                "ACAAGCAGTGTCTATATTGAAGTGCTGCATTTGACCCAGGGAACATGCTGGGAGCAAATG\n" +
                "TATACTCCAGGAGGGGAGGTGAGAAATGATGATGTTGACCAAAGCTTGATTATTGCTGCC\n" +
                "AGGAACATAGTAAGAAGAGCAACGGTATCAGCAGACCCACTAGCATCTCTATTGGAGATG\n" +
                "TGCCATAGCACACAGATGGGGGAATAAGGATGGTAGACATTCTTCGACAAAATCCAACTG\n" +
                "AGGAACAAGCTGTGGACATATGCAAGGCAGCAATGGGCTTAAGGATTAGCTCATCTTTCA\n" +
                "GCTTTGGTGGATTCACT\n"
        )).andReturn(true);
        
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        FastaParser.parseFasta(RESOURCES.getFile("files/seqs.fasta"), mockVisitor);
        verify(mockVisitor);
    }
}
