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

import org.jcvi.jillion.assembly.util.columns.AssemblyColumn;
import org.jcvi.jillion.assembly.util.columns.QualifiedAssemblyColumn;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * A {@code Slice} is a one base wide vertical cut of an assembly containing zero 
 * or more {@link SliceElement}s.
 * @author dkatzel
 *
 *
 */
public interface Slice extends QualifiedAssemblyColumn<SliceElement>{
    /**
     * Does this {@link Slice} contain a 
     * {@link SliceElement} with the given id.
     * @param elementId the id of the {@link SliceElement} being queried.
     * @return {@code true} if this Slice does contain
     * a {@link SliceElement} with the given id; {@code false} otherwise.
     */
    boolean containsElement(String elementId);
    /**
     * Get the SliceElement by id.
     * @param elementId the id of the SliceElement to get.
     * @return the {@link SliceElement} if exists; or {@code null}
     * if there is no {@link SliceElement} for this Slice with that id.
     */
    SliceElement getSliceElement(String elementId);
    
   
}
