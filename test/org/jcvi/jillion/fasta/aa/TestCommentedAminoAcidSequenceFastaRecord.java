package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.jillion.internal.fasta.aa.CommentedAminoAcidSequenceFastaRecord;

public class TestCommentedAminoAcidSequenceFastaRecord extends AbstractTestAminoAcidSequenceFastaRecord{

	@Override
	protected AminoAcidSequenceFastaRecord createRecord(String id,
			AminoAcidSequence seq, String optionalComment) {
		return new CommentedAminoAcidSequenceFastaRecord(id,seq,optionalComment);
	}

}
