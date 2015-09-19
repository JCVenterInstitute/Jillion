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
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;
/**
 * {@code FileVisitor} is an base interface
 * which uses the "push approach" to walk
 * over complicated File structures. 
 * This is similar to the Event and Visitor Design 
 * Patterns where each method follows
 * the format visitXXX.  It is up
 * to the implementor of this interface
 * to determine what to do during
 * each visitXXX call.
 * 
 * @author dkatzel
 *
 *
 */
public interface FileVisitor {
    /**
     * Visiting a new File.
     */
    void visitFile();
   
    /**
     * The File has been completely visited.
     */
    void visitEndOfFile();
}
