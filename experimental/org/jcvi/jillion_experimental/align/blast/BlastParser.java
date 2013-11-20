package org.jcvi.jillion_experimental.align.blast;

import java.io.IOException;

public interface BlastParser {

	boolean canParse();
	
	void parse(BlastVisitor visitor) throws IOException;
}
