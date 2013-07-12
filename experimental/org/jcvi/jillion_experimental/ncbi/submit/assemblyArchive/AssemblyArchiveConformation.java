package org.jcvi.jillion_experimental.ncbi.submit.assemblyArchive;

/**
 * {@code ContigConformation} denotes whether the contig is linear or circular.
 * @author dkatzel
 */
public enum AssemblyArchiveConformation{
	 /**
     * Contig is linear.
     */
    LINEAR,
    /**
     * Contig is circular.  Contigs that are circular use 
     * the the TIGR convention to
     * have trace coordinates which are negative to indicate they wrap around.
     */
    CIRCULAR
}
