/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.util.ContigCoverageMapBuilder;
import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Test;
public class TestCoverageMapBuilderMaxCoverage {

	@Test
	public void contigCoverageAlwaysBelowThresholdShouldBeSameAsWithoutThreshold(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> unthresholded = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.build();
		CoverageMap<AssembledRead> belowThreshold = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.maxAllowedCoverage(10)
														.build();
		
		assertEquals(unthresholded, belowThreshold);
		
	}
	@Test
	public void contigCoverageEqualToThresholdShouldBeSameAsWithoutThreshold(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> unthresholded = new ContigCoverageMapBuilder<AssembledRead>(contig).build();
		CoverageMap<AssembledRead> atThreshold = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.maxAllowedCoverage(2)
														.build();
		
		assertEquals(unthresholded, atThreshold);		
	}
	
	@Test
	public void contigCoverageAboveThresholdShouldIgnoreReadsThatGoOver(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.addRead("read3", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> restrictedCoverage = new ContigCoverageMapBuilder<AssembledRead>(contig)
															.maxAllowedCoverage(2)
															.build();
		
		Contig<AssembledRead> reducedCoverageContig = new DefaultContig.Builder(contig)
															.removeRead("read3")
															.build();
		assertEquals(new ContigCoverageMapBuilder<AssembledRead>(reducedCoverageContig).build(),
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
		CoverageMap<AssembledRead> restrictedCoverage = new ContigCoverageMapBuilder<AssembledRead>(contig)
																.maxAllowedCoverage(2)
																.build();
		
		Contig<AssembledRead> reducedCoverageContig = new DefaultContig.Builder(contig)
															.removeRead("read3")
															.removeRead("read4")
															.removeRead("read5")
															.removeRead("read6")
															.build();
		assertEquals(new ContigCoverageMapBuilder<AssembledRead>(reducedCoverageContig).build(),
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
		CoverageMap<AssembledRead> restrictedCoverage = new ContigCoverageMapBuilder<AssembledRead>(contig)
															.maxAllowedCoverage(2)
															.build();
		
		Contig<AssembledRead> reducedCoverageContig = new DefaultContig.Builder(contig)
															.removeRead("read3")
															.removeRead("read5")
															.build();
		assertEquals(new ContigCoverageMapBuilder<AssembledRead>(reducedCoverageContig).build(),
							restrictedCoverage );
	}
}
