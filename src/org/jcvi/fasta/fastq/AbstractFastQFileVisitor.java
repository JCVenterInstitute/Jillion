/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;


public abstract class AbstractFastQFileVisitor <T extends FastQRecord> implements FastQFileVisitor{

    private boolean initialized=false;

    protected void checkNotYetInitialized(){
        if(initialized){
            throw new IllegalStateException("already initialized, can not visit more records");
        }
    }    


    @Override
    public void visitEndOfFile() {
        checkNotYetInitialized();
        initialized=true;        
    }

    @Override
    public void visitLine(String line) {
        checkNotYetInitialized();
       
        
    }
    
    @Override
    public boolean visitEncodedQualities(String encodedQualities) {
        return true;
    }

    @Override
    public boolean visitEndBlock() {
        return true;
    }

    @Override
    public boolean visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
        return true;
    }

    @Override
    public void visitFile() {
        
    }
}
