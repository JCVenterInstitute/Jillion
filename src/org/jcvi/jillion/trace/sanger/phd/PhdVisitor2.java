package org.jcvi.jillion.trace.sanger.phd;

import java.util.Map;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

public interface PhdVisitor2 {

	void visitComment(Map<String,String> comments);
	
	void visitBasecall(Nucleotide base, PhredQuality quality);
	
	void visitBasecall(Nucleotide base, PhredQuality quality, int tracePosition);
	
	PhdReadTagVisitor2 visitReadTag();
	
	PhdWholeReadItemVisitor visitWholeReadItem();
	
	void visitEnd();
	

	void halted();
}
