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
package org.jcvi.jillion.trace.fastq;



/**
 * {@code FastqVisitor} is a visitor
 * interface to visit components of a single
 * fastq file.
 * 
 * @author dkatzel
 *
 */
public interface FastqVisitor {
	/**
	 * {@code FastqVisitorCallback}
	 * is a callback mechanism for the {@link FastqVisitor}
	 * instance to communicate with the parser
	 * that is parsing the fastq data.
	 * @author dkatzel
	 *
	 */
	public interface FastqVisitorCallback{
		/**
		 * {@code FastqVisitorMemento} is a marker
		 * interface that {@link FastqFileParser}
		 * instances can use to "rewind" back
		 * to the position in its fastq file
		 * in order to revisit portions of the fastq file. 
		 * {@link FastqVisitorMemento} should only be used
		 * by the {@link FastqFileParser} instance that
		 * generated it.
		 * @author dkatzel
		 *
		 */
		public interface FastqVisitorMemento{
			
		}
		/**
		 * Is this callback capabable of
		 * creating {@link FastqVisitorMemento}s
		 * via {@link #createMemento()}.
		 * @return {@code true} if this callback
		 * can create mementos; {@code false} otherwise.
		 */
		boolean canCreateMemento();
		/**
		 * Create a {@link FastqVisitorMemento}
		 * 
		 * @return a {@link FastqVisitorMemento}; never null.
		 * @see #canCreateMemento()
		 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
		 * returns {@code false}.
		 */
		FastqVisitorMemento createMemento();
		/**
		 * Tell the {@link FastqFileParser} to stop parsing
		 * the fastq file.  {@link FastqVisitor#visitEnd()}
		 * will still be called.
		 */
		void haltParsing();
	}
	/**
     * Visit the defline of a the current fastq record.
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
     * @param callback a {@link FastqVisitorCallback} that can be used
     * to communicate with the parser object.
     * @param id the id of this record as a String
     * @param optionalComment the comment for this record.  This comment
     * may have white space.  If no comment exists, then this
     * parameter will be null.
     * @return an instance of {@link FastqRecordVisitor};
     * if this method returns null, then that means
     * to skip the current record.
     */
	FastqRecordVisitor visitDefline(FastqVisitorCallback callback, String id, String optionalComment);
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
