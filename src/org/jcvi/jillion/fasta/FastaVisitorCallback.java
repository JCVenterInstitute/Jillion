package org.jcvi.jillion.fasta;

public interface FastaVisitorCallback {

	interface Memento{
		
	}
	
	boolean canCreateMemento();
	
	Memento createMemento();
	
	void stopParsing();
}
