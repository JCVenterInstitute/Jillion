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
 * Created on Aug 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.io.fileServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
/**
 * {@code ReadWriteFileServer} is an extension of
 * {@link FileServer} that allows putting
 * files.
 * @author dkatzel
 *
 *
 */
public interface ReadWriteFileServer extends FileServer {

    /**
     * Puts the given File into the FileServer and associates
     * the given id to it.
     * @param fileId the id to associate with the given File.
     * @param fileToPut the File.
     * @throws IOException if there is a problem putting the file.
     * @throws UnsupportedOperationException if the FileServer
     * is ReadOnly.
     */
    void putFile(String fileId, File fileToPut) throws IOException;
    
    /**
     * Puts the given InputStream into the FileServer and associates
     * the given id to it.
     * @param id the id to associate with the given stream.
     * @param inputStream the File.
     * @throws IOException if there is a problem putting the stream.
     * @throws UnsupportedOperationException if the FileServer
     * is ReadOnly.
     */
    void putStream(String id, InputStream inputStream) throws IOException;
    
    /**
     * Creates a new File for the given filepath.
     * @param filePath the path of the file to create.
     * @return a new File.
     * @throws IOException if there is a problem creating the file.
     * @throws UnsupportedOperationException if the implementation
     * does not support creating new files.
     */
    File createNewFile(String filePath) throws IOException;
    /**
     * Creates a new Directory for the given path.
     * @param dirPath the path of the dir to create.
     * @return a new File representing a Directory.
     * @throws IOException if there is a problem creating the file.
     * @throws UnsupportedOperationException if the implementation
     * does not support creating new directories.
     */
    File createNewDir(String dirPath) throws IOException;
    
    File createNewDirIfNeeded(String dirPath) throws IOException;
    
    /**
     * Does this File Server support Symbolic Links?
     * @return {@code true} if symbolic links are 
     * supported; {@code false} otherwise.
     */
    boolean supportsSymlinks();
    /**
     * Create a new symbolic link.
     * @param pathtoFileToLink path to the file to be linked
     * @param symbolicPath path to the link.
     * @throws IOException if there was a problem creating the symbolic link.
     * @throws UnsupportedOperationException if sym links are not supported.
     */
    void createNewSymLink(String pathtoFileToLink, String symbolicPath) throws IOException;
}
