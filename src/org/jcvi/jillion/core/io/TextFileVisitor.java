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
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;
/**
 * {@code TextFileVisitor} is a {@link FileVisitor}
 * that visits a text file.
 * @author dkatzel
 *
 */
public interface TextFileVisitor extends FileVisitor {
    /**
     * Visit a new line.  
     * @param line the line being visited; this line
     * contains all whitespace originally present in the line
     * including the end of line characters.
     */
    void visitLine(String line);
}
