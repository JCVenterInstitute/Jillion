/*
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.Closeable;
import java.io.File;
import java.util.List;

public interface CasIdLookup extends Closeable{

    
    String getLookupIdFor(long casReadId);
    List<File> getFiles();
    int getNumberOfIds();
    File getFileFor(String lookupId);
    File getFileFor(long casReadId);
    
}
