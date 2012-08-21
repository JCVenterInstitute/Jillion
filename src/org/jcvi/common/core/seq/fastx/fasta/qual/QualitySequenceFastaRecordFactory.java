package org.jcvi.common.core.seq.fastx.fasta.qual;

import org.jcvi.common.core.symbol.qual.QualitySequence;

public final class QualitySequenceFastaRecordFactory {

	private QualitySequenceFastaRecordFactory(){
		//can not instantiate
	}
	
	public static QualitySequenceFastaRecord create(String id, QualitySequence sequence){
		return new UncommentedQualitySequenceFastaRecord(id, sequence);
	}
	public static QualitySequenceFastaRecord create(String id, QualitySequence sequence, String comment){
		if(comment==null){
			return create(id,sequence);
		}
		return new CommentedQualitySequenceFastaRecord(id, sequence, comment);
	}
}
