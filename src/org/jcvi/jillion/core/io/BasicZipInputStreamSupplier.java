package org.jcvi.jillion.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
/**
 * {@link InputStreamSupplier} that assumes the wrapped zip file
 * only contains a single entry.
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

}
