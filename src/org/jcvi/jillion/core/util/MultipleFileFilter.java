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
package org.jcvi.jillion.core.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@code MultipleFileFilter} is a {@link FileFilter}
 * implementation that wraps multiple
 * {@link FileFilter} delegates and will
 * only accept a File if all the delegates
 * accept it.
 * @author dkatzel
 *
 *
 */
final class MultipleFileFilter implements FileFilter{

    private final List<FileFilter> delegates;
    
    
    public MultipleFileFilter(List<FileFilter> delegates) {
        this.delegates = new ArrayList<FileFilter>(delegates);
    }
    public MultipleFileFilter(FileFilter...fileFilters){
        this(Arrays.asList(fileFilters));
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean accept(File pathname) {
        for(FileFilter delegate : delegates) {
            if(!delegate.accept(pathname)){
                return false;
            }
        }
        return true;
    }

}
