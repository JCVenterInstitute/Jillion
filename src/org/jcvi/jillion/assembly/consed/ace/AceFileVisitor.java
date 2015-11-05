/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;

/**
 * {@code AceFileVisitor} is a Visitor
 * implementation for .ace encoded assembly files.
 * <p>
 * The {@link AceFileVisitor} will have the appropriate
 * visit methods called on it as an ace file is parsed/ traversed.
 * @author dkatzel
 *
 *
 */
public interface AceFileVisitor {
	
	
    /**
     * Visits the Ace File Header.
     * @param numberOfContigs number of contigs in this file.
     * @param totalNumberOfReads total number of reads in this file.
     */
    void visitHeader(int numberOfContigs, long totalNumberOfReads);

    /**
     * Denotes when a new contig has been detected.
     * @param callback an {@link AceFileVisitorCallback} instance. 
     * @param contigId the id of the contig visited.
     * @param numberOfBases number of bases in contig (Does this count bases
     * outside of valid range?)
     * @param numberOfReads number of reads in contig.
     * @param numberOfBaseSegments number of base segment lines
     * which indicate reads phrap has chosen to be the consensus
     * at a particular position.
     * @param reverseComplemented is this contig reverse complemented
     * @return an instance of {@link AceContigVisitor} which will
     * get its visitXXX methods called for this particular contig;
     * or {@code null} if this contig should be skipped.
     */
    AceContigVisitor visitContig(AceFileVisitorCallback callback, String contigId, int numberOfBases, int numberOfReads, int numberOfBaseSegments, boolean reverseComplemented);
    
    
    
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
     * Begin to start visiting a consensus tag.  Consensus tags are multiple lines
     * and can contain nested comments inside them.  Any lines visited from now
     * until {@link #visitEnd()} will be for lines inside this tag.
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
     * 
     * @return a {@link AceConsensusTagVisitor} to be visited;
     * or {@code null} if this tag should be skipped.
     */
    AceConsensusTagVisitor visitConsensusTag(String id, String type, String creator, long gappedStart, long gappedEnd, Date creationDate, boolean isTransient);
    
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
    /**
     * The end of the ace file as been reached.
     */
    void visitEnd();
    /**
     * Visiting the ace has been prematurely halted
     * probably by a call to {@link AceFileVisitorCallback#haltParsing()}.
     */
    void halted();
}
