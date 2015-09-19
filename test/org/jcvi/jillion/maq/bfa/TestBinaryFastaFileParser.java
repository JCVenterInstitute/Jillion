/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.maq.bfa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.nt.AbstractNucleotideFastaRecordVisitor;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.maq.bfa.BfaParser;
import org.junit.Test;

public class TestBinaryFastaFileParser extends AbstractTestBinaryFastaFile {

	

	@Test
	public void parseBfa() throws IOException{
		File bfa =getHelper().getFile("seqs.bfa");
		FastaVisitorSpy spy = new FastaVisitorSpy();
		
		BfaParser.create(bfa, ByteOrder.LITTLE_ENDIAN)
								.parse(spy);
		
		assertTrue(spy.visitedEnd());
		assertFalse(spy.wasHalted());
		
		Map<String, NucleotideFastaRecord> map = spy.getRecordMap();
		assertEquals(2, map.size());
		
		assertEquals(forward, map.get(forward.getId()));
		assertEquals(reverse, map.get(reverse.getId()));
		List<NucleotideFastaRecord> orderedList = new ArrayList<NucleotideFastaRecord>(map.values());
		
		assertEquals(Arrays.asList(forward, reverse), orderedList);
		
	}
	
	private static final class FastaVisitorSpy implements FastaVisitor{

		private final Map<String,NucleotideFastaRecord> map = new LinkedHashMap<String,NucleotideFastaRecord>();
		private boolean visitEnd=false;
		private boolean halted = false;
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				String id, String optionalComment) {
			return new AbstractNucleotideFastaRecordVisitor(id, optionalComment) {
				
				@Override
				protected void visitRecord(NucleotideFastaRecord fastaRecord) {
					
						map.put(fastaRecord.getId(), fastaRecord);
					
					
				}
			};
		}

		@Override
		public void visitEnd() {
			visitEnd =true;
			
		}

		@Override
		public void halted() {
			halted =true;
			
		}

		public Map<String, NucleotideFastaRecord> getRecordMap() {
			return map;
		}

		public boolean visitedEnd() {
			return visitEnd;
		}

		public boolean wasHalted() {
			return halted;
		}
		
		
		
	}
}
