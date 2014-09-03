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
