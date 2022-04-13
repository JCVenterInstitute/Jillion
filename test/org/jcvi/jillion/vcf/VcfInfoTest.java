package org.jcvi.jillion.vcf;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.vcf.VcfInfo;
import org.jcvi.jillion.vcf.VcfNumber;
import org.jcvi.jillion.vcf.VcfValueType;
import org.junit.Test;

public class VcfInfoTest {

	@Test
	public void equalsSameValues() {
		VcfInfo sut = VcfInfo.builder()
								.id("id")
								.description("description")
								.number(VcfNumber.valueOf(1))
								.type(VcfValueType.Integer)
								.build();
		
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
		TestUtil.assertEqualAndHashcodeSame(sut, VcfInfo.builder()
														.id("id")
														.description("description")
														.number(VcfNumber.valueOf(1))
														.type(VcfValueType.Integer)
														.build());
	}
	
	@Test
	public void differentIdNotEqual() {
		VcfInfo sut = VcfInfo.builder()
								.id("id")
								.description("description")
								.number(VcfNumber.valueOf(1))
								.type(VcfValueType.Integer)
								.build();
		
		VcfInfo different = VcfInfo.builder()
				.id("different id")
				.description("description")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Integer)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentDescriptionNotEqual() {
		VcfInfo sut = VcfInfo.builder()
								.id("id")
								.description("description")
								.number(VcfNumber.valueOf(1))
								.type(VcfValueType.Integer)
								.build();
		
		VcfInfo different = VcfInfo.builder()
				.id("id")
				.description("different description")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Integer)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentNumberNotEqual() {
		VcfInfo sut = VcfInfo.builder()
								.id("id")
								.description("description")
								.number(VcfNumber.valueOf(1))
								.type(VcfValueType.Integer)
								.build();
		
		VcfInfo different = VcfInfo.builder()
				.id("id")
				.description("description")
				.number(VcfNumber.valueOf(2))
				.type(VcfValueType.Integer)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentTypeNotEqual() {
		VcfInfo sut = VcfInfo.builder()
								.id("id")
								.description("description")
								.number(VcfNumber.valueOf(1))
								.type(VcfValueType.Integer)
								.build();
		
		VcfInfo different = VcfInfo.builder()
				.id("id")
				.description("description")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Flag)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentPropertiesNotEqual() {
		VcfInfo sut = VcfInfo.builder()
								.id("id")
								.description("description")
								.number(VcfNumber.valueOf(1))
								.type(VcfValueType.Integer)
								.parameter("key1", "value1")
								.build();
		
		VcfInfo different = VcfInfo.builder()
				.id("id")
				.description("description")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Integer)
				.parameter("different key", "different value")
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentPropertiesSameKeyDifferentValueNotEqual() {
		VcfInfo sut = VcfInfo.builder()
								.id("id")
								.description("description")
								.number(VcfNumber.valueOf(1))
								.type(VcfValueType.Integer)
								.parameter("key1", "value1")
								.build();
		
		VcfInfo different = VcfInfo.builder()
				.id("id")
				.description("description")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Integer)
				.parameter("key1", "value2")
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
}
