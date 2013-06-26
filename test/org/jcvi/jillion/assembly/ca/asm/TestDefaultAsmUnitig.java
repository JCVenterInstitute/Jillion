/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
package org.jcvi.jillion.assembly.ca.asm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

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
		assertIteratorMatches(Arrays.asList(read, unitig.getRead("read2")), unitig.getReadIterator());
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
