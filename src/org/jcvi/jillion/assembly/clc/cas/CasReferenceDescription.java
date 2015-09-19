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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;
/**
 * {@code CasContigDescription} is an interface
 * which explains details about a contig
 * (reference).
 * @author dkatzel
 *
 *
 */
public interface CasReferenceDescription {
    /**
     * Get the length of this reference sequence.
     * @return the length of this reference as a positive long.
     */
    long getContigLength();
    /**
     * Is this reference circular.
     * @return {@code true} if this reference is circular;
     * {@code false} otherwise.
     */
    boolean isCircular();
}
