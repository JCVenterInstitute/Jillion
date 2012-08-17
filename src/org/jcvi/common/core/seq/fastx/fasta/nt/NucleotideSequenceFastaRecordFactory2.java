package org.jcvi.common.core.seq.fastx.fasta.nt;

import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public class NucleotideSequenceFastaRecordFactory2 {

	private NucleotideSequenceFastaRecordFactory2(){
		//can not instantiate
	}
	
	public static NucleotideSequenceFastaRecord create(String id, NucleotideSequence sequence){
		return new UnCommentedNucleotideSequenceFastaRecord(id, sequence);
	}
	public static NucleotideSequenceFastaRecord create(String id, NucleotideSequence sequence, String comment){
		if(comment==null){
			return create(id,sequence);
		}
		return new CommentedNucleotideSequenceFastaRecord(id, sequence,comment);
	}
}
