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
import org.jcvi.jillion.assembly.util.ReadFilter;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Test;
public class TestSliceMapBuilderReadFilter {
	PhredQuality defaultQuality = PhredQuality.valueOf(20);

	@Test
	public void readFilterDoesNotFilterAnyReads(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
		.addRead("read1", 0, "ACGTACGT")
		.addRead("read2", 4, "ACGT")
		.build();
	
		SliceMap filteredSliceMap = new SliceMapBuilder<AssembledRead>(contig, defaultQuality)
											.filter(new ReadFilter<AssembledRead>() {
												
												@Override
												public boolean accept(AssembledRead read) {
													return true;
												}
											})
											.build();
		
		SliceMap unfilteredSliceMap = new SliceMapBuilder<AssembledRead>(contig, defaultQuality)
													.build();
	
		assertEquals(unfilteredSliceMap, filteredSliceMap);
	}
	
	@Test
	public void filterReads(){
		Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
		.addRead("read1", 0, "ACGTACGT")
		.addRead("read2", 4, "ACGT")
		.addRead("read3", 2, "GT")
		.build();
	
		SliceMap filteredSliceMap = new SliceMapBuilder<AssembledRead>(contig, defaultQuality)
											.filter(new ReadFilter<AssembledRead>() {
												
												@Override
												public boolean accept(AssembledRead read) {
													return read.getGappedStartOffset() < 3;
												}
											})
											.build();
		Contig<AssembledRead> contig2 = new DefaultContig.Builder("contigId", "ACGTACGT")
											.addRead("read1", 0, "ACGTACGT")
											.addRead("read3", 2, "GT")
											.build();
		SliceMap unfilteredSliceMap = new SliceMapBuilder<AssembledRead>(contig2, defaultQuality)
													.build();
	
		assertEquals(unfilteredSliceMap, filteredSliceMap);
	}
	
	
	
}
