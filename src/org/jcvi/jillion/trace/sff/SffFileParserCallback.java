package org.jcvi.jillion.trace.sff;

public interface SffFileParserCallback {

	interface SffFileMemento{
		
	}
	
	boolean mementoSupported();
	
	SffFileMemento createMemento();
	
	void stopParsing();
}
