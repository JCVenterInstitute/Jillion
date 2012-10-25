package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.jcvi.common.core.seq.fastx.fasta.aa.impl.CommentedAminoAcidSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

public class TestCommentedAminoAcidSequenceFastaRecord extends AbstractTestAminoAcidSequenceFastaRecord{

	@Override
	protected AminoAcidSequenceFastaRecord createRecord(String id,
			AminoAcidSequence seq, String optionalComment) {
		return new CommentedAminoAcidSequenceFastaRecord(id,seq,optionalComment);
	}

}
