/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

public interface IdParser<T> {

    T parseIdFrom(String string);
    boolean isValidId(String string);
}
