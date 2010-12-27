package org.jcvi.io;

import java.io.Closeable;
import java.util.Iterator;

/*
 * {@code ObjectIO} is the base interface for reading and writing
 * any type of object, from and to any input or output stream, including
 * files or databases. 
 * 
 * @author naxelrod
 */
 
public interface ObjectIO<T> extends Iterable<T>, Closeable {

		// Reader
		Iterator<T> iterator();
		
		// Writer
		public boolean write( T object );
		public int write( Iterable<T> object );
		public int write();

}
