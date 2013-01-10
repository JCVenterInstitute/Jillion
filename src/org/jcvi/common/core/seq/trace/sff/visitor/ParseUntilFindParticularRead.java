package org.jcvi.common.core.seq.trace.sff.visitor;

import org.jcvi.common.core.seq.trace.sff.SffCommonHeader;
import org.jcvi.common.core.seq.trace.sff.SffReadHeader;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public class ParseUntilFindParticularRead implements SffFileVisitor2{
	private final String id;

	
	
	public ParseUntilFindParticularRead(String id) {
		this.id = id;
	}

	@Override
	public void visitCommonHeader(SffParserCallback<Void> callback,
			SffCommonHeader commonHeader) {
		//no-op
		
	}

	@Override
	public SffFileReadVisitor visitRead(
			SffParserCallback<SffFileReadVisitor> callback,
			SffReadHeader readHeader) {
		String currentId = readHeader.getId();
		if(currentId.equals(id)){
			return new SffFileReadVisitor() {
				
				@Override
				public void visitQualitySequence(SffParserCallback<Void> callback,
						QualitySequence qualitySequence) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void visitNucleotideSequence(SffParserCallback<Void> callback,
						NucleotideSequence nucleotideSequence) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void visitFlowgramValues(SffParserCallback<Void> callback,
						short[] flowgramValues) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void visitEndSffRead(SffParserCallback<Void> callback) {
					callback.stopParsing();
					
				}
			};
		}
		//skip
		return null;
	}

	@Override
	public void visitEndSffFile(SffParserCallback<Void> callback) {
		//no-op
		
	}
	
	
}
