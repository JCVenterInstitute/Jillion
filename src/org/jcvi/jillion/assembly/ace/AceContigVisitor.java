package org.jcvi.jillion.assembly.ace;

import org.jcvi.jillion.assembly.ace.AceFileVisitor.BeginContigReturnCode;
import org.jcvi.jillion.assembly.ace.AceFileVisitor.EndContigReturnCode;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;

public interface AceContigVisitor {

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
	/**
     * Visit the ungapped consensus qualities
     * of the current contig being parsed.  This method will only
     * get called if the current contig is being parsed which is determined
     * by the return value of {@link #visitContig(String, int, int, int, boolean)}.
     * @param ungappedConsensusQualities all the
     * ungapped consensus qualities as a {@link QualitySequence};
     * will never be null.
     */
    void visitConsensusQualities(QualitySequence ungappedConsensusQualities);
    /**
     * Visit a line that defines the location of a 
     * read within a contig. This is equivalent to an AF line
     * in ace file.
     * @param readId id of read.
     * @param dir {@link Direction} of read inside contig.
     * @param gappedStartPosition gapped start position (1's based) of read inside contig.
     */
    void visitAlignedReadInfo(String readId, Direction dir, int gappedStartPosition);
    /**
     * Base Segments indicate reads phrap has chosen to be the consensus
     * at a particular position.  This method will only
     * get called if ace file contains BaseSegment data 
     * (new versions of consed no longer require it)
     * and the current contig is getting parsed because the previous
     * call to 
     * {@link #visitContig(String, int, int, int, boolean)}
     * returned {@link BeginContigReturnCode#VISIT_CURRENT_CONTIG}.
     * @param gappedConsensusRange range of consensus being defined.
     * @param readId read id that provides coverage at that range.
     */
    void visitBaseSegment(Range gappedConsensusRange, String readId);
    
    /**
     * Begin visiting a read.  This method will only
     * get called if the current contig is being parsed which is determined
     * by the return value of {@link #visitContig(String, int, int, int, boolean)}.
     * @param readId id of read being visited.
     * @param gappedLength gapped length of read.
     */
    AceContigReadVisitor visitBeginRead(String readId, int gappedLength);
    
    /**
     * The current contig being visited contains no more data.
     * This method will be called as soon as it is determined
     * that there is no more data for the current contig
     * and <strong>before</strong> the next line is visited
     * via the {@link #visitLine(String)}.
     * @return a non-null instance of {@link EndContigReturnCode}
     * which will tell the parser how to proceed with the ace file.
     * If the returned value is null, then the parser will throw
     * a {@link NullPointerException}.
     */
    void visitEnd();
    
    void halted();
}
