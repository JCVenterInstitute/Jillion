package org.jcvi.jillion.vcf;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.vcf.VcfFilter;
import org.junit.Test;

import static org.junit.Assert.*;

public class VcfFilterTest {

	
	
	@Test
	public void nullIdThrowsException() {
		assertThrows(NullPointerException.class,()-> new VcfFilter(null, "description"));
	}
	
	@Test
	public void nullDescriptionThrowsException() {
		assertThrows(NullPointerException.class,()-> new VcfFilter("id", null));
	}
	
	@Test
	public void assertSameValuesAreEqual() {
		VcfFilter sut = new VcfFilter("id", "a description");
		
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
		TestUtil.assertEqualAndHashcodeSame(sut, new VcfFilter("id", "a description"));
	}
	
	@Test
	public void differentFieldsAreNotEqual() {
		VcfFilter sut = new VcfFilter("id", "a description");
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new VcfFilter("diff id", "a description"));
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new VcfFilter("id", "something completely different"));
	
	}
}
