package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
/**
 * {@code FastqRecordVisitor} is a visitor
 * interface to visit a single fastq record
 * inside of a fastq file.
 * @author dkatzel
 *
 */
public interface FastqRecordVisitor {

	/**
     * Visit the {@link NucleotideSequence} of the current 
     * fastq record.
     * @param nucleotides the {@link NucleotideSequence};
     * will never be null.
     */
    void visitNucleotides(NucleotideSequence nucleotides);
    /**
     * Visit the encoded quality values for the current
     * fastq record.  If the fastq file breaks the quality values
     * across multiple lines, then {@code  encodedQualities}
     * will be the concatenation of all of those lines with all
     * whitespace removed.
     * @param encodedQualities the encoded quality values as a single line string;
     * will never be null.
     * @see FastqQualityCodec
     */
    void visitEncodedQualities(String encodedQualities);
    /**
	 * Visit the end of the 
	 * current fastq record.
	 */
    void visitEnd();
    
    /**
     * The parser has stopped parsing the 
     * current fastq record
     * due to {@link FastqVisitorCallback#haltParsing()}
     * being called. The end of the fastq record was
     * not yet reached.
     */
    void halted();
    
    
}
