package org.jcvi.jillion.assembly.ace;

public interface AceConsensusTagVisitor {

	/**
     * The current consensus tag contains a comment (which might span multiple lines).
     * @param comment the full comment as a string.
     */
    void visitComment(String comment);
    /**
     * The current consensus tag contains a data.
     * @param data the data as a string.
     */
    void visitData(String data);
    /**
     * The current consensus tag has been completely parsed.
     */
    void visitEnd();
    
    void halted();
}
