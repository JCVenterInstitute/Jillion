/*
 * Created on Sep 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assemblyArchive;
/**
 * {@code ContigConformation} denotes whether the contig is linear or circular.
 * @author dkatzel
 *
 *
 */
public enum ContigConformation {
    /**
     * Contig is linear.
     */
    LINEAR,
    /**
     * Contig is circular.  Contigs that are circular should
     * have trace coordinates which are negative to indicate they wrap around.
     */
    CIRCULAR
}
