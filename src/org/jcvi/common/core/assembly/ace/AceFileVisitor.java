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
package org.jcvi.common.core.assembly.ace;

import java.util.Date;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.FileVisitor;
import org.jcvi.jillion.core.io.TextFileVisitor;
import org.jcvi.jillion.core.qual.QualitySequence;
/**
 * {@code AceFileVisitor} is a {@link FileVisitor}
 * implementation for Ace Files.
 * <p/>
 * The {@link AceFileVisitor} will have the appropriate
 * visit methods called on it as an ace file is parsed/ traversed.
 * @author dkatzel
 *
 *
 */
public interface AceFileVisitor extends TextFileVisitor{
	
	/**
	 * Allowable return values
	 * for {@link AceFileVisitor#visitBeginContig(String, int, int, int, boolean)}.
	 * @author dkatzel
	 *
	 */
	enum BeginContigReturnCode{
		/**
		 * Skip the current contig record.
		 * Every call to {@link AceFileVisitor#visitLine(String)}
		 * followed eventually by {@link AceFileVisitor#visitEndOfContig()}
		 * will still be called but the following methods
		 * will not be called for the current contig:
		 * <ul>
		 * <li>{@link AceFileVisitor#visitBaseSegment(Range, String)}</li>
		 * <li>{@link AceFileVisitor#visitAssembledFromLine(String, Direction, int)}</li>
		 * <li>{@link AceFileVisitor#visitBasesLine(String)}</li>
		 * <li>{@link AceFileVisitor#visitConsensusQualities(QualitySequence)}</li>
		 * <li>{@link AceFileVisitor#visitReadHeader(String, int)}</li>
		 * <li>{@link AceFileVisitor#visitQualityLine(int, int, int, int)}</li>
		 * <li>{@link AceFileVisitor#visitTraceDescriptionLine(String, String, Date)}</li>
		 * </ul>
		 */
		SKIP_CURRENT_CONTIG,
		/**
		 * Completely parse and visit the current contig record.
		 * Every call to {@link AceFileVisitor#visitLine(String)}
		 * will be made as well as the following  method calls in the following order:
		 * <ol>
		 * 
		 * <li>Several calls to {@link AceFileVisitor#visitBasesLine(String)}
		 * one for each line of the contig consensus basecalls.</li>
		 * <li>{@link AceFileVisitor#visitConsensusQualities(QualitySequence)}</li>
		 * <li>1 call to 
		 * {@link AceFileVisitor#visitAssembledFromLine(String, Direction, int)}
		 * for each read in the contig.</li>
		 * <li>1 call to 
		 * {@link AceFileVisitor#visitBaseSegment(Range, String)}
		 * for each base segment line (if there are any).  This section
		 * of an ace file is now optional and not recommended so most
		 * current ace file will probably not include any base segments.
		 * </li>
		 * <p/>
		 * Then for each read:
		 * <li>{@link AceFileVisitor#visitReadHeader(String, int)}</li>
		 * <li>Several calls to {@link AceFileVisitor#visitBasesLine(String)}
		 * one for each line of the read basecalls.</li>
		 * <li>{@link AceFileVisitor#visitQualityLine(int, int, int, int)}</li>
		 * <li>{@link AceFileVisitor#visitTraceDescriptionLine(String, String, Date)}</li>
		 * </ol>
		 * followed eventually by {@link AceFileVisitor#visitEndOfContig()}
		 */
		VISIT_CURRENT_CONTIG
	}
	
