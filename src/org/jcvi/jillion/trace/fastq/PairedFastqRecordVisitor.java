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

import org.jcvi.jillion.core.qual.QualitySequence;
/**
 * {@code PairedFastqRecordVisitor} is a visitor
 * interface to visit the two mate paired fastq records
 * from paired fastq files.
 * @author dkatzel
 * 
 * @since 6.0.2
 *
 */
public interface PairedFastqRecordVisitor {

    /**
     * Visit the nucleotide sequence of Read 1 
     * fastq record as a String.
     * @param nucleotides the {@link NucleotideSequence};
     * will never be null.
     */
    void visitRead1Nucleotides(String nucleotides);
    /**
     * Visit the nucleotide sequence of Read 2
     * fastq record as a String.
     * @param nucleotides the {@link NucleotideSequence};
     * will never be null.
     */
    void visitRead2Nucleotides(String nucleotides);
    /**
     * Visit the encoded quality values for the Read 1
     * fastq record.  If the fastq file breaks the quality values
     * across multiple lines, then {@code  encodedQualities}
     * will be the concatenation of all of those lines with all
     * whitespace removed.
     * This method will not be called if
     * {@link #visitQualities(QualitySequence)}
     * is used instead.
     * @param encodedQualities the encoded quality values as a single line string;
     * will never be null.
     * @see FastqQualityCodec
     */
    void visitEncodedRead1Qualities(String encodedQualities);
    /**
     * Visit the the actual quality values
     * (not encoded) for the current
     * fastq record. This method will only be called
     * if the fastq implementation does not encode the quality values.
     * This method is only called
     * on specific fastq formatted files
     * (for example MAQ encoded bfq files) 
     * and method will only be called if {@link #visitEncodedQualities(String)}
     * is NOT called.
     * @param qualities the actual non-encoded quality values;
     * will never be null.
     * @see #visitEncodedQualities(String)
     */
    void visitRead1Qualities(QualitySequence qualities);
    /**
     * Visit the encoded quality values for the Read 1
     * fastq record.  If the fastq file breaks the quality values
     * across multiple lines, then {@code  encodedQualities}
     * will be the concatenation of all of those lines with all
     * whitespace removed.
     * This method will not be called if
     * {@link #visitQualities(QualitySequence)}
     * is used instead.
     * @param encodedQualities the encoded quality values as a single line string;
     * will never be null.
     * @see FastqQualityCodec
     */
    void visitEncodedRead2Qualities(String encodedQualities);
    /**
     * Visit the the actual quality values
     * (not encoded) for the current
     * fastq record. This method will only be called
     * if the fastq implementation does not encode the quality values.
     * This method is only called
     * on specific fastq formatted files
     * (for example MAQ encoded bfq files) 
     * and method will only be called if {@link #visitEncodedQualities(String)}
     * is NOT called.
     * @param qualities the actual non-encoded quality values;
     * will never be null.
     * @see #visitEncodedQualities(String)
     */
    void visitRead2Qualities(QualitySequence qualities);
    /**
	 * Visit the end of the 
	 * current fastq record.
	 */
    void visitEnd();
    
    /**
     * The parser has stopped parsing the 
     * current fastq record
     * due to {@link PairedFastqVisitorCallback#haltParsing()}
     * being called. The end of the fastq record was
     * not yet reached.
     */
    void halted();
    
	
    
    
}
