package org.jcvi.common.core.seq.fastx.fastq;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public final class FastqRecordFactory {

	private FastqRecordFactory(){
		//can not instantiate
	}
	public static FastqRecord create(String id, NucleotideSequence basecalls, QualitySequence qualities){
		return new UncommentedFastqRecord(id, basecalls, qualities);
	}
	public static FastqRecord create(String id, NucleotideSequence basecalls, QualitySequence qualities, String comments){
		if(comments ==null){
			create(id,basecalls, qualities);
		}
		return new CommentedFastqRecord(id, basecalls, qualities,comments);
	}
}
