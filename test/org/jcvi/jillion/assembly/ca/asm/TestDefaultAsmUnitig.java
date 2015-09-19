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
package org.jcvi.jillion.assembly.ca.asm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Test;

public class TestDefaultAsmUnitig {
	private String unitigId = "id";
	private NucleotideSequence consensus = new NucleotideSequenceBuilder("ACGTACGT").build();
	@Test
	public void noReads(){
		AsmUnitig unitig = DefaultAsmUnitig.createBuilder(unitigId, consensus)
										.build();
		
		assertEquals(unitigId, unitig.getId());
		assertEquals(0, unitig.getNumberOfReads());
		assertFalse(unitig.containsRead("unassembledRead"));
		assertEquals(consensus , unitig.getConsensusSequence());
		assertFalse(unitig.getReadIterator().hasNext());
	}
	
	@Test
	public void oneRead(){
		AsmUnitig unitig = DefaultAsmUnitig.createBuilder(unitigId, consensus)
				.addRead("read1", "ACGTACGT", 0, Direction.FORWARD, Range.of(1,8), 8, false)
				.build();

		assertEquals(unitigId, unitig.getId());
		assertEquals(1, unitig.getNumberOfReads());
		assertEquals(consensus , unitig.getConsensusSequence());
		assertTrue(unitig.containsRead("read1"));
		AsmAssembledRead read =unitig.getRead("read1");
		assertEquals("ACGTACGT", read.getNucleotideSequence().toString());
		assertIteratorMatches(Arrays.asList(read), unitig.getReadIterator());
	}
	
	@Test
	public void twoReads(){
		AsmUnitig unitig = DefaultAsmUnitig.createBuilder(unitigId, consensus)
				.addRead("read1", "ACGTACGT", 0, Direction.FORWARD, Range.of(1,8), 8, false)
				.addRead("read2", "ACGT", 4, Direction.REVERSE, Range.of(5,8), 8, false)
				.build();

		assertEquals(unitigId, unitig.getId());
		assertEquals(2, unitig.getNumberOfReads());
		assertEquals(consensus , unitig.getConsensusSequence());
		
		AsmAssembledRead read =unitig.getRead("read1");
		assertEquals("ACGTACGT", read.getNucleotideSequence().toString());
		assertEquals(toMap(Arrays.asList(read, unitig.getRead("read2")).iterator()), toMap(unitig.getReadIterator()));
	//	assertIteratorMatches(Arrays.asList(read, unitig.getRead("read2")), unitig.getReadIterator());
	}
	
	
	
	private <T extends AssembledRead>	Map<String, T> toMap(Iterator<T> iterator){
		Map<String, T> map = new HashMap<String, T>();
		iterator.forEachRemaining(read -> map.put(read.getId(), read));
		return map;
	}
	private <T> void  assertIteratorMatches(Iterable<T> expected, StreamingIterator<T> actual){
		try{
			Iterator<T> expectedIter = expected.iterator();
			
			
			while(expectedIter.hasNext()){
				assertTrue(actual.hasNext());
				assertEquals(expectedIter.next(), actual.next());
			}
			assertFalse(actual.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(actual);
		}
	}
}
