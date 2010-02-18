/*
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.trace.TraceDecoderException;

public class DefaultFolderTraceArchiveDataStore extends AbstractFolderTraceArchiveDataStore{

    public DefaultFolderTraceArchiveDataStore(String rootDirPath,
            TraceArchiveInfo traceArchiveInfo) {
        super(rootDirPath, traceArchiveInfo);
    }

    @Override
    public TraceArchiveTrace get(String id) throws DataStoreException {
        return createTraceArchiveTrace(id);
    }
    @Override
    public void close() throws IOException {
        //no-op
        
    }
    protected TraceArchiveTrace createTraceArchiveTrace(String id)
                                        throws DataStoreException {
      return new DefaultTraceArchiveTrace(getTraceArchiveInfo().get(id),getRootDirPath());
      
    }

    @Override
    public String toString() {
        return "Trace archive for folder " + getRootDirPath();
    }

    
    
    
}
