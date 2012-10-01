package org.jcvi.common.annotation;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
public class TestGene {
	private final String name = "name";
	private final Gene gene = new Gene(name, Range.of(1,10));
	@Rule
	public ExpectedException thrown = ExpectedException.none();


	@Test
	public void nullNameShouldThrowNPE(){
	    thrown.expect(NullPointerException.class);
	    thrown.expectMessage("name can not be null");
	    new Gene(null, Range.of(1,10));
	}
	@Test
	public void oneExon(){		
		assertEquals(name, gene.getName());
		assertEquals(Arrays.asList(Range.of(1,10)), gene.getExons());
		assertTrue(gene.getIntrons().isEmpty());
	}
	
	@Test
	public void oneIntron(){
		List<Range> exons = Arrays.asList(
				Range.of(1,10),
				Range.of(20,30)
				);
		List<Range> expectedIntrons = Arrays.asList(
				Range.of(11,19)
				);
		Gene gene = new Gene(name, exons);
		assertEquals(name, gene.getName());
		assertEquals(exons, gene.getExons());
		assertEquals(expectedIntrons, gene.getIntrons());
	}
	@Test
	public void twoIntrons(){
		List<Range> exons = Arrays.asList(
				Range.of(1,10),
				Range.of(20,30),
				Range.of(35,40)
				);
		List<Range> expectedIntrons = Arrays.asList(
				Range.of(11,19),
				Range.of(31,34)
				);
		Gene gene = new Gene(name, exons);
		assertEquals(name, gene.getName());
		assertEquals(exons, gene.getExons());
		assertEquals(expectedIntrons, gene.getIntrons());
	}
	
	@Test
	public void sameRefIsEqual(){
		TestUtil.assertEqualAndHashcodeSame(gene, gene);
	}
	
	@Test
	public void sameValuesAreEqual(){
		Gene same = new Gene(name, Range.of(1,10));
		TestUtil.assertEqualAndHashcodeSame(gene, same);
	}
	@Test
	public void differentClassShouldNotBeEqual(){
		assertFalse(gene.equals("not a gene"));
	}
	
	@Test
	public void shouldNeverEqualNull(){
		assertFalse(gene.equals(null));
	}
	@Test
	public void differentNameIsNotEqual(){
		Gene different = new Gene("not" + name, Range.of(1,10));
		TestUtil.assertNotEqualAndHashcodeDifferent(gene, different);
	}
	
	@Test
	public void differentExonShouldNotBeEqual(){
		Gene different = new Gene(name, Range.of(1,20));
		TestUtil.assertNotEqualAndHashcodeDifferent(gene, different);
	}
}
