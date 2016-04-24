package org.jcvi.jillion.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
/**
 * {@link InputStreamSupplier} that wraps a gzip file.
 * 
 * @author dkatzel
 *
 * @since 5.2
 */
class GZipInputStreamSupplier extends AbstractFileInputStreamSupplier {

    public GZipInputStreamSupplier(File file) {
        super(file);
    }

    @Override
    public InputStream get() throws IOException {
        return new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

}
