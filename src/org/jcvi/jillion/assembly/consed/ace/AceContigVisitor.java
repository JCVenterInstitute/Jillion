/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;

public interface AceContigVisitor {

	/**
     * Visit a line of basecalls of currently visited contig. A contig 
     * probably has several lines of basecalls.  The characters in the bases
     * could be mixed case.  Consed differentiates high quality basecalls
     * vs low quality basecalls by using upper and lowercase letters respectively.
     * 
     * @param mixedCaseBasecalls (some of) the basecalls of the currently visited read
     * or consensus which might have both upper and lower case letters to denote
     * high vs low quality.
     * 
     */
    void visitBasesLine(String mixedCaseBasecalls);
	/**
     * Visit the ungapped consensus qualities
     * of the current contig being parsed.
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
     * (new versions of consed no longer require it).
     * 
     * @param gappedConsensusRange range of consensus being defined.
     * @param readId read id that provides coverage at that range.
     */
    void visitBaseSegment(Range gappedConsensusRange, String readId);
    
    /**
     * Begin visiting a read.  If this read should get visited, then 
     * this visitor should return a non-null instance of {@link AceContigReadVisitor}.
     * @param readId id of read being visited.
     * @param gappedLength gapped length of read.
     * @return an {@link AceContigReadVisitor} instance which will
     * get its visitXXX methods called for the contents of the current read,
     * or {@code null} if this read should be skipped. 
     */
    AceContigReadVisitor visitBeginRead(String readId, int gappedLength);
    
    /**
     * The current contig being visited contains no more data.
     */
    void visitEnd();
    /**
     * Visiting has been prematurely halted
     * probably by a call to {@link AceFileVisitorCallback#haltParsing()}.
     */
    void halted();
}
