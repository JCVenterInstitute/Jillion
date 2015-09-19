/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Test;

public abstract class AbstractTestCoverageMapMinCoverage {

	protected abstract CoverageMap<AssembledRead> createCoverageMap(Contig<AssembledRead> contig);
	
	protected abstract CoverageMap<AssembledRead> createCoverageMap(Contig<AssembledRead> contig, int max, int min);
	
	@Test
	public void contigCoverageAlwaysBelowThresholdShouldBeSameAsWithoutThreshold(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> unthresholded = createCoverageMap(contig);
		CoverageMap<AssembledRead> belowThreshold = createCoverageMap(contig, 10, 5);
		
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
		CoverageMap<AssembledRead> unthresholded = createCoverageMap(contig);
		CoverageMap<AssembledRead> belowThreshold = createCoverageMap(contig, 3, 2);

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
		
		CoverageMap<AssembledRead> expected = createCoverageMap(expectedResultContig);
		
		CoverageMap<AssembledRead> actual = createCoverageMap(contig, 3, 2);
		
	
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
		
		CoverageMap<AssembledRead> expected = createCoverageMap(expectedResultContig);
		
		CoverageMap<AssembledRead> actual = createCoverageMap(contig, 3, 2);
		
	
		assertEquals(expected, actual);
		
	}
	
	
	
	
}
