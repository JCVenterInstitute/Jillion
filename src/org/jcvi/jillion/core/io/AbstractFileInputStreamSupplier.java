package org.jcvi.jillion.core.io;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
/**
 * Abstract {@link InputStreamSupplier} that returns an actual
 * File for {@link #getFile()}.
 * 
 * @author dkatzel
 *
 * @since 5.2
 */
abstract class AbstractFileInputStreamSupplier implements
        InputStreamSupplier {

    protected final File  file;
    
    

    public AbstractFileInputStreamSupplier(File file) {
        Objects.requireNonNull(file);
        this.file = file;
    }



    @Override
    public Optional<File> getFile() {
        return Optional.of(file);
    }
    

}
