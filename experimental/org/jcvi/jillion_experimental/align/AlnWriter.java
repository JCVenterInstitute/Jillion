package org.jcvi.jillion_experimental.align;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.Residue;

public interface AlnWriter<R extends Residue, S extends Sequence<R>> extends Closeable {

	void write(String id, S sequence) throws IOException;
	
}
