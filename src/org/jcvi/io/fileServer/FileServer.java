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
package org.jcvi.io.fileServer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
/**
 * {@code FileServer} is an interface for getting
 * Files from a remote location.
 * @author dkatzel
 *
 */
public interface FileServer extends Closeable{

    /**
     * Get the {@link File} associated with the given id.
     * @param fileId the id associated with the File.
     * @return a {@link File}.
     * @throws IOException if there is a problem fetching the file
     *  or the file does not exist.
     * @throws UnsupportedOperationException if the FileServer
     * does not support returning {@link File}s.
     */
    File getFile(String fileId) throws IOException;
    
    /**
     * Get the {@link File} associated with the given id as an {@link InputStream}.
     * @param fileId the id associated with the File.
     * @return an {@link InputStream}.
     * @throws IOException if there is a problem fetching the stream
     *  or the file does not exist.
     */
    InputStream getFileAsStream(String fileId) throws IOException;
    /**
     * Does this FileServer support {@link #getFile(String)}.
     * @return {@code true} if it does; {@code false}
     * if that method will throw an {@link UnsupportedOperationException}.
     */
    boolean supportsGettingFileObjects();
    /**
     * Does the FileServer contain a File with the given id.
     * @param id the file id to check.
     * @return {@code true} if there is a file with that id; {@code false} 
     * otherwise.
     * @throws IOException if there is a problem checking the database.
     */
    boolean contains(String id) throws IOException;
}
