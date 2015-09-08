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
