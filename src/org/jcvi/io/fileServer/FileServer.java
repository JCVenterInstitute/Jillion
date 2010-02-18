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
