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
package org.jcvi.jillion.trace.fastq;



/**
 * A visitor
 * interface to visit components of  paired end
 * fastq files.
 * 
 * @author dkatzel
 * 
 * @since 6.0.2
 *
 */
public interface PairedFastqVisitor {
	/**
	 * {@code FastqVisitorCallback}
	 * is a callback mechanism for the {@link FastqVisitor}
	 * instance to communicate with the parser
	 * that is parsing the fastq data.
	 * @author dkatzel
	 *
	 */
	public interface PairedFastqVisitorCallback{
		/**
		 * {@code PairedFastqVisitorCallback} is a marker
		 * interface that {@link FastqFileParser}
		 * instances can use to "rewind" back
		 * to the position in its fastq file
		 * in order to revisit portions of the fastq file. 
		 * {@link PairedFastqVisitorMemento} should only be used
		 * by the {@link PairedFastqVisitor} instance that
		 * generated it.
		 * @author dkatzel
		 *
		 */
		public interface PairedFastqVisitorMemento{
			
		}
		/**
		 * Is this callback capable of
		 * creating {@link PairedFastqVisitorMemento}s
		 * via {@link #createMemento()}.
		 * @return {@code true} if this callback
		 * can create mementos; {@code false} otherwise.
		 */
		boolean canCreateMemento();
		/**
		 * Create a {@link PairedFastqVisitorMemento}
		 * 
		 * @return a {@link FastqVisitorMemento}; never null.
		 * @see #canCreateMemento()
		 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
		 * returns {@code false}.
		 */
		PairedFastqVisitorMemento createMemento();
		/**
		 * Tell the {@link PairedFastqVisitor} to stop parsing
		 * the fastq file.  {@link PairedFastqVisitor#halted()}
		 * will still be called.
		 */
		void haltParsing();
		 
	}
	/**
     * Visit the defline of a the current fastq records.
     * <strong>Note: </strong>if the fastq records were created using 
     * Casava 1.8, then the id will contain a whitespace
     * followed by the mate information and no comment.
     * This is different than most other fastq parsers which separate
     * on whitespace and therefore will create duplicate ids for each
     * mate in the template (but with different values for the "comments").
     * Duplicate ids will break any applications that combine all the reads
     * from multiple fastq files so it was decided that {@link FastqRecord} id
     * contain both the template and mate information to guarantee uniqueness.
     * 
     * 
     * 
     * @param callback a {@link PairedFastqVisitorCallback} that can be used
     * to communicate with the parser object.
     * @param id the id of this record as a String
     * @param optionalComment the comment for this record.  This comment
     * may have white space.  If no comment exists, then this
     * parameter will be null.
     * @return an instance of {@link FastqRecordVisitor};
     * if this method returns null, then that means
     * to skip the current record.
     */
	PairedFastqRecordVisitor visitDefline(PairedFastqVisitorCallback callback, String id, String optionalComment);
	/**
	 * Visit the end of the fastq file.
	 */
	void visitEnd();
	
	 /**
     * The parser has stopped parsing the fastq file
     * due to {@link FastqVisitorCallback#haltParsing()}
     * being called. The end of the fastq file was
     * not yet reached.
     */
    void halted();
}
