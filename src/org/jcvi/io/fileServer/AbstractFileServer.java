/*
 * Created on Jul 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.fileServer;

import java.io.IOException;

public abstract class AbstractFileServer implements FileServer {

    private boolean closed = false;
    
    @Override
    public synchronized void close() throws IOException {
        closed = true;
    }

    /**
     * Checks to see if this FileServer
     * is not closed.
     * @throws IllegalStateException if {@link #isClosed().
     */
    protected void verifyNotClosed(){
        if(isClosed()){
            throw new IllegalStateException("DirectoryFileServer is closed");
        }
    }
    public synchronized boolean isClosed() {
        return closed;
    }

}