	/**
	 * Allowable return values
	 * for {@link AceFileVisitor#visitEndOfContig()}.
	 * @author dkatzel
	 *
	 */
	enum EndContigReturnCode{
		/**
		 * Continue parsing the ace file,
		 * if there are still more records
		 * to be parsed then
		 * if there are more contigs in the file,
		 * then {@link AceFileVisitor#visitBeginContig(String, int, int, int, boolean)}
		 * will be called next or if the next record
		 * in the ace file is a consed tag, then one of the ace tag methods
		 * {@link AceFileVisitor#visitWholeAssemblyTag(String, String, Date, String)}
		 * {@link AceFileVisitor#visitReadTag(String, String, String, long, long, Date, boolean)}
		 * or 
		 * {@link AceFileVisitor#visitBeginConsensusTag(String, String, String, long, long, Date, boolean)}
		 * 
		 * will get called next.
		 * If there are no more records in the file, 
		 * otherwise {@link AceFileVisitor#visitEndOfFile()} will be called.
		 */
		KEEP_PARSING,
		/**
		 * Halt parsing this file
		 * and jump immediately
		 * to {@link AceFileVisitor#visitEndOfFile()}.
		 */
		STOP_PARSING
	}
	
	
	/**
	 * Allowable return values
	 * for {@link AceFileVisitor#visitBeginContig(String, int, int, int, boolean)}.
	 * @author dkatzel
	 *
	 */
	enum BeginReadReturnCode{
		/**
		 * Completely parse and visit the current contig record.
		 * Every call to {@link AceFileVisitor#visitLine(String)}
		 * will be made as well as the following  method calls in the following order:
		 * <ol>
		 * 
		 * <li>Several calls to {@link AceFileVisitor#visitBasesLine(String)}
		 * one for each line of the contig consensus basecalls.</li>
		 * <li>{@link AceFileVisitor#visitConsensusQualities(QualitySequence)}</li>
		 * <li>1 call to 
		 * {@link AceFileVisitor#visitAssembledFromLine(String, Direction, int)}
		 * for each read in the contig.</li>
		 * <li>1 call to 
		 * {@link AceFileVisitor#visitBaseSegment(Range, String)}
		 * for each base segment line (if there are any).  This section
		 * of an ace file is now optional and not recommended so most
		 * current ace file will probably not include any base segments.
		 * </li>
		 * <p/>
		 * Then for each read:
		 * <li>{@link AceFileVisitor#visitReadHeader(String, int)}</li>
		 * <li>Several calls to {@link AceFileVisitor#visitBasesLine(String)}
		 * one for each line of the read basecalls.</li>
		 * <li>{@link AceFileVisitor#visitQualityLine(int, int, int, int)}</li>
		 * <li>{@link AceFileVisitor#visitTraceDescriptionLine(String, String, Date)}</li>
		 * </ol>
		 * followed eventually by {@link AceFileVisitor#visitEndOfContig()}
		 */
		VISIT_CURRENT_READ,
		
		/**
		 * Halt parsing this file
		 * and jump immediately
		 * to {@link AceFileVisitor#visitEndOfFile()}.
		 */
		STOP_PARSING;
	}
	
	/**
	 * Allowable return values
	 * for {@link AceFileVisitor#visitEndOfContig()}.
	 * @author dkatzel
	 *
	 */
	enum EndReadReturnCode{
		/**
		 * Continue parsing the ace file,
		 * if there are still more records
		 * to be parsed then
		 * if there are more contigs in the file,
		 * then {@link AceFileVisitor#visitBeginContig(String, int, int, int, boolean)}
		 * will be called next or if the next record
		 * in the ace file is a consed tag, then one of the ace tag methods
		 * {@link AceFileVisitor#visitWholeAssemblyTag(String, String, Date, String)}
		 * {@link AceFileVisitor#visitReadTag(String, String, String, long, long, Date, boolean)}
		 * or 
		 * {@link AceFileVisitor#visitBeginConsensusTag(String, String, String, long, long, Date, boolean)}
		 * 
		 * will get called next.
		 * If there are no more records in the file, 
		 * otherwise {@link AceFileVisitor#visitEndOfFile()} will be called.
		 */
		KEEP_PARSING,
		/**
		 * Halt parsing this file
		 * and jump immediately
		 * to {@link AceFileVisitor#visitEndOfFile()}.
		 */
		STOP_PARSING
	}
	
    /**
     * Visits the Ace File Header.
     * @param numberOfContigs number of contigs in this file.
     * @param totalNumberOfReads total number of reads in this file.
     */
    void visitHeader(int numberOfContigs, int totalNumberOfReads);

