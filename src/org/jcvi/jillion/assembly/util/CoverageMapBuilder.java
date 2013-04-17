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
	 * read arrival and departure values so the first reads
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
	 * Create a new {@link CoverageMap} instance using the contig provided
	 * by this constructor and filtered by any options set.
	 * @return a new {@link CoverageMap}; will never be null
	 * but may be empty.
	 */
	public CoverageMap<T> build(){
		if(maxCoverage == NOT_SET){
			return CoverageMapFactory.create(elements);
		}
		return CoverageMapFactory.create(elements, maxCoverage);
	}
}
