package org.jcvi.jillion.assembly.util.slice;

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
