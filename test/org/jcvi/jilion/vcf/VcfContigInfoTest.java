package org.jcvi.jilion.vcf;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.vcf.VcfContigInfo;
import org.junit.Test;

import static org.junit.Assert.*;

public class VcfContigInfoTest {

	@Test
	public void equalsItself() {
		VcfContigInfo sut = VcfContigInfo.builder()
								.id("id")
								.length(1234)
								.build();
		
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
		TestUtil.assertEqualAndHashcodeSame(sut, VcfContigInfo.builder()
																.id("id")
																.length(1234)
																.build());
	}
	
	@Test
	public void unsetParametersShouldBeEmpty() {
		VcfContigInfo sut = VcfContigInfo.builder()
				.id("id")
				.length(1234)
				.build();
		
		assertTrue(sut.getParameters().isEmpty());
	}
	
	@Test
	public void unsetLengthIsNully() {
		VcfContigInfo sut = VcfContigInfo.builder()
				.id("id")
				.build();
		
		assertNull(sut.getLength());
	}
	
	@Test
	public void differentIdNotEqual() {
		VcfContigInfo sut = VcfContigInfo.builder()
								.id("id")
								.length(1234)
								.build();
		
		VcfContigInfo different = VcfContigInfo.builder()
				.id("different id")
				.length(1234)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentLengthNotEqual() {
		VcfContigInfo sut = VcfContigInfo.builder()
								.id("id")
								.length(1234)
								.build();
		
		VcfContigInfo different = VcfContigInfo.builder()
				.id("id")
				.length(99999)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentLengthNullInOneButNotOtherLengthNotEqual() {
		VcfContigInfo sut = VcfContigInfo.builder()
								.id("id")
								.length(1234)
								.build();
		
		VcfContigInfo different = VcfContigInfo.builder()
				.id("id")
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentParametersEmptyInOneButNotOtherLengthNotEqual() {
		VcfContigInfo sut = VcfContigInfo.builder()
								.id("id")
								.length(1234)
								.parameter("key1", "value1")
								.build();
		
		VcfContigInfo different = VcfContigInfo.builder()
				.id("id")
				.length(1234)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentParametersNotEqual() {
		VcfContigInfo sut = VcfContigInfo.builder()
								.id("id")
								.length(1234)
								.parameter("key1", "value1")
								.build();
		
		VcfContigInfo different = VcfContigInfo.builder()
				.id("id")
				.length(1234)
				.parameter("diff key2", "diff value1")
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentMultipleParametersNotEqual() {
		VcfContigInfo sut = VcfContigInfo.builder()
								.id("id")
								.length(1234)
								.parameter("key1", "value1")
								.parameter("foo", "bar")
								.build();
		
		VcfContigInfo different = VcfContigInfo.builder()
				.id("id")
				.length(1234)
				.parameter("diff key2", "diff value1")
				.parameter("foo", "bar")
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void sameParametersAreEqual() {
		VcfContigInfo sut = VcfContigInfo.builder()
								.id("id")
								.length(1234)
								.parameter("key1", "value1")
								.parameter("foo", "bar")
								.build();
		
		VcfContigInfo different = VcfContigInfo.builder()
				.id("id")
				.length(1234)
				.parameter("key1", "value1")
				.parameter("foo", "bar")
				.build();
		
		TestUtil.assertEqualAndHashcodeSame(sut, different);
	}
	
}
