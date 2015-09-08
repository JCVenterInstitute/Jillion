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
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
/**
 * Test to make sure increasing consensus
 * and read lengths work.
 * @author dkatzel
 *
 */
public class TestAceContigBuilderInsertGapsIntoRead {

	String contigId = "contigId";
	Date date = new Date();
	
	@Test
	public void insertGapIntoRead(){
		AceContigBuilder builder = new AceContigBuilder(contigId, "ACGTACGT");
		addRead(builder, "read1", "ACGT", 0);
		addRead(builder, "read2", "ACGT", 4);
		
		builder.getAssembledReadBuilder("read1").insert(4, Nucleotide.Gap);
		
		AceContig actual = builder.build();
		AceContigBuilder expected = new AceContigBuilder(contigId, "ACGTACGT");
		addRead(expected, "read1", "ACGT-", 0);
		addRead(expected, "read2", "ACGT", 4);
		
		assertEquals(expected.build(), actual);
		
	}
	
	@Test
	public void insertGapIntoReadExtendingContig(){
		AceContigBuilder builder = new AceContigBuilder(contigId, "ACGTACGT");
		addRead(builder, "read1", "ACGT", 0);
		addRead(builder, "read2", "ACGT", 4);
		
		builder.getConsensusBuilder().insert(6, Nucleotide.Gap);
		builder.getAssembledReadBuilder("read2").insert(2, Nucleotide.Gap);
		
		AceContig actual = builder.build();
		AceContigBuilder expected = new AceContigBuilder(contigId, "ACGTAC-GT");
		addRead(expected, "read1", "ACGT", 0);
		addRead(expected, "read2", "AC-GT", 4);
		
		assertEquals(expected.build(), actual);
		
	}
	@Test
	public void insertMultipleGapsIntoReadExtendingContig(){
		AceContigBuilder builder = new AceContigBuilder(contigId, "ACGTACGT");
		addRead(builder, "read1", "ACGT", 0);
		addRead(builder, "read2", "ACGT", 4);
		
		builder.getConsensusBuilder().insert(6, "----");
		builder.getAssembledReadBuilder("read2").insert(2, "----");
		
		AceContig actual = builder.build();
		AceContigBuilder expected = new AceContigBuilder(contigId, "ACGTAC----GT");
		addRead(expected, "read1", "ACGT", 0);
		addRead(expected, "read2", "AC----GT", 4);
		
		assertEquals(expected.build(), actual);
		
	}
	
	
	
	private void addRead(AceContigBuilder builder, String id, String seq, int offset){
		builder.addRead(id, new NucleotideSequenceBuilder(seq).build(), offset, Direction.FORWARD, 
				Range.ofLength(seq.length()), new PhdInfo(id, id, date), seq.length());
	}
}
