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
package org.jcvi.jillion.assembly.util;

import java.util.function.Predicate;

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
public  interface ReadFilter<R extends AssembledRead> extends Predicate<R>{
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
	
	
	default boolean test(R read){
		return accept(read);
	}
}
