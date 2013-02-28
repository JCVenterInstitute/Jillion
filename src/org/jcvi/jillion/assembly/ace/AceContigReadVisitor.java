package org.jcvi.jillion.assembly.ace;

import java.util.Date;

public interface AceContigReadVisitor {

	/**
     * Visit quality line of currently visited read.  This method will only
     * get called if the current contig is being parsed which is determined
     * by the return value of {@link #visitContig(String, int, int, int, boolean)}.
     * @param qualLeft left position(1-based)  of clear range.
     * @param qualRight right position(1-based) of clear range.
     * @param alignLeft left alignment(1-based) position. 
     * @param alignRight right alignment(1-based) position.
     */
    void visitQualityLine(int qualLeft, int qualRight, int alignLeft, int alignRight);
    /**
     * Visit Trace Description line of currently visited read.
     * @param traceName name of trace file corresponding
     * to currently visited read.  This method will only
     * get called if the current contig is being parsed which is determined
     * by the return value of {@link #visitContig(String, int, int, int, boolean)}.
     * @param phdName name of phd file.
     * @param date date phd file created.
     */
    void visitTraceDescriptionLine(String traceName, String phdName, Date date);
    /**
     * Visit a line of basecalls of currently visited read. A read 
     * probably has several lines of basecalls.  The characters in the bases
     * could be mixed case.  Consed differentiates high quality basecalls
     * vs low quality basecalls by using upper and lowercase letters respectively.
     * This method will only
     * get called if the current contig is being parsed which is determined
     * by the return value of {@link #visitContig(String, int, int, int, boolean)}.
     * @param mixedCaseBasecalls (some of) the basecalls of the currently visited read
     * or consensus which might have both upper and lower case letters to denote
     * high vs low quality.
     * 
     */
    void visitBasesLine(String mixedCaseBasecalls);
    

    void visitEnd();
    
    void halted();
}
