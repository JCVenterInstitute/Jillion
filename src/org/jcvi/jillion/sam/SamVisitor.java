package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.header.SamHeader;
/**
 * {@code SamVisitor} is the visitor
 * interface to visit a single SAM or BAM file.
 * @author dkatzel
 *
 */
public interface SamVisitor {
	
	/**
	 * {@code SamVisitorCallback}
	 * is a callback mechanism for the {@link SamVisitor}
	 * instance to communicate with the parser
	 * that is parsing the SAM or BAM file.
	 * @author dkatzel
	 *
	 */
	public interface SamVisitorCallback{
		/**
		 * {@code SamVisitorMemento} is a marker
		 * interface that {@link SamParser}
		 * instances can use to "rewind" back
		 * to the position in its SAM or BAM file
		 * in order to revisit portions of the that file. 
		 * {@link SamVisitorMemento} should only be used
		 * by the {@link SamParser} instance that
		 * generated it.
		 * @author dkatzel
		 *
		 */
		public interface SamVisitorMemento{
			
		}
		/**
		 * Is this callback capable of
		 * creating {@link SamVisitorMemento}s
		 * via {@link #createMemento()}.
		 * @return {@code true} if this callback
		 * can create mementos; {@code false} otherwise.
		 */
		boolean canCreateMemento();
		/**
		 * Create a {@link SamVisitorMemento}
		 * 
		 * @return a {@link SamVisitorMemento}; never null.
		 * @see #canCreateMemento()
		 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
		 * returns {@code false}.
		 */
		SamVisitorMemento createMemento();
		/**
		 * Tell the {@link SamVisitor} to stop parsing
		 * the SAM or BAM file.  {@link SamVisitor#halted()}
		 * will still be called.
		 */
		void haltParsing();
	}
	/**
	 * Visit the {@link SamHeader} of this SAM or BAM file.
	 * The {@link SamHeader} should contain all the information
	 * described in the header section of the SAM or BAM file
	 * and also include reference sequence names, program ids etc
	 * that are referenced later in the SAM or BAM file.
	 * @param header the complete {@link SamHeader} for this file;
	 * will not be null.
	 * @param callback a {@link SamVisitorCallback} that can be used
     * to communicate with the parser object; will never be null.
	 */
	void visitHeader(SamVisitorCallback callback, SamHeader header);
	/**
	 * Visit the next {@link SamRecord} in the file.  This
	 * method is only called if there is no
	 * any {@link VirtualFileOffset} information available
	 * (for example, this is a SAM file, and not a BAM file),
	 * otherwise {@link #visitRecord(SamVisitorCallback, SamRecord, VirtualFileOffset, VirtualFileOffset)}
	 * is called instead for each {@link SamRecord}.
	 * @param callback a {@link SamVisitorCallback} that can be used
     * to communicate with the parser object; will never be null.
	 * @param record the {@link SamRecord} to visit; will never be null.
	 * @see #visitRecord(SamVisitorCallback, SamRecord, VirtualFileOffset, VirtualFileOffset)
	 */
	void visitRecord(SamVisitorCallback callback, SamRecord record);
	/**
	 * Visit the next {@link SamRecord} in the file.  This
	 * method is only called if there is valid
	 * {@link VirtualFileOffset} information available
	 * (for example, this is a BAM file, and not a SAM file),
	 * otherwise {@link #visitRecord(SamVisitorCallback, SamRecord)}
	 * is called instead for each {@link SamRecord}.
	 * @param callback a {@link SamVisitorCallback} that can be used
     * to communicate with the parser object; will never be null.
	 * @param record the {@link SamRecord} to visit; will never be null.
	 * @see #visitRecord(SamVisitorCallback, SamRecord)
	 */
	void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start, VirtualFileOffset end);
	/**
	 * Reached the end of the SAM or BAM file
	 * (without halting).
	 */
	void visitEnd();
	
	 /**
     * The parser has stopped parsing the SAM or BAM file
     * due to {@link SamVisitorCallback#haltParsing()}
     * being called. The end of the fastq file was
     * not yet reached.
     */
    void halted();
}
