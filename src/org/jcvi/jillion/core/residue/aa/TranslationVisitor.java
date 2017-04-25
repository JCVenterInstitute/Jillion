package org.jcvi.jillion.core.residue.aa;

public interface TranslationVisitor {

    public enum FoundStartResult{
            FIND_ADDITIONAL_STARTS,
            CONTINUE,
            STOP
    }
    public enum FoundStopResult{
        READ_THROUGH,
        STOP
    }
    
   
    void visitCodon(long nucleotideCoordinate, Codon codon);
    FoundStartResult foundStart(long nucleotideCoordinate, Codon codon);
    
    FoundStopResult foundStop(long nucleotideCoordinate, Codon codon);
    
    void end();
}
