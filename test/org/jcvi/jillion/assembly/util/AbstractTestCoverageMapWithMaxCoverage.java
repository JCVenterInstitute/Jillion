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
public abstract class AbstractTestCoverageMapWithMaxCoverage {

	protected abstract CoverageMap<AssembledRead> createCoverageMap(Contig<AssembledRead> contig);
	
	protected abstract CoverageMap<AssembledRead> createCoverageMap(Contig<AssembledRead> contig, int maxAllowedCoverage);
	@Test
	public void contigCoverageAlwaysBelowThresholdShouldBeSameAsWithoutThreshold(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> unthresholded = new ContigCoverageMapBuilder<AssembledRead>(contig)
														.build();
		CoverageMap<AssembledRead> belowThreshold = createCoverageMap(contig, 10);
		
		assertEquals(unthresholded, belowThreshold);
		
	}
	@Test
	public void contigCoverageEqualToThresholdShouldBeSameAsWithoutThreshold(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> unthresholded = new ContigCoverageMapBuilder<AssembledRead>(contig).build();
		CoverageMap<AssembledRead> atThreshold = createCoverageMap(contig, 2);
		
		assertEquals(unthresholded, atThreshold);		
	}
	
	@Test
	public void contigCoverageAboveThresholdShouldIgnoreReadsThatGoOver(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("id", "ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 4, "ACGT")
										.addRead("read3", 4, "ACGT")
										.build();
		CoverageMap<AssembledRead> restrictedCoverage = createCoverageMap(contig, 2);
		
		Contig<AssembledRead> reducedCoverageContig = new DefaultContig.Builder(contig)
															.removeRead("read3")
															.build();
		assertEquals(createCoverageMap(reducedCoverageContig),
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
		CoverageMap<AssembledRead> restrictedCoverage = createCoverageMap(contig, 2);
		
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
		CoverageMap<AssembledRead> restrictedCoverage = createCoverageMap(contig, 2);
		
		Contig<AssembledRead> reducedCoverageContig = new DefaultContig.Builder(contig)
															.removeRead("read3")
															.removeRead("read5")
															.build();
		assertEquals(new ContigCoverageMapBuilder<AssembledRead>(reducedCoverageContig).build(),
							restrictedCoverage );
	}
}
