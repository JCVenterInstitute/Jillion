/*
 * Created on Apr 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;


public abstract class AbstractFastaVisitor implements FastaVisitor{

   

    @Override
    public void visitEndOfFile() {
        //no-op
    }

    @Override
    public void visitLine(String line) {
        //no-op
    }

    @Override
    public void visitBodyLine(String bodyLine) {
        
    }

    @Override
    public void visitDefline(String defline) {
        
    }

    @Override
    public void visitFile() {
        
    }
    
    
}
