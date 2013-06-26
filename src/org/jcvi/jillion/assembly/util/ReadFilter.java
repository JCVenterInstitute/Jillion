/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
