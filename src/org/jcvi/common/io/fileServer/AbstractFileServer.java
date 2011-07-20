/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jul 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.io.fileServer;

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
