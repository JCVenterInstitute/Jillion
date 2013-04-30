package org.jcvi.jillion.assembly.util;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code ContigCoverageMapBuilder} will build
 * a {@link CoverageMap} where each {@link CoverageRegion}
 * will contain {@link AssembledRead}s.
 * 
 * The Builder has optional methods for filtering
 * what reads get included in the CoverageMap to be built.
 * Once all the options have been set, {@link #build()}
 * will construct the actual {@link CoverageMap} instance.
 * 
 * @author dkatzel
 *
 * @param <R> the {@link AssembledRead} type in the Contig
 * that will be used to back the {@link CoverageMap}.
 */
public final class ContigCoverageMapBuilder<R extends AssembledRead> {

	
	private static final int NOT_SET = -1;
	private final Contig<R> contig;
	
	private ReadFilter<? super R> filter = null;
	
	private int maxCoverage=NOT_SET;
	
	private boolean useUngappedCoordinates=false;
	

	
	/**
	 * Create a new Builder instance that will
	 * use the given contig to eventually
	 * build a {@link CoverageMap}.
	 * @param contig the contig to use;
	 * should not be null.
	 * @throws NullPointerException if contig is null.
	 */
	public ContigCoverageMapBuilder(Contig<R> contig){
		if(contig ==null){
			throw new NullPointerException("contig can not be null");
		}
		this.contig = contig;
	}
	/**
	 * Set the maximum coverage depth any {@link CoverageRegion}
	 * in the resulting CoverageMap will have,
	 * any {@link AssembledRead} in the {@link Contig}
	 *  that provides additional coverage than
	 * the specified max will not be included in the CoverageMap. 
	 * This exclusion is performed AFTER any read filtering
	 * performed by the filter specified by
	 * {@link #filter(ReadFilter)}.  So it is possible for 
	 * a {@link ReadFilter} to accept a read and still have that
	 * read excluded due to the maxCoverage threshold.
	 * <p/>
	 * This exclusion is performed by computing
	 * read arrival and departure values so the first reads
	 * providing coverage by start coordinate will be included while
	 * the "maxCoverage-th" read will be excluded:
	 * 
	 * In the following diagram, if maxCoverage is set to 4,
	 * then read7 will be excluded because it causes 5x coverage
	 * at some of its offsets.
	 * <pre>
	 * ---------------------------
	 * 1x|	=read1=   =read5=
	 * 2x|	  =read2=    =read6=
	 * 3x|	  =read3=     =read8=
	 * 4x|	    =read4=   
	 * 5x|	     =read7=
	 * </pre>
	 * @param maxCoverage the maxCoverage any {@link CoverageRegion}
	 * will be allowed to have; must be >=0.
	 * @return this
	 */
	public ContigCoverageMapBuilder<R> maxAllowedCoverage(int maxCoverage){
		if(maxCoverage<0){
			throw new IllegalArgumentException("maxCoverage must be positive");
		}
		this.maxCoverage = maxCoverage;
		return this;
	}
	/**
	 * Apply the given {@link ReadFilter} to each {@link AssembledRead}
	 * in the contig, only reads that are accepted by the filter
	 * will be considered when building the {@link CoverageMap}.
	 * The filter will be called by the {@link #build()}
	 * but before any reads are excluded by the max coverage threshold.
	 * @param filter the {@link ReadFilter} to use, can not be null;
	 * if no filter is required, do not call this method.
	 * @return this
	 * @see #maxAllowedCoverage(int)
	 * @throws NullPointerException if filter is null.
	 */
	public ContigCoverageMapBuilder<R> filter(ReadFilter<? super R> filter){
		if(filter==null){
			throw new IllegalArgumentException("filter can not be null");
		}
		this.filter = filter;
		return this;
	}
	/**
	 * Calling this method will force the resulting CoverageMap
	 * to use each read's ungapped consensus start and end 
	 * values instead of the read's gapped consensus start and end values.
	 * Coordinates are only determined by the consensus gapped vs ungapped coordiantes
	 * so if a read has it's own gaps but the corresponding consensus offset is not a 
	 * gap then the coordinate is unchanged.  Conversely, if the read has a non-gap,
	 * but the consensus is a gap, then the coverageRegion will be adjusted.
	 * @return this.
	 */
	public ContigCoverageMapBuilder<R> useUngappedCoordinates(){
		useUngappedCoordinates = true;
		return this;
	}
	/**
	 * Create a new {@link CoverageMap} instance using the contig provided
	 * by this constructor and filtered by any options set.
	 * @return a new {@link CoverageMap}; will never be null
	 * but may be empty.
	 */
	public CoverageMap<R> build(){
		StreamingIterator<R> iter = null;
		List<R> readsToInclude = new ArrayList<R>(MapUtil.computeMinHashMapSizeWithoutRehashing(contig.getNumberOfReads()));
		try{
			iter = contig.getReadIterator();
			while(iter.hasNext()){
				R read = iter.next();
				if(filter==null || filter.accept(read)){
					readsToInclude.add(read);
				}
			}
			final CoverageMap<R> gappedCoverageMap;
			if(maxCoverage == NOT_SET){
				gappedCoverageMap = CoverageMapFactory.create(readsToInclude);
			}else{
				gappedCoverageMap = CoverageMapFactory.create(readsToInclude, maxCoverage);
			}
			if(useUngappedCoordinates){
				return CoverageMapFactory.createUngappedCoverageMap(contig.getConsensusSequence(), gappedCoverageMap);
			}else{
				return gappedCoverageMap;
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
	
}
