package org.jcvi.common.core.seq.trace.sff.visitor;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;


public interface SffFileReadVisitor {
	
	void visitFlowgramValues(SffParserCallback<Void> callback, short[] flowgramValues);
	
	void visitNucleotideSequence(SffParserCallback<Void> callback, NucleotideSequence nucleotideSequence);
	
	void visitQualitySequence(SffParserCallback<Void> callback, QualitySequence qualitySequence);
	
	void visitEndSffRead(SffParserCallback<Void> callback);

}
