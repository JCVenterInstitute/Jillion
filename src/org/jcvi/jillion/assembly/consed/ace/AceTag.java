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
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;

/**
 * Ace files can contain tags at the end of the file
 * which can inform consed tools of various features
 * or hints or additional information that can not
 * be explained in the standard ace file format.
 * @author dkatzel
 */
public interface AceTag{
    /**
     * Each tag has a type which is a free form string 
     * with no whitespace. 
     * @return A String never null.
     */
    String getType();
    /**
     * The program or tool that generated this tag.
     * @return a String never null.
     */
    String getCreator();
    /**
     * The date that this tag was created.
     * @return a {@link Date}; never null.
     */
    Date getCreationDate();
    /**
     * Get the data (not counting header info or comments) in the tag as a String.
     * @return the data or {@code null} if no Data exists.
     */
    String getData();
}
