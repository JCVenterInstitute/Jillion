/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestFastaParser {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestFastaParser.class);

    
    @Test(expected = NullPointerException.class)
    public void nullFileShouldThrowNPE() throws IOException{
        FastaFileParser.create((File)null);
    }
    @Test(expected = NullPointerException.class)
    public void nullInputStreamShouldThrowNPE() throws IOException{
    	FastaFileParser.create((InputStream)null);
    }
    @Test(expected = NullPointerException.class)
    public void nullVisitorShouldThrowNPE() throws IOException{
    	 FastaFileParser.create(getFastaFile()).parse(null);
    }
    
    @Test
    public void skipAllRecords() throws IOException{
    	
    	FastaVisitor visitor = new FastaVisitor() {
			int count=0;
			@Override
			public void visitEnd() {
				assertEquals(2, count);				
			}
			@Override
			public void halted() {
				throw new IllegalStateException("parsing was halted");		
			}
			
			@Override
			public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
					String id, String optionalComment) {
				count++;
				return null;
			}
		};
		
		FastaFileParser.create(getFastaFile()).parse(visitor);
    }
    
    
    @Test
    public void parseAllRecords() throws FileNotFoundException, IOException{
        
       FastaVisitor visitor = new FastaVisitor() {
			boolean visitedFirst=false;
			boolean visitedLast=false;
			@Override
			public void visitEnd() {
				assertTrue(visitedFirst);
				assertTrue(visitedLast);
				
			}
			@Override
			public void halted() {
				throw new IllegalStateException("parsing was halted");		
			}
			@Override
			public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(matchesFirstRecord(id,optionalComment)){
					visitedFirst=true;
					return new FirstRecordVisitor();
				}else if (matchesSecondRecord(id,optionalComment)){
					visitedLast=true;
					return new SecondRecordVisitor();
				}
				throw new AssertionError("def line did not match first or second record");
			}
	
			
		};
		FastaFileParser.create(getFastaFile()).parse(visitor);
    }
    
    private boolean matchesFirstRecord(String id, String optionalComment) {
    	return "IWKNA01T07A01PB2A1101R".equals(id) && "comment1".equals(optionalComment);
	}
    private boolean matchesSecondRecord(String id, String optionalComment) {
    	return "IWKNA01T07A01PB2A1F".equals(id) && "another comment".equals(optionalComment);
	}
    /**
     * Should be the same visits as if we
     * parsed all records.
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Test
    public void stopAfterLastRecord() throws FileNotFoundException, IOException{
        
    	FastaVisitor visitor = new FastaVisitor() {
			boolean visitedFirst=false;
			boolean visitedLast=false;
			@Override
			public void visitEnd() {				
				throw new IllegalStateException("parsing was NOT halted");	
			}
			@Override
			public void halted() {
				assertTrue(visitedFirst);
				assertTrue(visitedLast);	
			}
			
			@Override
			public FastaRecordVisitor visitDefline(final FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(matchesFirstRecord(id,optionalComment)){
					visitedFirst=true;
					return new FirstRecordVisitor();
				}else if (matchesSecondRecord(id,optionalComment)){
					visitedLast=true;
					return new SecondRecordVisitor(){

						@Override
						public void visitEnd() {
							super.visitEnd();
							callback.haltParsing();
						}
						
					};
				}
				throw new AssertionError("def line did not match first or second record");
			}
	
			
		};
		FastaFileParser.create(getFastaFile()).parse(visitor);
    }
  
    
    
    
    @Test
    public void skipFirstRecord() throws FileNotFoundException, IOException{
        
    	FastaVisitor visitor = new FastaVisitor() {
			boolean visitedFirst=false;
			boolean visitedLast=false;
			@Override
			public void visitEnd() {
				assertTrue(visitedFirst);
				assertTrue(visitedLast);
				
			}
			@Override
			public void halted() {
				throw new IllegalStateException("parsing was halted");
			}
			
			@Override
			public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(matchesFirstRecord(id,optionalComment)){
					visitedFirst=true;
					return null;
				}else if (matchesSecondRecord(id,optionalComment)){
					visitedLast=true;
					return new SecondRecordVisitor();
				}
				throw new AssertionError("def line did not match first or second record");
			}
	
			
		};
		FastaFileParser.create(getFastaFile()).parse(visitor);
    }
    
    @Test
    public void skipLastRecord() throws FileNotFoundException, IOException{
        
    	FastaVisitor visitor = new FastaVisitor() {
			boolean visitedFirst=false;
			boolean visitedLast=false;
			@Override
			public void visitEnd() {
				assertTrue(visitedFirst);
				assertTrue(visitedLast);				
			}
			
			@Override
			public void halted() {
				throw new IllegalStateException("parsing was halted");
			}
			
			@Override
			public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(matchesFirstRecord(id,optionalComment)){
					visitedFirst=true;
					return new FirstRecordVisitor();
				}else if (matchesSecondRecord(id,optionalComment)){
					visitedLast=true;
					return null;
				}
				throw new AssertionError("def line did not match first or second record");
			}
	
			
		};
		FastaFileParser.create(getFastaFile()).parse(visitor);
    }
    
   
    
    @Test
    public void stopAfterFirstDeflineRecords() throws FileNotFoundException, IOException{
        
    	FastaVisitor visitor = new FastaVisitor() {
			boolean visitedFirst=false;
			boolean visitedLast=false;
			@Override
			public void visitEnd() {					
				throw new IllegalStateException("parsing was NOT halted");
			}
			@Override
			public void halted() {
				assertTrue(visitedFirst);
				assertFalse(visitedLast);		
			}
			
			@Override
			public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(matchesFirstRecord(id,optionalComment)){
					visitedFirst=true;
					callback.haltParsing();
					return null;
				}
				throw new AssertionError("def line did not match first");
			}
	
			
		};
		FastaFileParser.create(getFastaFile()).parse(visitor);
    }
    
   
    @Test
    public void stopAfterFirstRecord() throws FileNotFoundException, IOException{
        
    	FastaVisitor visitor = new FastaVisitor() {
			boolean visitedFirst=false;
			boolean visitedLast=false;
			@Override
			public void visitEnd() {					
				throw new IllegalStateException("parsing was NOT halted");
			}
			@Override
			public void halted() {
				assertTrue(visitedFirst);
				assertFalse(visitedLast);		
			}
			
			@Override
			public FastaRecordVisitor visitDefline(final FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(matchesFirstRecord(id,optionalComment)){
					visitedFirst=true;					
					return new FirstRecordVisitor(){

						@Override
						public void visitEnd() {
							super.visitEnd();
							callback.haltParsing();
						}
						
					};
				}
				throw new AssertionError("def line did not match first");
			}
	
			
		};
		FastaFileParser.create(getFastaFile()).parse(visitor);
    }
    
    
    
    
    @Test
    public void stopAfterSecondDefline() throws FileNotFoundException, IOException{
        
    	FastaVisitor visitor = new FastaVisitor() {
			boolean visitedFirst=false;
			boolean visitedLast=false;
			@Override
			public void visitEnd() {					
				throw new IllegalStateException("parsing was NOT halted");
			}
			
			@Override
			public void halted() {
				assertTrue(visitedFirst);
				assertTrue(visitedLast);				
			}
			@Override
			public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(matchesFirstRecord(id,optionalComment)){
					visitedFirst=true;
					return new FirstRecordVisitor();
				}else if (matchesSecondRecord(id,optionalComment)){
					visitedLast=true;
					callback.haltParsing();
					return null;
				}
				throw new AssertionError("def line did not match first or second record");
			}


			
	
			
		};
		FastaFileParser.create(getFastaFile()).parse(visitor);
    }

	

	private File getFastaFile() throws IOException {
		return RESOURCES.getFile("files/seqs.fasta");
	}

    
    

	
	private static class FirstRecordVisitor implements FastaRecordVisitor{
		private final List<String> expectedLines = new ArrayList<String>();
		private final List<String> actualLines = new ArrayList<String>();
		public FirstRecordVisitor(){
			expectedLines.add("CCTCATGTACTCTTACTTTCAATGTTTGGAGGTTGCCCGTAAGCACTTCTTCTTCCCAAT\n");
	        expectedLines.add("AACTGACGACCCACTTGTTCTTTTGAAAGTGAATCCACCAAAGCTGAAAGATGAGCTAAT\n");
	        expectedLines.add("CCTTAAGCCCATTGCTGCCTTGCATATGTCCACAGCTTGTTCCTCAGTTGGATTTTGTCG\n");
	        expectedLines.add("AAGAATGTCTACCATCCTTATTCCCCCAATCTGTGTGCTATGGCACATCTCCAATAAAGA\n");
	        expectedLines.add("TGCTAGTGGGTCTGCTGATACCGTTGCTCTTCTTACTATGTTCCTGGCAGCAATAATCAA\n");
	        expectedLines.add("GCTTTGGTCAACATCATCATTTCTCACCTCCCCTCCTGGAGTATACATTTGCTCCCAGCA\n");
	        expectedLines.add("TGTTCCCTGGGTCAAATGCAGCACTTCAATATAGACACTGCTTGTTCCACCAGCCACTGG\n");
	        expectedLines.add("GAGGAATCTCGTTTTGCGGACCAACTCTCTTTCTAGCATGTATGCAACCATTAAGGGGGC\n");
	        expectedLines.add("AATTTTGCAGTCCTGGAGTTCTTCCTTCTTCTCTTTTGTTATCGTCAGCTGTGATTCCGA\n");
	        expectedLines.add("TGTTAGTATTCTAGCTCCCACTTCATTTGGGAAAACAACTTCCATGATTACATCCTGCGC\n");
	        expectedLines.add("CTCTTTGGCACTGAGGTCTGCGTGGCCAGGGTTTATGTCAACCCTCCGTCTTATTTTAAC\n");
	        expectedLines.add("TTGATTTCTGAAGTGGACAGGGCCAAAGGTCCCGTGTTTCAATCTTTCGACTTTTTCAAA\n");
	        expectedLines.add("ATAAGTTTTATATACCTTTGGGTAGTGAACTGTACTTGTTGTTGGTCCATTCCTATTCCA\n");
	        expectedLines.add("CCATGTTACAGCTAGAGGTGATACCATTACTCGGTCTGAGCCGGCA\n");
		}
		@Override
		public void visitBodyLine(String line) {
			actualLines.add(line);
			
		}

		@Override
		public void visitEnd() {
			assertEquals(expectedLines, actualLines);			
		}
		@Override
		public void halted() {
			throw new IllegalStateException("visitor should not be halted");				
		}
		
	}
	
	private static class SecondRecordVisitor implements FastaRecordVisitor{
		private final List<String> expectedLines = new ArrayList<String>();
		private final List<String> actualLines = new ArrayList<String>();
		public SecondRecordVisitor(){
			 expectedLines.add("ATAAAAGAACTAAGAGATCTAATGTCGCAGTCTCGCACCCGCGAGATACTAACCAAAACC\n");
		        expectedLines.add("ACTGTGGACCACATGGCCATAATCAAAAAATACACATCAGGAAGACAAGAGAAGAACCCC\n");
		        expectedLines.add("GCACTCAGGATGAAGTGGATGATGGCAATGAAATATCCAATTACAGCAGATAAAAGAATA\n");
		        expectedLines.add("ATGGAAATGATTCCCGAAAGGAATGAACAAGGACAAACCCTCTGGAGTAAAACAAACGAT\n");
		        expectedLines.add("GCCGGCTCAGACCGAGTAATGGTATCACCTCTAGCTGTAACATGGTGGAATAGGAATGGA\n");
		        expectedLines.add("CCAACAACAAGTACAGTTCACTACCCAAAGGTATATAAAACTTATTTTGAAAAAGTCGAA\n");
		        expectedLines.add("AGATTGAAACACGGGACCTTTGGCCCTGTCCACTTCAGAAATCAAGTTAAAATAAGACGG\n");
		        expectedLines.add("AGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCCAAAGAGGCGCAGGATGTAATC\n");
		        expectedLines.add("ATGGAAGTTGTTTTCCCAAATGAAGTGGGAGCTAGAATACTAACATCGGAATCACAGCTG\n");
		        expectedLines.add("ACGATAACAAAAGAGAAGAAGGAAGAACTCCAGGACTGCAAAATTGCCCCCTTAATGGTT\n");
		        expectedLines.add("GCATACATGCTAGAAAGAGAGTTGGTCCGCAAAACGAGATTCCTCCCAGTGGCTGGTGGA\n");
		        expectedLines.add("ACAAGCAGTGTCTATATTGAAGTGCTGCATTTGACCCAGGGAACATGCTGGGAGCAAATG\n");
		        expectedLines.add("TATACTCCAGGAGGGGAGGTGAGAAATGATGATGTTGACCAAAGCTTGATTATTGCTGCC\n");
		        expectedLines.add("AGGAACATAGTAAGAAGAGCAACGGTATCAGCAGACCCACTAGCATCTCTATTGGAGATG\n");
		        expectedLines.add("TGCCATAGCACACAGATGGGGGAATAAGGATGGTAGACATTCTTCGACAAAATCCAACTG\n");
		        expectedLines.add("AGGAACAAGCTGTGGACATATGCAAGGCAGCAATGGGCTTAAGGATTAGCTCATCTTTCA\n");
		        expectedLines.add("GCTTTGGTGGATTCACT\n");
		}
		@Override
		public void visitBodyLine(String line) {
			actualLines.add(line);
			
		}

		@Override
		public void visitEnd() {
			assertEquals(expectedLines, actualLines);
			
		}
		@Override
		public void halted() {
			throw new IllegalStateException("visitor should not be halted");				
		}
	}
	
	
    
    @Test
    public void parseEmptyFile() throws IOException{
        FastaVisitor visitor = createMock(FastaVisitor.class);
        visitor.visitEnd();
        
        replay(visitor);
        FastaFileParser.create(new ByteArrayInputStream(new byte[]{})).parse(visitor);
        verify(visitor);
    }
}
