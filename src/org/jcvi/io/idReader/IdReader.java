/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

import java.io.Closeable;
import java.util.Iterator;

public interface IdReader<T> extends Closeable, Iterable<T>{

    Iterator<T> getIds() throws IdReaderException;
}
