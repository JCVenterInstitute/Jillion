/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.core;
/**
 * Something has has an ID and an optional comment.
 * This is usually referred to as a definition line in many
 * bioinformatics file formats.
 * 
 * @author dkatzel
 *
 * @since 5.3
 */
public interface Defline {

    /**
     * Get the Id of this record.
     * @return A <code>String</code>.
     */
    String getId();

    /**
     * Get the comment (if any) associated with this record.
     * @return A <code>String</code> of the comment
     * or {@code null} if there is no comment.
     */
    String getComment();
    
    /**
     * Create a new Defline instance with the given id and no comment.
     * @param id the id; can not be null.
     * 
     * @return a new Defline instance.
     * 
     * @apiNote this is the same as {@link #of(String, String) of(id, null)}
     * 
     * @throws NullPointerException if id is null.
     */
    public static Defline of(String id){
        return of(id, null);
    }
    
    /**
     * Create a new Defline instance with the given id and comment.
     * @param id the id; can not be null.
     * @param comment the optional comment; set to null if a comment is not present.
     * @return a new Defline instance.
     * 
     * @throws NullPointerException if id is null.
     */
    public static Defline of(String id, String comment){
        return new DeflineImpl(id, comment);
    }

}
