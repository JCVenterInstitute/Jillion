package org.jcvi.jillion_experimental.align;

import java.io.IOException;

import org.jcvi.jillion_experimental.align.AlnVisitor.AlnVisitorCallback.AlnVisitorMemento;

public interface AlnParser {

	boolean canParse();
	
	void parse(AlnVisitor visitor) throws IOException;
	
	void parse(AlnVisitor visitor, AlnVisitorMemento memento) throws IOException;
}
