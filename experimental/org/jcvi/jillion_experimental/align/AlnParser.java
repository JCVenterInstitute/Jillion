package org.jcvi.jillion_experimental.align;

import java.io.IOException;

import org.jcvi.jillion_experimental.align.AlnVisitor2.AlnVisitorCallback.AlnVisitorMemento;

public interface AlnParser {

	boolean canParse();
	
	void parse(AlnVisitor2 visitor) throws IOException;
	
	void parse(AlnVisitor2 visitor, AlnVisitorMemento memento) throws IOException;
}
