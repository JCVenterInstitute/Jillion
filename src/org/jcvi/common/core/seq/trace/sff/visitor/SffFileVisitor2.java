package org.jcvi.common.core.seq.trace.sff.visitor;

import org.jcvi.jillion.trace.sff.SffCommonHeader;
import org.jcvi.jillion.trace.sff.SffReadHeader;

public interface SffFileVisitor2{

	void  visitCommonHeader(SffParserCallback<Void> callback, SffCommonHeader commonHeader);

	SffFileReadVisitor visitRead(SffParserCallback<SffFileReadVisitor> callback,SffReadHeader readHeader);
    
	void visitEndSffFile(SffParserCallback<Void> callback);
}
