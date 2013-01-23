package org.jcvi.jillion.trace.sff;

public interface SffFileParserCallback {

	interface SffFileMomento{
		
	}
	
	boolean momentoSupported();
	
	SffFileMomento createMomento();
	
	void stopParsing();
}
