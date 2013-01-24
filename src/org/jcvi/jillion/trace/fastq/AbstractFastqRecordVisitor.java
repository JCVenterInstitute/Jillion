package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public abstract class AbstractFastqRecordVisitor implements FastqRecordVisitor{

	private final String id;
	private final String optionalComment;
	private final FastqQualityCodec qualityCodec;
	
	private NucleotideSequence currentBasecalls;
	private QualitySequence currentQualities;

	
	public AbstractFastqRecordVisitor(String id, String optionalComment,
			FastqQualityCodec qualityCodec) {
		this.id = id;
		this.optionalComment = optionalComment;
		this.qualityCodec = qualityCodec;
	}

	@Override
	public final void visitNucleotides(NucleotideSequence nucleotides) {
		currentBasecalls = nucleotides;
		
	}

	@Override
	public final void visitEncodedQualities(String encodedQualities) {
		currentQualities = qualityCodec.decode(encodedQualities);
		
	}

	@Override
	public final void visitEnd() {
		visitRecord(new FastqRecordBuilder(id, currentBasecalls, currentQualities)
									.comment(optionalComment)
									.build());
		
	}

	protected abstract void visitRecord(FastqRecord record);

}
