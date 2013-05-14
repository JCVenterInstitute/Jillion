package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.junit.Test;
public class TestSliceBuilder {

	List<SliceElement> elements = Arrays.<SliceElement>asList(
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
	
	private Map<Nucleotide, Integer> createEmptyCountMap(){
		EnumMap<Nucleotide, Integer> map = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
		for(Nucleotide n : Nucleotide.values()){
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
		assertFalse(builder.containsElement("id"));
		builder.addSliceElement("id", Nucleotide.Adenine, 
													PhredQuality.valueOf(30), 
													Direction.FORWARD);
		assertTrue(builder.containsElement("id"));
	}
	
	@Test(expected = NullPointerException.class)
	public void containsNullShouldThrowNPE(){
		new SliceBuilder().containsElement(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void addNullElementShouldThrowNPE(){
		new SliceBuilder().addSliceElement(null);
	}
	
	@Test
	public void addSliceElement(){
		SliceElement element = elements.get(0);
		
		Slice slice= new SliceBuilder()
						.addSliceElement(element)
						.build();

		assertEquals(1, slice.getCoverageDepth());
		assertTrue(slice.containsElement(element.getId()));
		assertEquals(element, slice.getSliceElement(element.getId()));
		
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Adenine, 1);
		
		assertEquals(expectedCountMap, slice.getNucleotideCounts());
	}
	
	@Test
	public void addingSliceElementIdShouldOverwriteExistingValue(){
		SliceElement element = elements.get(0);
		
		SliceElement replacementElement = new DefaultSliceElement(element.getId(), Nucleotide.Unknown, PhredQuality.valueOf(0), Direction.REVERSE);
		Slice slice = new SliceBuilder()
						.addSliceElement(element)
						.addSliceElement(replacementElement)
						.build();
		
		assertEquals(1, slice.getCoverageDepth());
		Map<Nucleotide, Integer> expectedCountMap = createEmptyCountMap();
		expectedCountMap.put(Nucleotide.Unknown, 1);
		
		assertEquals(expectedCountMap, slice.getNucleotideCounts());
		
	}
	
	@Test
	public void addMultipleSliceElementsOneAtATime(){
		
		
		SliceBuilder builder= new SliceBuilder();
		
		for(SliceElement element : elements){
			builder.addSliceElement(element);
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
	}
	
	@Test
	public void addMultipleSliceElementsAllAtOnce(){
		List<SliceElement> elements = Arrays.<SliceElement>asList(
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
		
		SliceBuilder builder= new SliceBuilder();
		
		builder.addSliceElements(elements);

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
	}
	
	@Test
	public void removeNonExistentElementShouldDoNothing(){
		List<SliceElement> elements = Arrays.<SliceElement>asList(
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
		
		SliceBuilder builder= new SliceBuilder(elements);
		
		builder.removeSliceElement("does not exist");

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
	}
	
	@Test
	public void removeSliceElement(){
		List<SliceElement> elements = Arrays.<SliceElement>asList(
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
		
		SliceBuilder builder= new SliceBuilder();
		
		for(SliceElement element : elements){
			builder.addSliceElement(element);
		}
		
		builder.removeSliceElement("id2");
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
	}
	
	@Test
	public void copy(){
		List<SliceElement> elements = Arrays.<SliceElement>asList(
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

		SliceBuilder builder= new SliceBuilder();
		builder.addSliceElements(elements);
		SliceBuilder copy =builder.copy();
		copy.removeSliceElement("id2");
		
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


	}
	
	@Test
	public void replaceFirstSliceElement(){
		List<SliceElement> elements = Arrays.<SliceElement>asList(
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
		
		SliceBuilder builder = new SliceBuilder(elements);
		SliceElement newElement = new DefaultSliceElement("id", Nucleotide.Cytosine, PhredQuality.valueOf(50), Direction.FORWARD);
		
		builder.addSliceElement(newElement);
		
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
		List<SliceElement> elements = Arrays.<SliceElement>asList(
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
		
		SliceBuilder builder = new SliceBuilder(elements);
		SliceElement newElement = new DefaultSliceElement("id2", Nucleotide.Cytosine, PhredQuality.valueOf(50), Direction.FORWARD);
		
		builder.addSliceElement(newElement);
		
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
		List<SliceElement> elements = Arrays.<SliceElement>asList(
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
		
		SliceBuilder builder = new SliceBuilder(elements);
		SliceElement newElement = new DefaultSliceElement("id3", Nucleotide.Cytosine, PhredQuality.valueOf(50), Direction.FORWARD);
		
		builder.addSliceElement(newElement);
		
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
	
}
