/*
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;
/**
 * {@code FileVisitor} is an base interface
 * which conforms to the Visitor Pattern
 * for visiting a text file.
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
     * The File has been completely visitied.
     */
    void visitEndOfFile();
}
