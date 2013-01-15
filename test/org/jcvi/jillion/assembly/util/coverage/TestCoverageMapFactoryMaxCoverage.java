package org.jcvi.jillion.assembly.util.coverage;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.DefaultContig;
import org.jcvi.jillion.assembly.util.coverage.CoverageMap;
import org.jcvi.jillion.assembly.util.coverage.CoverageMapFactory;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCoverageMapFactoryMaxCoverage {

	@Test
	public void contigCoverageAlwaysBelowThresholdShouldBeSameAsWithoutThreshold(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> unthresholded = CoverageMapFactory.createGappedCoverageMapFromContig(contig);
		CoverageMap<AssembledRead> belowThreshold = CoverageMapFactory.createGappedCoverageMapFromContig(contig, 10);
		
		assertEquals(unthresholded, belowThreshold);
		
	}
	@Test
	public void contigCoverageEqualToThresholdShouldBeSameAsWithoutThreshold(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> unthresholded = CoverageMapFactory.createGappedCoverageMapFromContig(contig);
		CoverageMap<AssembledRead> atThreshold = CoverageMapFactory.createGappedCoverageMapFromContig(contig, 2);
		
		assertEquals(unthresholded, atThreshold);		
	}
	
	@Test
	public void contigCoverageAboveThresholdShouldIgnoreReadsThatGoOver(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.addRead("read3", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> restrictedCoverage = CoverageMapFactory.createGappedCoverageMapFromContig(contig, 2);
		
		Contig<AssembledRead> reducedCoverageContig = new DefaultContig.Builder(contig)
															.removeRead("read3")
															.build();
		assertEquals(CoverageMapFactory.createGappedCoverageMapFromContig(reducedCoverageContig),
							restrictedCoverage );
	}
	@Test
	public void contigCoverageAboveThresholdInStackShouldIgnoreReadsThatGoOver(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.addRead("read3", 4, "ACGT")
										.addRead("read4", 4, "ACGT")
										.addRead("read5", 4, "ACGT")
										.addRead("read6", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> restrictedCoverage = CoverageMapFactory.createGappedCoverageMapFromContig(contig, 2);
		
		Contig<AssembledRead> reducedCoverageContig = new DefaultContig.Builder(contig)
															.removeRead("read3")
															.removeRead("read4")
															.removeRead("read5")
															.removeRead("read6")
															.build();
		assertEquals(CoverageMapFactory.createGappedCoverageMapFromContig(reducedCoverageContig),
							restrictedCoverage );
	}
	@Test
	public void contigCoverageAboveThresholdInStariStepShouldIgnoreReadsThatGoOver(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.addRead("read3", 4, "ACGT")
										.addRead("read4", 0, "ACGT")
										.addRead("read5", 6, "GT")
										.build();
		CoverageMap<AssembledRead> restrictedCoverage = CoverageMapFactory.createGappedCoverageMapFromContig(contig, 2);
		
		Contig<AssembledRead> reducedCoverageContig = new DefaultContig.Builder(contig)
															.removeRead("read3")
															.removeRead("read5")
															.build();
		assertEquals(CoverageMapFactory.createGappedCoverageMapFromContig(reducedCoverageContig),
							restrictedCoverage );
	}
}
