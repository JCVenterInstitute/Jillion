package org.jcvi.jillion.internal.fasta;
/**
 * Object representation of a single
 * record in a {@code fai} index file
 * that is used by Samtools.
 * 
 * @author dkatzel
 *
 * @since 5.1
 */
public interface FastaIndex {

	FastaIndexRecord getIndexFor(String id);
}
