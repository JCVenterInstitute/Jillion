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
/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.assembly.AssembledRead;
/**
 * {@code AceAssembledRead} is a {@link AssembledRead} implementation
 * specific for reads from an ace file.
 * @author dkatzel
 */
public interface AceAssembledRead extends AssembledRead{
    /**
     * Get the {@link PhdInfo} associated with this
     * read.
     * @return a {@link PhdInfo} should never be null.
     */
    PhdInfo getPhdInfo();
    
}
