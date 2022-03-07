package org.jcvi.jillion.vcf;

import java.io.IOException;

public interface VcfParser {

	void parse(VcfVisitor visitor) throws IOException;
}
