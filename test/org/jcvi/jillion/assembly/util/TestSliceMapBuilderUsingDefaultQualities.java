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
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Test;

public class TestSliceMapBuilderUsingDefaultQualities {

	
	@Test
	public void allQualitiesAreDefaultValue(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
												.addRead("read1", 0, "ACGTACGT")
												.addRead("read1", 4, "ACGT")
												.build();
		PhredQuality defaultQuality = PhredQuality.valueOf(20);
		
		SliceMap sliceMap = new SliceMapBuilder<AssembledRead>(contig, defaultQuality)
													.build();
		assertEquals(contig.getConsensusSequence().getLength(), sliceMap.getSize());
		assertAllSliceElementsHaveDefaultQuality(sliceMap, defaultQuality);
	}

	private void assertAllSliceElementsHaveDefaultQuality(SliceMap sliceMap,
			PhredQuality defaultQuality) {
		for(Slice slice : sliceMap){
			for(SliceElement element: slice){
				assertEquals(defaultQuality, element.getQuality());
			}
		}
		
	}
}
