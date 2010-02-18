/*
 * Created on Jul 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.trace.TraceDataStore;
import org.jcvi.util.FileIterator;

public class TraceFileServerDataStore<T extends SangerTrace> implements TraceDataStore<T> {

    private final DirectoryFileServer fileServer;
    private final SangerTraceCodec traceParser;
    
    /**
     * @param fileServer
     * @param traceParser
     */
    public TraceFileServerDataStore(DirectoryFileServer fileServer,
            SangerTraceCodec traceParser) {
        this.fileServer = fileServer;
        this.traceParser = traceParser;
    }

    @Override
    public boolean contains(String id) throws DataStoreException{
        try {
            return fileServer.contains(id);
        } catch (IOException e) {
            throw new DataStoreException("error checking file server", e);
        }
    }

    @Override
    public T get(String id) throws DataStoreException {
        try {
            return (T)traceParser.decode(fileServer.getFileAsStream(id));
        } catch (Exception e) {
            throw new DataStoreException("error getting file from fileServer", e);
        }
    }

    @Override
    public int size() throws DataStoreException {
        throw new UnsupportedOperationException("number of traces not supported by file servers yet");
    }

    @Override
    public Iterator<T> iterator() {
        return new DataStoreIterator<T>(this);
    }

    @Override
    public void close() throws IOException {
        fileServer.close();
        
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return new FileNameIterator();
        
    }

    private class FileNameIterator implements Iterator<String>{
        Iterator<File> iter = FileIterator.createFileIterator(fileServer.getRootDir(), false);
        private final int rootPathLength =fileServer.getRootDir().getAbsolutePath().length();
        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public String next() {
            
           return iter.next().getAbsolutePath().substring(rootPathLength);
        }

        @Override
        public void remove() {
            iter.remove();
            
        }
        
    }
}
