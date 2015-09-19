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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.List;
/**
 * {@code ConsensusAceTag} is an {@link AceTag}
 * that maps to a particular location on the 
 * consensus of a contig in an ace file.
 * @author dkatzel
 */
public interface ConsensusAceTag extends RangeableAceTag {
    
    /**
     * Get the Id of contig
     * this tag references.
     */
    String getId();
    /**
     * Get any comments (if any) associated with this
     * tag.
     * @return a Set of all the comments (in case there are 
     * multiple comments).  Each comment string may contain
     * multiple lines, if there are no comments,
     * then the list is empty; never null.
     */
    List<String> getComments();
}
