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
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.assembly.util.SliceBuilder.SliceElementFilter;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestSliceBuilder {

	private final List<SliceElement> elements = Arrays.<SliceElement>asList(
			new DefaultSliceElement("id",
								Nucleotide.Adenine, 
								PhredQuality.valueOf(30), 
								Direction.FORWARD),
			new DefaultSliceElement("id2",
					Nucleotide.Adenine, 
					PhredQuality.valueOf(20), 
					Direction.REVERSE),
			new DefaultSliceElement("id3",
							Nucleotide.Guanine, 
							PhredQuality.valueOf(50), 
							Direction.REVERSE)
			);
	
	private final Nucleotide consensus = Nucleotide.Adenine;
	
	
	private Map<Nucleotide, Integer> createEmptyCountMap(){
		EnumMap<Nucleotide, Integer> map = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
		for(Nucleotide n : Nucleotide.VALUES){
			map.put(n, Integer.valueOf(0));
		}
		return map;
	}
	@Test
	public void emptySlice(){
		Slice slice = new SliceBuilder().build();
		
		assertEquals(0, slice.getCoverageDepth());
		assertFalse(slice.iterator().hasNext());
		assertEquals(createEmptyCountMap(), slice.getNucleotideCounts());
		assertNull(slice.getSliceElement("does not exist"));
	}
	
	@Test(expected = NullPointerException.class)
	public void copyConstructorWithNullShouldThrowNPE(){
		new SliceBuilder((Slice)null);
	}
	
	@Test(expected = NullPointerException.class)
	public void iterableConstructorWithNullShouldThrowNPE(){
		new SliceBuilder((Iterable<SliceElement>)null);
	}
	
	@Test
	public void contains(){
		SliceBuilder builder = new SliceBuilder();
		assertFalse(builder.containsId("id"));
		builder.add("id", Nucleotide.Adenine, 
													PhredQuality.valueOf(30), 
													Direction.FORWARD);
		assertTrue(builder.containsId("id"));
	}
	
	@Test(expected = NullPointerException.class)
	public void containsNullShouldThrowNPE(){
		new SliceBuilder().containsId(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void addNullElementShouldThrowNPE(){
		new SliceBuilder().add(null);
	}
	
	@Test
	public void addSliceElement(){
		SliceElement element = elements.get(0);
		
		Slice slice= new SliceBuilder()
						.add(element)
						.build();

		assertEquals(1, slice.getCoverageDepth());
		assertTrue(slice.containsElement(element.getId()));
		assertEquals(element, slice.getSliceElement(element.getId()));
		
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 1);
		
		assertEquals(expectedCountMap, slice.getNucleotideCounts());
		assertNull(slice.getConsensusCall());
	}
	
	@Test
	public void addingSliceElementIdShouldOverwriteExistingValue(){
		SliceElement element = elements.get(0);
		
		SliceElement replacementElement = new DefaultSliceElement(element.getId(), Nucleotide.Unknown, PhredQuality.valueOf(0), Direction.REVERSE);
		Slice slice = new SliceBuilder()
						.add(element)
						.add(replacementElement)
						.build();
		
		assertEquals(1, slice.getCoverageDepth());
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Unknown, 1);
		
		assertEquals(expectedCountMap, slice.getNucleotideCounts());
		assertNull(slice.getConsensusCall());
		
	}
	
	@Test
	public void addMultipleSliceElementsOneAtATime(){
		
		
		SliceBuilder builder= new SliceBuilder();
		
		for(SliceElement element : elements){
			builder.add(element);
		}
		Slice slice = builder.build();

		assertEquals(3, slice.getCoverageDepth());
		for(SliceElement element : elements){
			assertTrue(slice.containsElement(element.getId()));
			assertEquals(element, slice.getSliceElement(element.getId()));
		}
		
		
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 2);
		expectedCountMap.put(Nucleotide.Guanine, 1);
		
		assertEquals(expectedCountMap, slice.getNucleotideCounts());
		assertNull(slice.getConsensusCall());
	}
	
	@Test
	public void addMultipleSliceElementsAllAtOnce(){
		List<SliceElement> shuffledList = new ArrayList<SliceElement>(elements);
		Collections.shuffle(shuffledList);
		Slice shuffeledSlice = new SliceBuilder()
									.addAll(shuffledList)
									.build();

		Slice unshuffeledSlice = new SliceBuilder()
									.addAll(elements)
									.build();
		assertEquals(unshuffeledSlice, shuffeledSlice);
		assertNull(shuffeledSlice.getConsensusCall());
		assertNull(unshuffeledSlice.getConsensusCall());
		
	}
	
	@Test
	public void slicesAddedInDifferentOrderShouldStillBeEqual(){
		
		
		SliceBuilder builder= new SliceBuilder();
		
		builder.addAll(elements);

		Slice slice = builder.build();

		assertEquals(3, slice.getCoverageDepth());
		for(SliceElement element : elements){
			assertTrue(slice.containsElement(element.getId()));
			assertEquals(element, slice.getSliceElement(element.getId()));
		}
		
		
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 2);
		expectedCountMap.put(Nucleotide.Guanine, 1);
		
		assertEquals(expectedCountMap, slice.getNucleotideCounts());
		assertNull(slice.getConsensusCall());
		
	}
	
	@Test
	public void removeNonExistentElementShouldDoNothing(){
		
		
		SliceBuilder builder= new SliceBuilder(elements);
		
		builder.removeById("does not exist");

		Slice slice = builder.build();

		assertEquals(3, slice.getCoverageDepth());
		for(SliceElement element : elements){
			assertTrue(slice.containsElement(element.getId()));
			assertEquals(element, slice.getSliceElement(element.getId()));
		}
		
		
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 2);
		expectedCountMap.put(Nucleotide.Guanine, 1);
		
		assertEquals(expectedCountMap, slice.getNucleotideCounts());
		assertNull(slice.getConsensusCall());
	}
	
	@Test
	public void removeSliceElement(){
		
		
		SliceBuilder builder= new SliceBuilder();
		
		for(SliceElement element : elements){
			builder.add(element);
		}
		
		builder.removeById("id2");
		Slice slice = builder.build();

		assertEquals(2, slice.getCoverageDepth());
		for(SliceElement element : elements){
			if("id2".equals(element.getId())){
				assertFalse(slice.containsElement(element.getId()));
				assertNull(slice.getSliceElement(element.getId()));
			}else{
				assertTrue(slice.containsElement(element.getId()));
				assertEquals(element, slice.getSliceElement(element.getId()));
			}
		}
		
		
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 1);
		expectedCountMap.put(Nucleotide.Guanine, 1);
		
		assertEquals(expectedCountMap, slice.getNucleotideCounts());
		assertNull(slice.getConsensusCall());
	}
	
	@Test
	public void copy(){
		
		SliceBuilder builder= new SliceBuilder();
		builder.addAll(elements);
		SliceBuilder copy =builder.copy();
		copy.removeById("id2");
		
		Slice modifiedSlice = copy.build();
		assertEquals(2, modifiedSlice.getCoverageDepth());
		for(SliceElement element : elements){
			if("id2".equals(element.getId())){
				assertFalse(modifiedSlice.containsElement(element.getId()));
				assertNull(modifiedSlice.getSliceElement(element.getId()));
			}else{
				assertTrue(modifiedSlice.containsElement(element.getId()));
				assertEquals(element, modifiedSlice.getSliceElement(element.getId()));
			}
		}
		
		
		Map<Nucleotide, Integer> expectedModifiedCountMap = createEmptyCountMap();
		expectedModifiedCountMap.put(Nucleotide.Adenine, 1);
		expectedModifiedCountMap.put(Nucleotide.Guanine, 1);
		
		assertEquals(expectedModifiedCountMap, modifiedSlice.getNucleotideCounts());
		
		Slice slice = builder.build();
		
		assertEquals(3, slice.getCoverageDepth());
		for(SliceElement element : elements){
			assertTrue(slice.containsElement(element.getId()));
			assertEquals(element, slice.getSliceElement(element.getId()));
		}
		
		
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 2);
		expectedCountMap.put(Nucleotide.Guanine, 1);
		
		assertEquals(expectedCountMap, slice.getNucleotideCounts());
		assertNull(slice.getConsensusCall());


	}
	
	@Test
	public void replaceFirstSliceElement(){
		
		SliceBuilder builder = new SliceBuilder(elements);
		SliceElement newElement = new DefaultSliceElement("id", Nucleotide.Cytosine, PhredQuality.valueOf(50), Direction.FORWARD);
		
		builder.add(newElement);
		
		Slice slice =builder.build();
		
		assertEquals(3, slice.getCoverageDepth());
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 1);
		expectedCountMap.put(Nucleotide.Guanine, 1);
		expectedCountMap.put(Nucleotide.Cytosine, 1);
		
		assertEquals(newElement, slice.getSliceElement("id"));
		
	}
	@Test
	public void replaceMiddleSliceElement(){
	
		SliceBuilder builder = new SliceBuilder(elements);
		SliceElement newElement = new DefaultSliceElement("id2", Nucleotide.Cytosine, PhredQuality.valueOf(50), Direction.FORWARD);
		
		builder.add(newElement);
		
		Slice slice =builder.build();
		
		assertEquals(3, slice.getCoverageDepth());
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 1);
		expectedCountMap.put(Nucleotide.Guanine, 1);
		expectedCountMap.put(Nucleotide.Cytosine, 1);
		
		assertEquals(newElement, slice.getSliceElement("id2"));
		
	}
	
	@Test
	public void replaceLastSliceElement(){
		
		SliceBuilder builder = new SliceBuilder(elements);
		SliceElement newElement = new DefaultSliceElement("id3", Nucleotide.Cytosine, PhredQuality.valueOf(50), Direction.FORWARD);
		
		builder.add(newElement);
		
		Slice slice =builder.build();
		
		assertEquals(3, slice.getCoverageDepth());
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 1);
		expectedCountMap.put(Nucleotide.Guanine, 1);
		expectedCountMap.put(Nucleotide.Cytosine, 1);
		
		assertEquals(newElement, slice.getSliceElement("id3"));
		
	}
	
	@Test
	public void copyConstructor(){
		Slice slice = new SliceBuilder(elements).build();
		
		Slice copy = new SliceBuilder(slice).build();
		
		assertEquals(copy, slice);
	}
	
	@Test
	public void setConsensusCallToNull(){
		Slice slice = new SliceBuilder(elements)
								.setConsensus(null)
								.build();
		assertNull(slice.getConsensusCall());
	}
	
	@Test
	public void setConsensusCall(){
		
		Slice slice = new SliceBuilder(elements)
							.setConsensus(consensus)
							.build();
		assertEquals(consensus, slice.getConsensusCall());
	}
	
	@Test
	public void setConsensusCallInConstructor(){
		
		Slice expected = new SliceBuilder(elements)
							.setConsensus(consensus)
							.build();
		Slice actual = new SliceBuilder(consensus).addAll(elements).build();
		assertEquals(expected, actual);
		assertEquals(consensus, actual.getConsensusCall());
	}
	
	@Test
	public void copyConstructorWithSetConsensus(){
		Slice slice = new SliceBuilder(elements)
							.setConsensus(consensus).build();
		
		Slice copy = new SliceBuilder(slice).build();
		
		assertEquals(copy, slice);
		assertEquals(consensus, slice.getConsensusCall());
		assertEquals(consensus, copy.getConsensusCall());
	}
	
	@Test
	public void copyMethodWithSetConsensus(){
		SliceBuilder builder = new SliceBuilder(elements)
							.setConsensus(consensus);
		Slice copy = builder.copy().build();
		
		Slice slice = builder.build();
		
		assertEquals(copy, slice);
		assertEquals(consensus, slice.getConsensusCall());
		assertEquals(consensus, copy.getConsensusCall());
	}
	
	@Test
	public void copyChangeConsensusShouldNoLongerBeEqual(){
		Nucleotide otherConsensus = Nucleotide.Guanine;
		
		SliceBuilder builder = new SliceBuilder(elements)
							.setConsensus(consensus);
		Slice copy = builder.copy()
								.setConsensus(otherConsensus).build();
		
		Slice slice = builder.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(copy, slice);
		assertEquals(consensus, slice.getConsensusCall());
		assertEquals(otherConsensus, copy.getConsensusCall());
	}
	
	@Test(expected = NullPointerException.class)
	public void nullFilterThrowsNPE(){
		new SliceBuilder().filter(null);
	}
	
	@Test
	public void alwaysExceptingFilterDoesNotRemoveAnyElements(){
		Slice expected = new SliceBuilder(elements).build();
		Slice actual = new SliceBuilder(elements)
							.filter(new SliceElementFilter() {
								
								@Override
								public boolean accept(SliceElement e) {
									return true;
								}
							})
							.build();
		assertEquals(expected, actual);		
		
	}
	@Test
	public void filterRemovesEverything(){
		Slice actual = new SliceBuilder(elements)
							.filter(new SliceElementFilter() {
								
								@Override
								public boolean accept(SliceElement e) {
									return false;
								}
							})
							.build();
		assertEquals(0,actual.getCoverageDepth());		
		
	}
	@Test
	public void filterRemovesSomeElements(){
		Slice actual = new SliceBuilder(elements)
							.filter(new SliceElementFilter() {
								
								@Override
								public boolean accept(SliceElement e) {
									return e.getDirection()==Direction.FORWARD;
								}
							})
							.build();
		
		Slice expected = new SliceBuilder().add(elements.get(0)).build();
		assertEquals(expected,actual);		
		
	}
	
	@Test
	public void filterConstructorShouldMatchCallingFilterExplicity(){
		SliceElementFilter filter = new SliceElementFilter() {
			
			@Override
			public boolean accept(SliceElement e) {
				return e.getDirection()==Direction.FORWARD;
			}
		};
		
		Slice actual = new SliceBuilder(new SliceBuilder(elements).build(), filter).build();
		Slice expected = new SliceBuilder(elements).filter(filter).build();
		assertEquals(expected, actual);
	}
	
}
