package org.jcvi.common.core.assembly.util.coverage;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultContig;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
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
		
		assertEquals("ungapped length should be less than gapped length",gappedCoverageMap.getLength(), ungappedCoverageMap.getLength()+1);
		
		assertEquals("number of regions should be the same", gappedCoverageMap.getNumberOfRegions(), ungappedCoverageMap.getNumberOfRegions());
		assertEquals(2, ungappedCoverageMap.getNumberOfRegions());
		assertEquals(gappedCoverageMap.getRegion(0), ungappedCoverageMap.getRegion(0));
		CoverageRegion<AssembledRead> coverageRegion = ungappedCoverageMap.getRegion(1);
		assertEquals(Range.create(4,6), coverageRegion.asRange());
		assertEquals(2, coverageRegion.getCoverage());
		List<String> actualReads = new ArrayList<String>();
		CloseableIterator<AssembledRead> readIter = null;
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
