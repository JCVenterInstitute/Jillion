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
		assertIndexesEqual(expectedIndex, actualIndex, true);
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
		boolean equalSize = expectedBins.size() == actualBins.size();
		
		if(!equalSize){
			//pretty print
			System.err.println("ref # " + refCount);
			System.err.println("bin size mismatch expected " + expectedBins.size() + " but was : "+ actualBins.size() );
			//for better error reporting check each bin 
			for(int i =0; i< expectedBins.size() && i< actualBins.size(); i++){
				if(!expectedBins.get(i).equals(actualBins.get(i))){
					System.err.println("\t bin mismatch bin #" + i + " : expected " + expectedBins.get(i) + " but was : " + actualBins.get(i) );
				}
			}
			
			if(actualBins.size() > expectedBins.size()){
				System.err.println("\t extra actual bins :");
				for(int i=expectedBins.size(); i< actualBins.size(); i++){
					System.err.println("\t\t" + actualBins.get(i));
				}
			}else{
				System.err.println("\t extra expected bins :");
				for(int i=actualBins.size(); i< expectedBins.size(); i++){
					System.err.println("\t\t" + expectedBins.get(i));
				}
			}
			
			throw new AssertionError("bin size " + refCount);
		}
		
		
		//for better error reporting check each bin 
		for(int i =0; i< expectedBins.size(); i++){
			assertEquals("refCount = " + refCount + " bin # " + i, expectedBins.get(i), actualBins.get(i));
		}
	
		
	}
}
