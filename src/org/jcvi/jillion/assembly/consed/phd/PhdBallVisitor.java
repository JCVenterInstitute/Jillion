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
package org.jcvi.jillion.assembly.consed.phd;
/**
 * {@code PhdBallVisitor} is a visitor
 * for a phd.ball file or phd file that contains
 * one or more phd records.
 * 
 * @author dkatzel
 *
 */
public interface PhdBallVisitor {
	/**
	 * Optional comment at the beginning
	 * of new versions of phd.ball files.
	 * The comment is often the path to the 
	 * corresponding wrapped input file
	 * (path to fastq or sff file).
	 * 
	 * @param comment the comment at the beginning of the phd file.
	 * 
	 */
	void visitFileComment(String comment);
	/**
	 * Denotes when a new phd record has been detected.
	 * @param callback the {@link PhdBallVisitorCallback} instance
	 * that can be used to call pack to the parser.
	 * @param id the id of this phd record.
	 * @param version the version of this phd record; if the version
	 * is not specified, then this value will be {@code null}. More recent versions
	 * of the phdball file format specification allow
	 * the version to be included for each record but not all files
	 * contain this information. 
	 * @return a non-null {@link PhdVisitor} instance if this
	 * record should be parsed; or {@code null} if this phd record
	 * should be skipped.
	 */
	PhdVisitor visitPhd(PhdBallVisitorCallback callback, String id , Integer version);

	/**
	 * The phd file has been completely visited.
	 */
	void visitEnd();
	/**
	 * The phd visitation has been halted,
	 * usually by calling {@link PhdBallVisitorCallback#haltParsing()}.
	 */
	void halted();
}
