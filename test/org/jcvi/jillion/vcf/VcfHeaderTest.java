package org.jcvi.jillion.vcf;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.vcf.VcfHeader;
import org.junit.Test;

public class VcfHeaderTest {

	@Test
	public void equalsItself() {
		VcfHeader sut = VcfHeader.builder()
								.build();
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	
	@Test
	public void emptyFieldsShouldBeEmpty() {
		VcfHeader sut = VcfHeader.builder()
				.build();
		assertTrue(sut.getContigInfos().isEmpty());
		assertTrue(sut.getExtraColumns().isEmpty());
		assertTrue(sut.getFilters().isEmpty());
		assertTrue(sut.getFormats().isEmpty());
		assertTrue(sut.getInfos().isEmpty());
		assertTrue(sut.getProperties().isEmpty());
		
	}
	
	@Test
	public void unsetVersionShouldBeNull() {
		VcfHeader sut = VcfHeader.builder()
				.build();
		assertNull(sut.getVersion());
	}
	
	@Test
	public void sameEmptyValuesShouldBeEqual() {
		
		TestUtil.assertEqualAndHashcodeSame(VcfHeader.builder()
				.build(), VcfHeader.builder()
				.build());
	}
	
	@Test
	public void version() {
		VcfHeader sut = VcfHeader.builder()
				.version("4.0")
				.build();
		VcfHeader sameVersion = VcfHeader.builder()
				.version("4.0")
				.build();
		
		VcfHeader diffVersion = VcfHeader.builder()
				.version("3.5")
				.build();
		
		TestUtil.assertEqualAndHashcodeSame(sut, sameVersion);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, diffVersion);
	}
	
	@Test
	public void sameSingleProperty() {
		VcfHeader sut = VcfHeader.builder()
				.property("key1", "value1")
				.build();
		VcfHeader samePropertySameOrder = VcfHeader.builder()
				.property("key1", "value1")
				.build();
		VcfHeader differentKey = VcfHeader.builder()
				.property("key2", "value1")
				.build();
		VcfHeader differentValue = VcfHeader.builder()
				.property("key1", "value2")
				.build();
		
		
		TestUtil.assertEqualAndHashcodeSame(sut, samePropertySameOrder);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentKey);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValue);
	}
	
}
