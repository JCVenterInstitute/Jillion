package org.jcvi.jillion.assembly.clc.cas;

public interface CasMatchVisitor {

	/**
     * Visit a {@link CasMatch}.
     * @param match the CasMatch object being visited (never null)
     */
    void visitMatch(CasMatch match);
    
    /**
     * All matches have been visited.
     */
    void visitEnd();
    
    void halted();
}
