/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit;

public interface Edit<O,E> {

    E performEdit(O original) throws EditException;
}
