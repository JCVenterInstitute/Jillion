/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.index;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.internal.ArrayComparisonFailure;

public class BamIndexTestUtil {

	public BamIndexTestUtil(){
		
	}
	
	public static void printIndex(PrintWriter out, BamIndex index){
		int size = index.getNumberOfReferenceIndexes();
		out.printf("num refs = %d%n", size);
	
		for(int i=0; i< size; i++){
			ReferenceIndex refIndex =index.getReferenceIndex(i);
			List<Bin> bins =refIndex.getBins();
			out.printf("\tref %d num Bins = %d%n", i, bins.size());
			if(refIndex.hasMetaData()){
				out.printf("\toffset ranges = %s - %s #aligned=%d #unaligned = %d%n",
						refIndex.getLowestStartOffset(), refIndex.getHighestEndOffset(), 
						refIndex.getNumberOfAlignedReads(), refIndex.getNumberOfUnAlignedReads());
				
			}
			
			for(Bin bin : bins){
				out.printf("\tbin %d%n", bin.getBinNumber());
				for(Chunk chunk : bin.getChunks()){
					out.printf("\t\tchunk %s - %s%n", chunk.getBegin(), chunk.getEnd());
				}
			}
			
		}

		out.printf("total unmapped count = %d%n", index.getTotalNumberOfUnmappedReads());
		
		
		
	}
	public static void assertIndexesEqual(BamIndex expectedIndex,
			BamIndex actualIndex){
		
	}
	public static void assertIndexesEqual(BamIndex expectedIndex,
			BamIndex actualIndex, boolean ignoreMetaData){
		int size = actualIndex.getNumberOfReferenceIndexes();
		List<ReferenceIndex> indexes = new ArrayList<ReferenceIndex>(size);
		for(int i=0; i<size; i++ ){
			indexes.add(actualIndex.getReferenceIndex(i));
		}
		assertIndexesEqual(expectedIndex, indexes, ignoreMetaData);
	}
	public static void assertIndexesEqual(BamIndex expectedIndex,
			List<ReferenceIndex> actualIndex, boolean ignoreMetaData) throws ArrayComparisonFailure {
		//even though assertEquals(list, list) 
		//would work if the assertion fails, 
		//the stack trace is so big (mostly error message)
		//it causes out of mem exception
		//and takes forever to run.
		//so we break up the checks to speed things up.
		assertEquals("num indexes different",  actualIndex.size(), expectedIndex.getNumberOfReferenceIndexes());
		Iterator<ReferenceIndex> actualIter = actualIndex.iterator();

		for(int i=0 ;i < expectedIndex.getNumberOfReferenceIndexes(); i++){
			ReferenceIndex expectedRefIndex = expectedIndex.getReferenceIndex(i);
			ReferenceIndex actualRefIndex = actualIter.next();
			assertBinsMatch(expectedRefIndex, actualRefIndex, i);
			
			assertArrayEquals("intervals for " +i, expectedRefIndex.getIntervals(), actualRefIndex.getIntervals());
			if(!ignoreMetaData){
				assertEquals(expectedRefIndex.hasMetaData(), actualRefIndex.hasMetaData());
				if(expectedRefIndex.hasMetaData()){
					assertEquals(expectedRefIndex.getLowestStartOffset(), actualRefIndex.getLowestStartOffset());
					assertEquals(expectedRefIndex.getHighestEndOffset(), actualRefIndex.getHighestEndOffset());
					
					assertEquals(expectedRefIndex.getNumberOfAlignedReads(), actualRefIndex.getNumberOfAlignedReads());
					assertEquals("unaligned count for ref " + i + "aligned = " + expectedRefIndex.getNumberOfAlignedReads(), expectedRefIndex.getNumberOfUnAlignedReads(), actualRefIndex.getNumberOfUnAlignedReads());
					
					
				}
			}
		}
		assertFalse(actualIter.hasNext());
	}
	
	private static void assertBinsMatch(ReferenceIndex expected, ReferenceIndex actual, int refCount){
		List<Bin> expectedBins = expected.getBins();
		List<Bin> actualBins = actual.getBins();
		
		assertEquals("bin size " +refCount, expectedBins.size(), actualBins.size());
		assertEquals(expectedBins, actualBins);
		
	}
}
