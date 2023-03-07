/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
import java.util.function.Predicate;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.jcvi.jillion.core.io.InputStreamSupplier.InputStreamReadOptions;
/**
 * {@link InputStreamSupplier} that assumes the wrapped supports zip files.  Added support for multiple entries in {@code 6.0}.
 * 
 * @author dkatzel
 * 
 * @since 5.2
 *
 */
class BasicZipInputStreamSupplier extends AbstractFileInputStreamSupplier {

    public BasicZipInputStreamSupplier(File file) {
        super(file);
    }

    @Override
    public InputStream get() throws IOException {
        ZipInputStream in =new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
        //assume first record is the entry we care about?
        in.getNextEntry();
        return in;
    }
    
    @Override
	public InputStream get(InputStreamReadOptions readOptions) throws IOException {
		//check if we have to do entry checking
		if(readOptions.getEntryNamePredicate() !=null) {
			ZipArchiveInputStream in = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(file)));
			Predicate<String> predicate = readOptions.getEntryNamePredicate();
			return InputStreamSupplierUtil.getInputStreamForFirstEntryThatMatches(in, predicate);
		}
		return super.get(readOptions);
	}

}
