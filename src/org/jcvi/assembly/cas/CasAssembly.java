/*
 * Created on Jan 7, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.File;
import java.util.List;

import org.jcvi.assembly.Assembly;
import org.jcvi.datastore.DataStore;

public interface CasAssembly extends Assembly<CasContig, DataStore<CasContig>>{

    List<File> getReferenceFiles();
    CasIdLookup getReferenceIdLookup();
    CasIdLookup getReadIdLookup();
}
