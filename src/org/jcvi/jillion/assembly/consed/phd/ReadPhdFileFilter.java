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
package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.FileFilter;

class ReadPhdFileFilter implements FileFilter{
    private final String readId;

    public ReadPhdFileFilter(String readId) {
        this.readId = readId;
    }



    /**
    * {@inheritDoc}
    */
    @Override
    public boolean accept(File pathname) {
        
        return pathname.getName().startsWith(readId);
    }
    
}
