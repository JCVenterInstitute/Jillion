package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.internal.seq.fasta.aa.CommentedAminoAcidSequenceFastaRecord;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecord;

public class TestCommentedAminoAcidSequenceFastaRecord extends AbstractTestAminoAcidSequenceFastaRecord{

	@Override
	protected AminoAcidSequenceFastaRecord createRecord(String id,
			AminoAcidSequence seq, String optionalComment) {
		return new CommentedAminoAcidSequenceFastaRecord(id,seq,optionalComment);
	}

}
