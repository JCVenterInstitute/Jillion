package org.jcvi.jillion.assembly.consed.phd;

import java.util.Map;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

public interface PhdVisitor {

	void visitComments(Map<String,String> comments);
	
	void visitBasecall(Nucleotide base, PhredQuality quality, Integer tracePosition);
	
	PhdReadTagVisitor visitReadTag();
	
	PhdWholeReadItemVisitor visitWholeReadItem();
	
	/**
	 * The phd file has been completely visited.
	 */
	void visitEnd();
	/**
	 * The phd visitation has been halted,
	 * usually by calling {@link PhdBallVisitorCallback#haltParsing()}.
	 */
	void halted();
}
