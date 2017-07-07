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
