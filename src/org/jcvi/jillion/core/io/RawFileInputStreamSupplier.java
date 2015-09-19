package org.jcvi.jillion.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
/**
 * An {@link InputStreamSupplier}
 * that creates {@link InputStream}s from 
 * normal files (not compressed etc).
 * 
 * 
 * @author dkatzel
 *
 */
class RawFileInputStreamSupplier implements InputStreamSupplier {
    private final File file;
    
    RawFileInputStreamSupplier(File file){
        //assume since this class is package private
        //that the file is not null, exists and is readable.
        this.file = file;
    }
    
    @Override
    public InputStream get() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }
    /**
     * Uses {@link RandomAccessFileInputStream} to start
     * the stream directly from the start offset without
     * having to read/skip all the beginning bytes.
     */
    @Override
    public InputStream get(long startOffset) throws IOException{
        return new RandomAccessFileInputStream(file, startOffset);
    }

}
