package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;

public class TestCoverageMapCollectorMinCoverage extends AbstractTestCoverageMapMinCoverage{

	protected CoverageMap<AssembledRead> createCoverageMap(Contig<AssembledRead> contig){
		return contig.reads()
						.collect(CoverageMapCollectors.toCoverageMap());
	}
	
	protected CoverageMap<AssembledRead> createCoverageMap(Contig<AssembledRead> contig, int max, int min){
		return contig.reads()
				.collect(CoverageMapCollectors.toCoverageMap(max, min));
	}
	
	
}
