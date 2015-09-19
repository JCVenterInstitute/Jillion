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

/**
 * @author dkatzel
 *
 *
 */
public class FileIteratorTestUtil {
    /**
     * A {@link FileFilter} that only accepts
     * Files whose names end with "2"
     */
    public static final FileFilter FILE_FILTER_ANYTHING_THAT_DOESNT_END_WITH_2 = new FileFilter() {
        
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith("2");
        }
    };
}
