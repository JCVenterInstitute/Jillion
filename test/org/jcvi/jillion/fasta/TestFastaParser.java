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

package org.jcvi.jillion.fasta;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaFileVisitor;
import org.jcvi.jillion.fasta.FastaFileVisitor.DeflineReturnCode;
import org.jcvi.jillion.fasta.FastaFileVisitor.EndOfBodyReturnCode;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestFastaParser {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestFastaParser.class);
    
    FastaFileVisitor mockVisitor;
    
    @Before
    public void setup(){
        mockVisitor = createMock(FastaFileVisitor.class);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullFileShouldThrowNPE() throws FileNotFoundException{
        FastaFileParser.parse((File)null, mockVisitor);
    }
    @Test(expected = NullPointerException.class)
    public void nullInputStreamShouldThrowNPE(){
        FastaFileParser.parse((InputStream)null, mockVisitor);
    }
    @Test(expected = NullPointerException.class)
    public void nullVisitorFileConstructorShouldThrowNPE() throws IOException{
        FastaFileParser.parse(getFastaFile() , null);
    }
    @Test(expected = NullPointerException.class)
    public void nullVisitorStreamConstructorShouldThrowNPE() throws IOException{
        FastaFileParser.parse(new ByteArrayInputStream(new byte[0]) , null);
    }
    @Test
    public void parseAllRecords() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLinesForFirstRecord();
        visitLinesForSecondRecord();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfFirstRecord();
        visitEndOfBodyAndReturn(EndOfBodyReturnCode.KEEP_PARSING);
        

        visitDeflineOfSecondRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfSecondRecord();
        visitEndOfBodyAndReturn(EndOfBodyReturnCode.KEEP_PARSING);
        
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        parseFastaWithVisitor();
        verify(mockVisitor);
    }
    /**
     * Should be the same visits as if we
     * parsed all records.
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Test
    public void stopAfterLastRecord() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLinesForFirstRecord();
        visitLinesForSecondRecord();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfFirstRecord();
        visitEndOfBodyAndReturn(EndOfBodyReturnCode.KEEP_PARSING);
        

        visitDeflineOfSecondRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfSecondRecord();
        visitEndOfBodyAndReturn(EndOfBodyReturnCode.STOP_PARSING);
        
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        parseFastaWithVisitor();
        verify(mockVisitor);
    }
    
    @Test(expected = IllegalStateException.class)
    public void returnNullOnLastVisitEndOfBodyShouldThrowIllegalStateException() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLinesForFirstRecord();
        visitLinesForSecondRecord();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfFirstRecord();
        visitEndOfBodyAndReturn(EndOfBodyReturnCode.KEEP_PARSING);
        

        visitDeflineOfSecondRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfSecondRecord();
        visitEndOfBodyAndReturn(null);
        
        replay(mockVisitor);
        parseFastaWithVisitor();
    }
    @Test
    public void skipFirstRecord() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLinesForFirstRecord();
        visitLinesForSecondRecord();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.SKIP_CURRENT_RECORD);
        

        visitDeflineOfSecondRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfSecondRecord();
        visitEndOfBodyAndReturn(EndOfBodyReturnCode.KEEP_PARSING);
        
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        parseFastaWithVisitor();
        verify(mockVisitor);
    }
    
    @Test
    public void skipLastRecord() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLinesForFirstRecord();
        visitLinesForSecondRecord();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfFirstRecord();
        visitEndOfBodyAndReturn(EndOfBodyReturnCode.KEEP_PARSING);
        

        visitDeflineOfSecondRecordAndReturn(DeflineReturnCode.SKIP_CURRENT_RECORD);
        
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        parseFastaWithVisitor();
        verify(mockVisitor);
    }
    
    @Test
    public void skipAllRecords() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLinesForFirstRecord();
        visitLinesForSecondRecord();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.SKIP_CURRENT_RECORD);
        

        visitDeflineOfSecondRecordAndReturn(DeflineReturnCode.SKIP_CURRENT_RECORD);
        
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        parseFastaWithVisitor();
        verify(mockVisitor);
    }
    
    @Test
    public void stopAfterFirstDeflineRecords() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLineForFirstDefline();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.STOP_PARSING);
        
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        parseFastaWithVisitor();
        verify(mockVisitor);
    }
    
    @Test(expected = IllegalStateException.class)
    public void returnNullOnVisitDeflineShouldThrowIllegalStateException() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLineForFirstDefline();
        
        visitDeflineOfFirstRecordAndReturn(null);
        
        replay(mockVisitor);
        parseFastaWithVisitor();
    }
    @Test
    public void stopAfterFirstRecord() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLinesForFirstRecord();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfFirstRecord();
        visitEndOfBodyAndReturn(EndOfBodyReturnCode.STOP_PARSING);
        
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        parseFastaWithVisitor();
        verify(mockVisitor);
    }
    
    
    
    @Test(expected = IllegalStateException.class)
    public void returningNullOnVisitEndOfBodyShouldThrowIllegalStateException() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLinesForFirstRecord();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfFirstRecord();
        visitEndOfBodyAndReturn(null);
        
      
        
        replay(mockVisitor);
        parseFastaWithVisitor();
    }
    @Test
    public void stopAfterSecondDefline() throws FileNotFoundException, IOException{
        
        mockVisitor.visitFile();        
        visitLinesForFirstRecord();
        
        visitDeflineOfFirstRecordAndReturn(DeflineReturnCode.VISIT_CURRENT_RECORD);
        visitBodyOfFirstRecord();
        visitEndOfBodyAndReturn(EndOfBodyReturnCode.KEEP_PARSING);
        
        visitLineForSecondDefline();
        visitDeflineOfSecondRecordAndReturn(DeflineReturnCode.STOP_PARSING);
        
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        parseFastaWithVisitor();
        verify(mockVisitor);
    }

	private void parseFastaWithVisitor() throws FileNotFoundException,
			IOException {
		FastaFileParser.parse(getFastaFile(), mockVisitor);
	}

	private File getFastaFile() throws IOException {
		return RESOURCES.getFile("files/seqs.fasta");
	}

    
    private void visitEndOfBodyAndReturn(EndOfBodyReturnCode ret){
    	 expect(mockVisitor.visitEndOfBody()).andReturn(ret);         
    }
    
    private void visitDeflineOfFirstRecordAndReturn(DeflineReturnCode ret){
    	expect(mockVisitor.visitDefline("IWKNA01T07A01PB2A1101R", "comment1")).andReturn(ret);
    }
    private void visitDeflineOfSecondRecordAndReturn(DeflineReturnCode ret){
    	expect(mockVisitor.visitDefline("IWKNA01T07A01PB2A1F",  "another comment")).andReturn(ret);
    }
	private void visitBodyOfFirstRecord() {
		mockVisitor.visitBodyLine("CCTCATGTACTCTTACTTTCAATGTTTGGAGGTTGCCCGTAAGCACTTCTTCTTCCCAAT");
        mockVisitor.visitBodyLine("AACTGACGACCCACTTGTTCTTTTGAAAGTGAATCCACCAAAGCTGAAAGATGAGCTAAT");
        mockVisitor.visitBodyLine("CCTTAAGCCCATTGCTGCCTTGCATATGTCCACAGCTTGTTCCTCAGTTGGATTTTGTCG");
        mockVisitor.visitBodyLine("AAGAATGTCTACCATCCTTATTCCCCCAATCTGTGTGCTATGGCACATCTCCAATAAAGA");
        mockVisitor.visitBodyLine("TGCTAGTGGGTCTGCTGATACCGTTGCTCTTCTTACTATGTTCCTGGCAGCAATAATCAA");
        mockVisitor.visitBodyLine("GCTTTGGTCAACATCATCATTTCTCACCTCCCCTCCTGGAGTATACATTTGCTCCCAGCA");
        mockVisitor.visitBodyLine("TGTTCCCTGGGTCAAATGCAGCACTTCAATATAGACACTGCTTGTTCCACCAGCCACTGG");
        mockVisitor.visitBodyLine("GAGGAATCTCGTTTTGCGGACCAACTCTCTTTCTAGCATGTATGCAACCATTAAGGGGGC");
        mockVisitor.visitBodyLine("AATTTTGCAGTCCTGGAGTTCTTCCTTCTTCTCTTTTGTTATCGTCAGCTGTGATTCCGA");
        mockVisitor.visitBodyLine("TGTTAGTATTCTAGCTCCCACTTCATTTGGGAAAACAACTTCCATGATTACATCCTGCGC");
        mockVisitor.visitBodyLine("CTCTTTGGCACTGAGGTCTGCGTGGCCAGGGTTTATGTCAACCCTCCGTCTTATTTTAAC");
        mockVisitor.visitBodyLine("TTGATTTCTGAAGTGGACAGGGCCAAAGGTCCCGTGTTTCAATCTTTCGACTTTTTCAAA");
        mockVisitor.visitBodyLine("ATAAGTTTTATATACCTTTGGGTAGTGAACTGTACTTGTTGTTGGTCCATTCCTATTCCA");
        mockVisitor.visitBodyLine("CCATGTTACAGCTAGAGGTGATACCATTACTCGGTCTGAGCCGGCA");
	}

	private void visitBodyOfSecondRecord() {
		mockVisitor.visitBodyLine("ATAAAAGAACTAAGAGATCTAATGTCGCAGTCTCGCACCCGCGAGATACTAACCAAAACC");
        mockVisitor.visitBodyLine("ACTGTGGACCACATGGCCATAATCAAAAAATACACATCAGGAAGACAAGAGAAGAACCCC");
        mockVisitor.visitBodyLine("GCACTCAGGATGAAGTGGATGATGGCAATGAAATATCCAATTACAGCAGATAAAAGAATA");
        mockVisitor.visitBodyLine("ATGGAAATGATTCCCGAAAGGAATGAACAAGGACAAACCCTCTGGAGTAAAACAAACGAT");
        mockVisitor.visitBodyLine("GCCGGCTCAGACCGAGTAATGGTATCACCTCTAGCTGTAACATGGTGGAATAGGAATGGA");
        mockVisitor.visitBodyLine("CCAACAACAAGTACAGTTCACTACCCAAAGGTATATAAAACTTATTTTGAAAAAGTCGAA");
        mockVisitor.visitBodyLine("AGATTGAAACACGGGACCTTTGGCCCTGTCCACTTCAGAAATCAAGTTAAAATAAGACGG");
        mockVisitor.visitBodyLine("AGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCCAAAGAGGCGCAGGATGTAATC");
        mockVisitor.visitBodyLine("ATGGAAGTTGTTTTCCCAAATGAAGTGGGAGCTAGAATACTAACATCGGAATCACAGCTG");
        mockVisitor.visitBodyLine("ACGATAACAAAAGAGAAGAAGGAAGAACTCCAGGACTGCAAAATTGCCCCCTTAATGGTT");
        mockVisitor.visitBodyLine("GCATACATGCTAGAAAGAGAGTTGGTCCGCAAAACGAGATTCCTCCCAGTGGCTGGTGGA");
        mockVisitor.visitBodyLine("ACAAGCAGTGTCTATATTGAAGTGCTGCATTTGACCCAGGGAACATGCTGGGAGCAAATG");
        mockVisitor.visitBodyLine("TATACTCCAGGAGGGGAGGTGAGAAATGATGATGTTGACCAAAGCTTGATTATTGCTGCC");
        mockVisitor.visitBodyLine("AGGAACATAGTAAGAAGAGCAACGGTATCAGCAGACCCACTAGCATCTCTATTGGAGATG");
        mockVisitor.visitBodyLine("TGCCATAGCACACAGATGGGGGAATAAGGATGGTAGACATTCTTCGACAAAATCCAACTG");
        mockVisitor.visitBodyLine("AGGAACAAGCTGTGGACATATGCAAGGCAGCAATGGGCTTAAGGATTAGCTCATCTTTCA");
        mockVisitor.visitBodyLine("GCTTTGGTGGATTCACT");
	}

	private void visitLinesForSecondRecord() {
		visitLineForSecondDefline();
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
	}

	private void visitLineForSecondDefline() {
		mockVisitor.visitLine(">IWKNA01T07A01PB2A1F  another comment\n");
	}

	private void visitLinesForFirstRecord() {
		visitLineForFirstDefline();
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
	}

	private void visitLineForFirstDefline() {
		mockVisitor.visitLine(">IWKNA01T07A01PB2A1101R comment1\n");
	}
    
    @Test
    public void parseEmptyFile(){
        mockVisitor.visitFile();
        mockVisitor.visitEndOfFile();
        
        replay(mockVisitor);
        FastaFileParser.parse(new ByteArrayInputStream(new byte[]{}), mockVisitor);
        verify(mockVisitor);
    }
}
