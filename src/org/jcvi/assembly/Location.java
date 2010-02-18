/*
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

public interface Location<T> {

    T getSource();
    int getIndex();
}
