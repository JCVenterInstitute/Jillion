package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.jcvi.common.core.seq.fastx.fasta.aa.impl.UnCommentedAminoAcidSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestUnCommentedAminoAcidSequenceFastaRecord extends AbstractTestAminoAcidSequenceFastaRecord{

	@Override
	protected AminoAcidSequenceFastaRecord createRecord(String id,
			AminoAcidSequence seq, String optionalComment) {
		return new UnCommentedAminoAcidSequenceFastaRecord(id,seq);
	}

	@Test
	public void commentsShouldAlwaysReturnNull(){
		assertNull(sut.getComment());
	}
}
