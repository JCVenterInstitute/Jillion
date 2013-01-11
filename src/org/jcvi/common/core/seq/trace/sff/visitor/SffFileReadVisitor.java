package org.jcvi.common.core.seq.trace.sff.visitor;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;


public interface SffFileReadVisitor {
	
	void visitFlowgramValues(SffParserCallback<Void> callback, short[] flowgramValues);
	
	void visitNucleotideSequence(SffParserCallback<Void> callback, NucleotideSequence nucleotideSequence);
	
	void visitQualitySequence(SffParserCallback<Void> callback, QualitySequence qualitySequence);
	
	void visitEndSffRead(SffParserCallback<Void> callback);

}
