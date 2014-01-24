package org.jcvi.jillion.sam;

import java.io.IOException;

public interface SamParser {

	boolean canAccept();
	
	void accept(SamVisitor visitor) throws IOException;
}