    /**
     * Denotes when a new contig has been detected. 
     * @param contigId the id of the contig visited.
     * @param numberOfBases number of bases in contig (Does this count bases
     * outside of valid range?)
     * @param numberOfReads number of reads in contig.
     * @param numberOfBaseSegments number of base segment lines
     * which indicate reads phrap has chosen to be the consensus
     * at a particular position.
     * @param reverseComplemented is this contig reverse complemented
     * @return a non-null instance of {@link BeginContigReturnCode}
     * which will tell the parser how to proceed with the current contig.
     * If the returned value is null, then the parser will throw
     * a {@link NullPointerException}.
     */
    BeginContigReturnCode visitBeginContig(String contigId, int numberOfBases, int numberOfReads, int numberOfBaseSegments, boolean reverseComplemented);
    /**
     * Visit the ungapped consensus qualities
     * of the current contig being parsed.  This method will only
     * get called if the current contig is being parsed which is determined
     * by the return value of {@link #visitBeginContig(String, int, int, int, boolean)}.
     * @param ungappedConsensusQualities all the
     * ungapped consensus qualities as a {@link QualitySequence};
     * will never be null.
     */
    void visitConsensusQualities(QualitySequence ungappedConsensusQualities);
    /**
     * Visit a line that defines the location of a 
     * read within a contig.  This method will only
     * get called if the current contig is being parsed which is determined
     * by the return value of {@link #visitBeginContig(String, int, int, int, boolean)}.
     * @param readId id of read.
     * @param dir {@link Direction} of read inside contig.
     * @param gappedStartOffset gapped start offset of read inside contig.
     */
    void visitAlignedReadInfo(String readId, Direction dir, int gappedStartOffset);
    /**
     * Base Segments indicate reads phrap has chosen to be the consensus
     * at a particular position.  This method will only
     * get called if ace file contains BaseSegment data 
     * (new versions of consed no longer require it)
     * and the current contig is getting parsed because the previous
     * call to 
     * {@link #visitBeginContig(String, int, int, int, boolean)}
     * returned {@link BeginContigReturnCode#VISIT_CURRENT_CONTIG}.
     * @param gappedConsensusRange range of consensus being defined.
     * @param readId read id that provides coverage at that range.
     */
    void visitBaseSegment(Range gappedConsensusRange, String readId);
    /**
     * Begin visiting a read.  This method will only
     * get called if the current contig is being parsed which is determined
     * by the return value of {@link #visitBeginContig(String, int, int, int, boolean)}.
     * @param readId id of read being visited.
     * @param gappedLength gapped length of read.
     */
    BeginReadReturnCode visitBeginRead(String readId, int gappedLength);
    /**
     * Visit quality line of currently visited read.  This method will only
     * get called if the current contig is being parsed which is determined
     * by the return value of {@link #visitBeginContig(String, int, int, int, boolean)}.
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
     * by the return value of {@link #visitBeginContig(String, int, int, int, boolean)}.
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
     * by the return value of {@link #visitBeginContig(String, int, int, int, boolean)}.
     * @param mixedCaseBasecalls (some of) the basecalls of the currently visited read
     * or consensus which might have both upper and lower case letters to denote
     * high vs low quality.
     * 
     */
    void visitBasesLine(String mixedCaseBasecalls);
    /**
     * Visit a tag that refers to data concerning a single read in the assembly.
     * @param id the read id.
     * @param type the type of this tag; will not have any whitespace.
     * @param creator the program or tool that generated this tag.
     * @param gappedStart the gapped start offset where this tag refers.  Depending
     * on what the tag is used for this could be consensus coordinates or read coordinates.
     * @param gappedEnd the gapped end offset where this tag refers.  Depending
     * on what the tag is used for this could be consensus coordinates or read coordinates.
     * @param creationDate the date that this tag was created.
     * @param isTransient a transient tag should not be preserved if the assembly
     * is re-assembled; returns {@code true} if transient; {@code false} otherwise.
     */
    void visitReadTag(String id, String type, String creator, long gappedStart, long gappedEnd, Date creationDate, boolean isTransient);
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
    EndContigReturnCode visitEndOfContig();
    /**
     * Begin to start visiting a consensus tag.  Consensus tags are multiple lines
     * and can contain nested comments inside them.  Any lines visited from now
     * until {@link #visitEndConsensusTag()} will be for lines inside this tag.
     * @param id the contig id.
     * @param type the type of this tag; will not have any whitespace.
     * @param creator the program or tool that generated this tag.
     * @param gappedStart the gapped start offset where this tag refers.  Depending
     * on what the tag is used for this could be consensus coordinates or read coordinates.
     * @param gappedEnd the gapped end offset where this tag refers.  Depending
     * on what the tag is used for this could be consensus coordinates or read coordinates.
     * @param creationDate the date that this tag was created.
     * @param isTransient a transient tag should not be preserved if the assembly
     * is re-assembled; returns {@code true} if transient; {@code false} otherwise.
     */
    void visitBeginConsensusTag(String id, String type, String creator, long gappedStart, long gappedEnd, Date creationDate, boolean isTransient);
    /**
     * The current consensus tag contains a comment (which might span multiple lines).
     * @param comment the full comment as a string.
     */
    void visitConsensusTagComment(String comment);
    /**
     * The current consensus tag contains a data.
     * @param data the data as a string.
     */
    void visitConsensusTagData(String data);
    /**
     * The current consensus tag has been completely parsed.
     */
    void visitEndConsensusTag();
    /**
     * Visit a tag that refers to data that concerns the entire ace file.  Example
     * whole assembly tags are locations to phd.balls that store the quality data
     * for all the reads in the assembly.
     * @param type the type of this tag; will not have any whitespace.
     * @param creator the program or tool that generated this tag.
     * @param creationDate the date that this tag was created.
     * @param data the actual content of this tag which could be a single line or span
     * multiple lines depending on what the tag is for.
     */
    void visitWholeAssemblyTag(String type, String creator, Date creationDate, String data);
}
