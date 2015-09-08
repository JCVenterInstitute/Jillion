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

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Test;

public class TestCoverageMapBuilderMinCoverage {

	@Test
	public void contigCoverageAlwaysBelowThresholdShouldBeSameAsWithoutThreshold(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> unthresholded = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.build();
		CoverageMap<AssembledRead> belowThreshold = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.maxAllowedCoverage(10, 5)
														.build();
		
		assertEquals(unthresholded, belowThreshold);
		
	}
	
	@Test
	public void readEnteringIsOverMaxButProvidesRequiredDownstreamShouldStay(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 0, "ACGT")
										.addRead("read3", 0, "ACGT")
										.addRead("read4", 3,    "TAC")
										.addRead("read5", 5,      "CGT")
										.build();
		CoverageMap<AssembledRead> unthresholded = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.build();
		CoverageMap<AssembledRead> belowThreshold = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.maxAllowedCoverage(3, 2)
														.build();

		assertEquals(unthresholded, belowThreshold);
		
	}
	@Test
	public void readEnteringIsOverMaxButProvidesRequiredDownstreamShouldStay2(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 0, "ACGT")
										.addRead("read3", 0, "ACGT")
										.addRead("read4", 3,    "TAC")
										.addRead("read5", 3,    "TAC")
										.addRead("read6", 5,      "CGT")
										.build();
		
		
		Contig<AssembledRead> expectedResultContig = new DefaultContig.Builder("id", "ACGTACGT")
											.addRead("read1", 0, "ACGTACGT")
											.addRead("read2", 0, "ACGT")
											.addRead("read3", 0, "ACGT")
											.addRead("read4", 3,    "TAC")
										//	.addRead("read5", 3,    "TAC")
											.addRead("read6", 5,      "CGT")
											.build();
		
		CoverageMap<AssembledRead> expected = new ContigCoverageMapBuilder<AssembledRead>(expectedResultContig)
														.build();
		
		CoverageMap<AssembledRead> actual = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.maxAllowedCoverage(3, 2)
														.build();
		
	
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void addBackLongestReadToGetMostCoverageBack(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 0, "ACGT")
										.addRead("read3", 0, "ACGT")
										.addRead("read4", 3,    "TAC")
										.addRead("read5", 3,    "TACGT")
										.addRead("read6", 5,      "CGT")
										.build();
		
		
		Contig<AssembledRead> expectedResultContig = new DefaultContig.Builder("id", "ACGTACGT")
											.addRead("read1", 0, "ACGTACGT")
											.addRead("read2", 0, "ACGT")
											.addRead("read3", 0, "ACGT")
										//	.addRead("read4", 3,    "TAC")
											.addRead("read5", 3,    "TACGT")
											.addRead("read6", 5,      "CGT")
											.build();
		
		CoverageMap<AssembledRead> expected = new ContigCoverageMapBuilder<AssembledRead>(expectedResultContig)
														.build();
		
		CoverageMap<AssembledRead> actual = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.maxAllowedCoverage(3, 2)
														.build();
		
	
		assertEquals(expected, actual);
		
	}
	
	
	private void printCoverageMap(CoverageMap<AssembledRead> map){
		for(CoverageRegion<AssembledRead> region : map){
			System.out.println(region);
			for(AssembledRead read : region){
				System.out.printf("\t%s%n", read.getId());
			}
			
		}
	}
	
}
