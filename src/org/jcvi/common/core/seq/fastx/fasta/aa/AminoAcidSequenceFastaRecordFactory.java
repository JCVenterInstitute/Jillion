package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

public final class AminoAcidSequenceFastaRecordFactory {

	private AminoAcidSequenceFastaRecordFactory(){
		//can not instantiate
	}
	
	public static AminoAcidSequenceFastaRecord create(String id, AminoAcidSequence sequence, String optionalComment){
		if(optionalComment==null){
			return create(id,sequence);
		}
		return new CommentedAminoAcidSequenceFastaRecord(id, sequence,optionalComment);
	}
	
	public static AminoAcidSequenceFastaRecord create(String id, AminoAcidSequence sequence){
		return new UnCommentedAminoAcidSequenceFastaRecord(id, sequence);
	}
}
