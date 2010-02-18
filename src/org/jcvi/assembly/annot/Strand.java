/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot;

public enum Strand {

    FORWARD("+"),
    REVERSE("-");
    
    private String stringRep;
    
    Strand(String stringRep){
        
        this.stringRep = stringRep;
    }

    @Override
    public String toString() {
        return stringRep;
    }
    
    public Strand oppositeStrand(){
        if(this == FORWARD){
            return REVERSE;
        }
        return FORWARD;
    }
    
    
}
