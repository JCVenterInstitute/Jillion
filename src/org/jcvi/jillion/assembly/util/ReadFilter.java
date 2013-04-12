package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.assembly.AssembledRead;
/**
 * A ReadFilter can be used to include/exclude
 * individual reads from a {@link CoverageMap}
 * or {@link SliceMap}.
 * @author dkatzel
 *
 * @param <R> the {@link AssembledRead} type in the Contig
 * that will be used to back the {@link CoverageMap} should be the
 * same type as the CoverageMap will use. 
 */
public  interface ReadFilter<R extends AssembledRead>{
	/**
	 * Should this read be included in the
	 * {@link CoverageMap}.
	 * @param read the read to check;
	 * will never be null.
	 * @return {@code true} if this read
	 * should be included in the coverageMap;
	 * {@code false} otherwise.
	 */
	boolean accept(R read);
}