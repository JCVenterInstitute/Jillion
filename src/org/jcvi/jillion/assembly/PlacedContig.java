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
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Rangeable;

public interface PlacedContig extends Rangeable {

	/**
     * Get the start coordinate of this placed object
     * on the placed axis.
     * @return the start as a long.
     */
    long getBegin();
    /**
     * Get the end coordinate of this placed object
     * on the placed axis.
     * @return the end as a long.
     */
    long getEnd();
    /**
     * Get the length of this placed object
     * on the axis.
     * @return the length of this placed object.
     */
    long getLength();
    
    String getContigId();
    Direction getDirection();
    
}
