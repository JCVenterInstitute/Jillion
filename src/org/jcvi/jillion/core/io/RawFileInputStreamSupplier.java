/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.jcvi.jillion.core.Range;
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
    
    
    @Override
    public Optional<File> getFile() {
        return Optional.of(file);
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

	@Override
	public InputStream get(Range range) throws IOException {
		 return new RandomAccessFileInputStream(file, range.getBegin(), range.getLength());
	}
    
    

}
