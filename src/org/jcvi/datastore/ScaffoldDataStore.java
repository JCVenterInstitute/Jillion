package org.jcvi.datastore;

import org.jcvi.assembly.Scaffold;

import java.util.Iterator;
import java.util.Set;

/**
 * User: aresnick
 * Date: Sep 9, 2009
 * Time: 2:55:11 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public interface ScaffoldDataStore<T extends Scaffold> {
    Set<String> getScaffoldIds();
    Iterator<T> getScaffolds();
    Scaffold getScaffold(String id);
}
