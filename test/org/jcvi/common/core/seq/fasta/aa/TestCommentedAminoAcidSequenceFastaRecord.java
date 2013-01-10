package org.jcvi.common.core.seq.fasta.aa;

import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.internal.seq.fasta.aa.CommentedAminoAcidSequenceFastaRecord;

public class TestCommentedAminoAcidSequenceFastaRecord extends AbstractTestAminoAcidSequenceFastaRecord{

	@Override
	protected AminoAcidSequenceFastaRecord createRecord(String id,
			AminoAcidSequence seq, String optionalComment) {
		return new CommentedAminoAcidSequenceFastaRecord(id,seq,optionalComment);
	}

}
