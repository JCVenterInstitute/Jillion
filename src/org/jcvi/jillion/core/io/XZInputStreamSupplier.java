package org.jcvi.jillion.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.tukaani.xz.XZInputStream;
/**
 * {@link InputStreamSupplier} that wraps a XZ file.
 * 
 * @author dkatzel
 *
 * @since 6.0
 */
public class XZInputStreamSupplier extends AbstractFileInputStreamSupplier {

    public XZInputStreamSupplier(File file) {
        super(file);
    }

    @Override
    public InputStream get() throws IOException {
        return new XZInputStream(new BufferedInputStream(new FileInputStream(file)));
    }

}
