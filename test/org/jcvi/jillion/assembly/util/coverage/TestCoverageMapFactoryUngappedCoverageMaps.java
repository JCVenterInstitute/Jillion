package org.jcvi.jillion.assembly.util.coverage;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.DefaultContig;
import org.jcvi.jillion.assembly.util.coverage.CoverageMap;
import org.jcvi.jillion.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.jillion.assembly.util.coverage.CoverageMapUtil;
import org.jcvi.jillion.assembly.util.coverage.CoverageRegion;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestCoverageMapFactoryUngappedCoverageMaps {

	@Test
	public void noGapsShouldSameReturnSameAsGappedCoverageMap(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		
		CoverageMap<AssembledRead> gappedCoverageMap = CoverageMapFactory.createGappedCoverageMapFromContig(contig);
		CoverageMap<AssembledRead> ungappedCoverageMap = CoverageMapFactory.createUngappedCoverageMapFromContig(contig);
		
		assertEquals(gappedCoverageMap, ungappedCoverageMap);
	}
	
	@Test
	public void oneGapInConsensus(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTAC-T")
											.addRead("read1", 0, "ACGTACGT")
											.addRead("read2", 4, "AC-T")
											.build();

		CoverageMap<AssembledRead> gappedCoverageMap = CoverageMapFactory.createGappedCoverageMapFromContig(contig);
		CoverageMap<AssembledRead> ungappedCoverageMap = CoverageMapFactory.createUngappedCoverageMapFromContig(contig);
		
		assertEquals("ungapped length should be less than gapped length",
				CoverageMapUtil.getLastCoveredOffsetIn(gappedCoverageMap), 
				CoverageMapUtil.getLastCoveredOffsetIn(ungappedCoverageMap)+1);
		
		assertEquals("number of regions should be the same", gappedCoverageMap.getNumberOfRegions(), ungappedCoverageMap.getNumberOfRegions());
		assertEquals(2, ungappedCoverageMap.getNumberOfRegions());
		assertEquals(gappedCoverageMap.getRegion(0), ungappedCoverageMap.getRegion(0));
		CoverageRegion<AssembledRead> coverageRegion = ungappedCoverageMap.getRegion(1);
		assertEquals(Range.of(4,6), coverageRegion.asRange());
		assertEquals(2, coverageRegion.getCoverageDepth());
		List<String> actualReads = new ArrayList<String>();
		StreamingIterator<AssembledRead> readIter = null;
		try{
			readIter = coverageRegion.getElementIterator();
			coverageRegion.getElementIterator();
			while(readIter.hasNext()){
				actualReads.add(readIter.next().getId());
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(readIter);
		}
		assertTrue(actualReads.contains("read1"));
		assertTrue(actualReads.contains("read2"));
	
	}
}