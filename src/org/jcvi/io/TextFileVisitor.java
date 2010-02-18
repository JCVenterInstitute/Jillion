/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

public interface TextFileVisitor extends FileVisitor {
    /**
     * Visit a new line.  
     * @param line the line being visited; this line
     * contains all whitespace originally present in the line
     * including the {@code \n}
     */
    void visitLine(String line);
}
