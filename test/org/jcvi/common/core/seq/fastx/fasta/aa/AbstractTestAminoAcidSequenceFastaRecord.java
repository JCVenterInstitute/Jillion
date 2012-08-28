package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestAminoAcidSequenceFastaRecord {

	private final AminoAcidSequence seq = new AminoAcidSequenceBuilder("ILMFTWVC").build();
	private final String id = "id";
	protected final AminoAcidSequenceFastaRecord sut;
	private final String comments = "a comment";
	public AbstractTestAminoAcidSequenceFastaRecord(){
		sut = createRecord(id, seq,comments);
	}
	protected abstract AminoAcidSequenceFastaRecord createRecord(String id, AminoAcidSequence seq, String optionalComment);
	
	@Test
	public void getters(){
		assertEquals(id, sut.getId());
		assertEquals(seq, sut.getSequence());
	}
	
	@Test
	public void sameReferenceShouldBeEqual(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void sameValuesShouldBeEqual(){
		AminoAcidSequenceFastaRecord same = createRecord(id, seq,comments);
		TestUtil.assertEqualAndHashcodeSame(sut, same);
	}
	/**
	 * Comments don't take part in equals/hashcode computation
	 */
	@Test
	public void differentCommentsValuesShouldBeEqual(){
		AminoAcidSequenceFastaRecord same = createRecord(id, seq,"different"+comments);
		TestUtil.assertEqualAndHashcodeSame(sut, same);
	}
	@Test
	public void differentIdShouldNotBeEqual(){
		AminoAcidSequenceFastaRecord different = createRecord("different"+id, seq,comments);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	@Test
	public void differentSeqShouldNotBeEqual(){
		AminoAcidSequenceFastaRecord different = createRecord(id, new AminoAcidSequenceBuilder(seq)
																	.append(AminoAcid.Histidine)
																	.build()
																	,comments);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
}
