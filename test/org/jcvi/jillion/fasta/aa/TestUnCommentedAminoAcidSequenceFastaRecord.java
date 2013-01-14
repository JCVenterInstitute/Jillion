package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.jillion.internal.fasta.aa.UnCommentedAminoAcidSequenceFastaRecord;
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
