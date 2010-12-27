package org.jcvi.seqmodel.io;

import java.util.Iterator;

import org.jcvi.io.ObjectIO;
import org.jcvi.seqmodel.SequenceI;

/*
 * {@code SequenceIO} is the base interface for reading and writing {@link SequenceI} objects.
 * 
 * @author naxelrod
 */

public interface SequenceIO<T extends SequenceI> extends ObjectIO<T> {

	// Reader
	Iterator<T> iterator();
	
	// Writer
	public boolean write( T sequence );
	public int write( Iterable<T> sequences );
	public int write();

}
