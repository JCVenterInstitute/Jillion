/*
 * Created on Apr 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.io.TextFileVisitor;

public interface FastaVisitor extends TextFileVisitor{

    void visitDefline(String defline);
    
    void visitBodyLine(String bodyLine);
    
    void visitRecord(String id, String comment, String entireBody);
}
