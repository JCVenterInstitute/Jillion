package org.jcvi.jillion.vcf;

import java.io.IOException;

import org.jcvi.jillion.vcf.VcfVisitor.VcfMemento;

public interface VcfParser {

	void parse(VcfVisitor visitor) throws IOException;
	
	void parse(VcfVisitor visitor, VcfMemento momento) throws IOException;
}
