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
package org.jcvi.jillion.fasta.nt;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.easymock.EasyMockSupport;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;


public class TestParseNonRedundantFastaFile extends EasyMockSupport{

	private ResourceHelper resources = new ResourceHelper(TestParseNonRedundantFastaFile.class);
	
	private FastaParser parser;
	
	@Before
	public void setup() throws IOException{
		parser = FastaFileParser.create(getFastaFile());
	}
	@Test
	public void visitRedundantDataXTimes() throws IOException{
		FastaVisitor visitor = createMockVisitorThatVisits(3);
		
		replayAll();
		
		parser.parse(visitor);
		
		
		
		verifyAll();
	}

	protected File getFastaFile() throws IOException {
		return resources.getFile("files/nonRedundantNucleotide.fasta.nr");
	}
	
	@Test
	public void createMementoForRecordAfterNonRedundantOne() throws IOException{
		
		FastaVisitorMemento memento = getMementoFor("Blah", parser);
		
		FastaVisitor visitor = createMockVisitorThatVisits(0);
		
		replayAll();
		
		parser.parse(visitor, memento);
		
		verifyAll();
	}
	
	@Test
	public void createMementoAtBeginningOfRedundantOneShouldVisitAllRecords() throws IOException{
		
		FastaVisitorMemento memento = getMementoFor("gi|3023276|sp|Q57293|AFUC_ACTPL", parser);
		
		FastaVisitor visitor = createMockVisitorThatVisits(3);
		
		replayAll();
		
		parser.parse(visitor, memento);
		
		verifyAll();
	}
	@Test
	public void createMementoAtSecondRedundantOneShouldSkipFirstRecords() throws IOException{
		
		FastaVisitorMemento memento = getMementoFor("gi|1469284|gb|AAB05030.1|", parser);
		
		FastaVisitor visitor = createMockVisitorThatVisits(2);
		
		replayAll();
		
		parser.parse(visitor, memento);
		
		verifyAll();
	}
	
	@Test
	public void createMementoAtLastRedundantOneShouldOnlyVisitLastRedundantRecord() throws IOException{
		
		FastaVisitorMemento memento = getMementoFor("gi|1477453|gb|AAB17216.1|", parser);
		
		FastaVisitor visitor = createMockVisitorThatVisits(1);
		
		replayAll();
		
		parser.parse(visitor, memento);
		
		verifyAll();
	}

	private FastaVisitorMemento getMementoFor(final String idToGetMementoFor, FastaParser parser) throws IOException {
		assertTrue(parser.canCreateMemento());
		
		final FastaVisitorMemento[] mementos = new FastaVisitorMemento[1];
		parser.parse(new FastaVisitor() {
			
			@Override
			public void visitEnd() {
				
			}
			
			@Override
			public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(idToGetMementoFor.equals(id)	){
					mementos[0]=callback.createMemento();
					callback.haltParsing();
				}
				return null;
			}
			
			@Override
			public void halted() {
				
			}
		});
		return mementos[0];
	}

	protected FastaVisitor createMockVisitorThatVisits(int times) {
		FastaVisitor visitor = createMock(FastaVisitor.class);
		if(times >0){
			FastaRecordVisitor redundantVisitor = createRedundantVisitor(times);
			if(times ==3){
			expect(visitor.visitDefline(isA(FastaVisitorCallback.class), eq("gi|3023276|sp|Q57293|AFUC_ACTPL"), eq("Ferric transport ATP-binding protein afuC")))
					.andReturn(redundantVisitor);
			}
			if(times >=2){
			expect(visitor.visitDefline(isA(FastaVisitorCallback.class), eq("gi|1469284|gb|AAB05030.1|"), eq("afuC gene product")))
			.andReturn(redundantVisitor);
			}
			if(times >=1){
			expect(visitor.visitDefline(isA(FastaVisitorCallback.class), eq("gi|1477453|gb|AAB17216.1|"), eq("afuC [Actinobacillus pleuropneumoniae]")))
			.andReturn(redundantVisitor);
		}
		}
		expect(visitor.visitDefline(isA(FastaVisitorCallback.class), eq("Blah"), isNull(String.class))).andReturn(createBlahVisitor());
		
		visitor.visitEnd();
		return visitor;
	}

	private FastaRecordVisitor createBlahVisitor() {
		FastaRecordVisitor visitor = createMock(FastaRecordVisitor.class);
		visitor.visitBodyLine("ACGTACGTACGTACGT\n");
		visitor.visitEnd();
		return visitor;
		
	}
	
	private FastaRecordVisitor createRedundantVisitor(int timesVisited) {
		FastaRecordVisitor visitor = createMock(FastaRecordVisitor.class);
		
		visitor.visitBodyLine("ATGAACAACGATTTTCTGGTGCTGAAAAACATTACCAAAAGCTTTGGCAAAGCGACCGTG\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("ATTGATAACCTGGATCTGGTGATTAAACGCGGCACCATGGTGACCCTGCTGGGCCCGAGC\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("GGCTGCGGCAAAACCACCGTGCTGCGCCTGGTGGCGGGCCTGGAAAACCCGACCAGCGGC\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("CAGATTTTTATTGATGGCGAAGATGTGACCAAAAGCAGCATTCAGAACCGCGATATTTGC\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("ATTGTGTTTCAGAGCTATGCGCTGTTTCCGCATATGAGCATTGGCGATAACGTGGGCTAT\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("GGCCTGCGCATGCAGGGCGTGAGCAACGAAGAACGCAAACAGCGCGTGAAAGAAGCGCTG\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("GAACTGGTGGATCTGGCGGGCTTTGCGGATCGCTTTGTGGATCAGATTAGCGGCGGCCAG\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("CAGCAGCGCGTGGCGCTGGCGCGCGCGCTGGTGCTGAAACCGAAAGTGCTGATTCTGGAT\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("GAACCGCTGAGCAACCTGGATGCGAACCTGCGCCGCAGCATGCGCGAAAAAATTCGCGAA\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("CTGCAGCAGCGCCTGGGCATTACCAGCCTGTATGTGACCCATGATCAGACCGAAGCGTTT\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("GCGGTGAGCGATGAAGTGATTGTGATGAACAAAGGCACCATTATGCAGAAAGCGCGCCAG\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("AAAATTTTTATTTATGATCGCATTCTGTATAGCCTGCGCAACTTTATGGGCGAAAGCACC\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("ATTTGCGATGGCAACCTGAACCAGGGCACCGTGAGCATTGGCGATTATCGCTTTCCGCTG\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("CATAACGCGGCGGATTTTAGCGTGGCGGATGGCGCGTGCCTGGTGGGCGTGCGCCCGGAA\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("GCGATTCGCCTGACCGCGACCGGCGAAACCAGCCAGCGCTGCCAGATTAAAAGCGCGGTG\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("TATATGGGCAACCATTGGGAAATTGTGGCGAACTGGAACGGCAAAGATGTGCTGATTAAC\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("GCGAACCCGGATCAGTTTGATCCGGATGCGACCAAAGCGTTTATTCATTTTACCGAACAG\n"); expectLastCall().times(timesVisited);
		visitor.visitBodyLine("GGCATTTTTCTGCTGAACAAAGAA\n"); expectLastCall().times(timesVisited);
		
		visitor.visitEnd();
		expectLastCall().times(timesVisited);
		
		return visitor;
	}
}
