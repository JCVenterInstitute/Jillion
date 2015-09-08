/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import java.util.Collection;

import org.jcvi.jillion.core.Rangeable;
/**
 * {@code CoverageMapBuilder} will build
 * a {@link CoverageMap} of the given elements.
 * 
 * The Builder has optional methods for filtering
 * what reads get included in the CoverageMap to be built.
 * Once all the options have been set, {@link #build()}
 * will construct the actual {@link CoverageMap} instance.
 * 
 * @author dkatzel
 *
 * @param <T> the {@link Rangeable} type
 * that will be used to back the {@link CoverageMap}.
 */
public final class CoverageMapBuilder<T extends Rangeable> {

	private static final int NOT_SET = -1;
	private final Collection<T> elements;
	private int maxCoverage=NOT_SET;
	private int minCoverage=NOT_SET;
			
	private boolean startAtOrigin=false;
	/**
	 * Create a new Builder instance that will
	 * use the given elements to eventually
	 * build a {@link CoverageMap}.
	 * @param elements the elements to use;
	 * should not be null.
	 * @throws NullPointerException if elements is null.
	 */
	public CoverageMapBuilder(Collection<T> elements){
		if(elements ==null){
			throw new NullPointerException("elements can not be null");
		}
		this.elements = elements;
	}
	/**
	 * Set the maximum coverage depth any {@link CoverageRegion}
	 * in the resulting CoverageMap will have,
	 * any elements
	 *  that provide additional coverage than
	 * the specified max will not be included in the CoverageMap. 
	 * <p/>
	 * This exclusion is performed by computing
	 * element arrival and departure values so the first elements
	 * providing coverage by start coordinate will be included while
	 * the "maxCoverage-th" read will be excluded:
	 * 
	 * In the following diagram, if maxCoverage is set to 4,
	 * then element7 will be excluded because it causes 5x coverage
	 * at some of its offsets.
	 * <pre>
	 * ---------------------------
	 * 1x|	=element1=   =element5=
	 * 2x|	  =element2=    =element6=
	 * 3x|	  =element3=     =element8=
	 * 4x|	    =element4=   
	 * 5x|	     =element7=
	 * </pre>
	 * @param maxCoverage the maxCoverage any {@link CoverageRegion}
	 * will be allowed to have; must be >=0.
	 * @return this
	 */
	public CoverageMapBuilder<T> maxAllowedCoverage(int maxCoverage){
		if(maxCoverage<0){
			throw new IllegalArgumentException("maxCoverage must be positive");
		}
		this.maxCoverage = maxCoverage;
		return this;
	}
	
	
	/**
	 * Set the PREFERRED maximum coverage depth any {@link CoverageRegion}
	 * in the resulting CoverageMap will have.
	 * Any {@link AssembledRead} in the {@link Contig}
	 *  that provides additional coverage than
	 * the specified max will not be included in the CoverageMap
	 * EXCEPT if there are other nearby {@link CoverageRegion}s
	 * whose coverage depth will be below the specified
	 * {@code requiredMinCoverage}. 
	 * <p/>
	 * This exclusion is performed by computing
	 * element arrival and departure values so the first elements
	 * providing coverage by start coordinate will be included while
	 * the "maxCoverage-th" element will be excluded:
	 * 
	 * In the following diagram, if maxCoverage is set to 4
	 * and {@code requiredMinCoverage} is set to 2,
	 * then element7 will still be included because
	 * even though it causes 5x coverage
	 * at some of its offsets, the end of the read 
	 * provides 2x coverage at that position so the read must stay.
	 * <pre>
	 * ---------------------------
	 * 1x|	=element1=   =element5=
	 * 2x|	  =element2=    =element6=
	 * 3x|	  =element3=     =element8=
	 * 4x|	    =element4=   
	 * 5x|	     =element7=
	 * </pre>
	 * @param preferredMaxCoverage the maximum coverage any {@link CoverageRegion}
	 * will strive to have; must be >=0.
	 * @param requiredMinCoverage the minimum coverage any 
	 * {@link CoverageRegion} must have even at the expense of exceeding the 
	 * preferredMaxCoverage; must be >=0.
	 * @return this
	 */
	public CoverageMapBuilder<T> maxAllowedCoverage(int preferredMaxCoverage, int requiredMinCoverage) {
		if(preferredMaxCoverage<0){
			throw new IllegalArgumentException("maxCoverage must be positive");
		}
		if(requiredMinCoverage<0){
			throw new IllegalArgumentException("requiredMinCoverage must be positive");
		}
		this.maxCoverage = preferredMaxCoverage;
		this.minCoverage = requiredMinCoverage;
		
		return this;
	}
	/**
	 * Create a new {@link CoverageMap} instance using the contig provided
	 * by this constructor and filtered by any options set.
	 * @return a new {@link CoverageMap}; will never be null
	 * but may be empty.
	 */
	public CoverageMap<T> build(){
		if(maxCoverage == NOT_SET){
			return CoverageMapFactory.create(elements,startAtOrigin);
		}
		if(minCoverage == NOT_SET){
			return CoverageMapFactory.create(elements, maxCoverage,startAtOrigin);
		}
		return CoverageMapFactory.create(elements, maxCoverage,minCoverage, startAtOrigin);
		
	}
	/**
	 * Forces a {@link CoverageRegion} to cover
	 * the origin (offset 0)
	 * in the coverage map.
	 * If this is set to {@code true} then the first {@link CoverageRegion}
	 * of the built {@link CoverageMap} will always have a CoverageRegion that
	 * will cover offset 0. If there is no coverage at offset zero,
	 * then a CoverageRegion that has {@link CoverageRegion#getCoverageDepth()} ==0 
	 * (a 0x region) will be inserted into the map
	 * even if it that makes it the first or last region in the map.
	 * <p/>
	 * If not set, then defaults to {@code false}.
	 * @param flag {@code true} to include the origin;
	 * {@code false} otherwise.
	 * @return this
	 */
	public CoverageMapBuilder<T> includeOrigin(boolean flag) {
		this.startAtOrigin = flag;
		return this;
	}
}
