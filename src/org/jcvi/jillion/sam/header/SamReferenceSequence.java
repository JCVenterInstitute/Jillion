package org.jcvi.jillion.sam.header;
/**
 * {@code SamReferenceSequence} is an object
 * representation of the metadata for a 
 * reference used in a SAM or BAM file.
 * 
 * @author dkatzel
 *
 */
public interface SamReferenceSequence {

    /**
     * Get the human readable name of this reference sequence.
     * @return a String; will never be null.
     */
    String getName();

    /**
     * Get the number of bases in this reference sequence.
     * @return the number of bases; will always be > 0.
     */
    int getLength();

    String getGenomeAssemblyId();

    String getSpecies();

    String getUri();

    String getMd5();

}