/*
 * Created on Jan 7, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.File;
import java.util.List;

import org.jcvi.assembly.Assembly;
import org.jcvi.datastore.DataStore;
import org.jcvi.trace.sanger.phd.Phd;

public interface AceAssembly<A extends AceContig> extends Assembly<A, DataStore<A>>{

    AceTagMap getAceTagMap();
    DataStore<Phd> getPhdDataStore();
    List<File> getPhdFiles();
}
