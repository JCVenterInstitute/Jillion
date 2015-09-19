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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code SliceElement} is a single
 * position of a read contained
 * in a {@link Slice}. Each read
 * in the slice contributes a base, quality value, and direction.
 * @author dkatzel
 *
 *
 */
public interface SliceElement {
	
	/**
     * Get the Id of this element.  Each element in a single Slice must
     * have a different Id, although SliceElements from 
     * different Slices can have the same Id.  This Id is usually the 
     * read Id.
     * @return the Id of this slice element.
     */
    String getId();
    /**
     * Get the {@link Nucleotide} of this SliceElement.
     * @return
     */
    Nucleotide getBase();
    /**
     * Get the {@link PhredQuality} of this SliceElement.
     * @return
     */
    PhredQuality getQuality();
    /**
     * Get the {@link Direction} of this SliceElement.
     * @return
     */
    Direction getDirection();
}
