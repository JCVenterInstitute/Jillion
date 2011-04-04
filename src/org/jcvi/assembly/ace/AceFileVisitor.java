/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;

import org.jcvi.Range;
import org.jcvi.io.FileVisitor;
import org.jcvi.io.TextFileVisitor;
import org.jcvi.sequence.SequenceDirection;
/**
 * {@code AceFileVisitor} is a {@link FileVisitor}
 * implementation for Ace Files.
 * @author dkatzel
 *
 *
 */
public interface AceFileVisitor extends TextFileVisitor{
    /**
     * Visits the Ace File Header.
     * @param numberOfContigs number of contigs in this file.
     * @param totalNumberOfReads total number of reads in this file.
     */
    void visitHeader(int numberOfContigs, int totalNumberOfReads);
    /**
     * Visit the head of a specific contig in the contig file.
     * @param contigId the id of the contig visited.
     * @param numberOfBases number of bases in contig (Does this count bases
     * outside of valid range?)
     * @param numberOfReads number of reads in contig.
     * @param numberOfBaseSegments number of base segment lines
     * which indicate reads phrap has chosen to be the consensus
     * at a particular position.
     * @param reverseComplimented is this contig reverse complimented
     */
    void visitContigHeader(String contigId, int numberOfBases, int numberOfReads, int numberOfBaseSegments, boolean reverseComplimented);
    /**
     * begin visiting consensus qualities.
     */
    void visitConsensusQualities();
    /**
     * AssembledFroms define the location of the 
     * read within a contig.
     * @param readId id of read.
     * @param dir {@link SequenceDirection} of read inside contig.
     * @param gappedStartOffset gapped start offset of read inside contig.
     */
    void visitAssembledFromLine(String readId, SequenceDirection dir, int gappedStartOffset);
    /**
     * Base Segments indicate reads phrap has chosen to be the consensus
     * at a particular position.
     * @param gappedConsensusRange range of consensus being defined.
     * @param readId read id that provides coverage at that range.
     */
    void visitBaseSegment(Range gappedConsensusRange, String readId);
    /**
     * begin visiting a read.
     * @param readId id of read being visited.
     * @param gappedLength gapped length of read.
     */
    void visitReadHeader(String readId, int gappedLength);
    /**
     * visit quality line of currently visited read.
     * @param qualLeft left position(1-based)  of clear range.
     * @param qualRight right position(1-based) of clear range.
     * @param alignLeft left alignment(1-based) position. 
     * @param alignRight right alignment(1-based) position.
     */
    void visitQualityLine(int qualLeft, int qualRight, int alignLeft, int alignRight);
    /**
     * Visit Trace Description line of currently visited read.
     * @param traceName name of trace file corresponding
     * to currently visited read.
     * @param phdName name of phd file.
     * @param date date phd file created.
     */
    void visitTraceDescriptionLine(String traceName, String phdName, Date date);
    /**
     * Visit a line of basecalls of currently visited read. A read 
     * probably has several lines of basecalls.
     * @param bases (some of) the basecalls of the currently visited read.
     */
    void visitBasesLine(String bases);
    
    void visitReadTag(String id, String type, String creator, long gappedStart, long gappedEnd, Date creationDate, boolean isTransient);
    /**
     * The current contig being visited contains no more data.
     */
    void visitEndOfContig();
    
    void visitBeginConsensusTag(String id, String type, String creator, long gappedStart, long gappedEnd, Date creationDate, boolean isTransient);
    void visitConsensusTagComment(String comment);
    void visitConsensusTagData(String data);
    void visitEndConsensusTag();
    
    void visitWholeAssemblyTag(String type, String creator, Date creationDate, String data);
}
