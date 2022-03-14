package org.jcvi.jillion.vcf;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

import org.jcvi.jillion.core.testUtil.TestUtil;

public class VcfFormatTest {

	@Test
	public void equalsSameValues() {
		VcfFormat sut = VcfFormat.builder()
							.id("id")
							.number(VcfNumber.valueOf(1))
							.type(VcfValueType.Integer)
							.build();
		
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
		TestUtil.assertEqualAndHashcodeSame(sut,  VcfFormat.builder()
															.id("id")
															.number(VcfNumber.valueOf(1))
															.type(VcfValueType.Integer)
															.build());
		
	}
	
	@Test
	public void differentIdNotEqual() {
		VcfFormat sut = VcfFormat.builder()
							.id("id")
							.number(VcfNumber.valueOf(1))
							.type(VcfValueType.Integer)
							.build();
		
		VcfFormat different = VcfFormat.builder()
				.id("different id")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Integer)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
		
	}
	@Test
	public void differentValueNotEqual() {
		VcfFormat sut = VcfFormat.builder()
							.id("id")
							.number(VcfNumber.valueOf(1))
							.type(VcfValueType.Integer)
							.build();
		
		VcfFormat different = VcfFormat.builder()
				.id("id")
				.number(VcfNumber.valueOf(2))
				.type(VcfValueType.Integer)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
		
	}
	@Test
	public void differentTypeNotEqual() {
		VcfFormat sut = VcfFormat.builder()
							.id("id")
							.number(VcfNumber.valueOf(1))
							.type(VcfValueType.Integer)
							.build();
		
		VcfFormat different = VcfFormat.builder()
				.id("id")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Character)
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
		
	}
	
	@Test
	public void differentPropertiesNotEqual() {
		VcfFormat sut = VcfFormat.builder()
							.id("id")
							.number(VcfNumber.valueOf(1))
							.type(VcfValueType.Integer)
							.parameter("key", "value")
							.build();
		
		VcfFormat different = VcfFormat.builder()
				.id("id")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Integer)
				.parameter("diff key", "value")
				.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
		
	}
	
	@Test
	public void equalsSameValuesWithProperties() {
		VcfFormat sut = VcfFormat.builder()
							.id("id")
							.number(VcfNumber.valueOf(1))
							.type(VcfValueType.Integer)
							.parameter("key1", "value1")
							.parameter("key2", "value2")
							.build();
		
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
		TestUtil.assertEqualAndHashcodeSame(sut,  VcfFormat.builder()
															.id("id")
															.number(VcfNumber.valueOf(1))
															.type(VcfValueType.Integer)
															.parameter("key1", "value1")
															.parameter("key2", "value2")
															.build());
		
	}
	
	@Test
	public void unsetDescriptionIsNull() {
		VcfFormat sut = VcfFormat.builder()
				.id("id")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Integer)
				.build();
		
		assertNull(sut.getDescription());
	}
	
	@Test
	public void oneProperty() {
		VcfFormat sut = VcfFormat.builder()
				.id("id")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Integer)
				.parameter("key", "value")
				.build();
		
		assertEquals(Map.of("key", "value"), sut.getParameters());
	}
	
	@Test
	public void unsetParametersIsEmpty() {
		VcfFormat sut = VcfFormat.builder()
				.id("id")
				.number(VcfNumber.valueOf(1))
				.type(VcfValueType.Integer)
				.build();
		
		assertTrue(sut.getParameters().isEmpty());
	}
	
	@Test
	public void idNullShouldThrowNpe() {
		assertThrows(NullPointerException.class, ()->VcfFormat.builder()
													.id(null)
													.number(VcfNumber.valueOf(1))
													.type(VcfValueType.Integer)
													.build());
	}
	
	@Test
	public void numberNullShouldThrowNpe() {
		assertThrows(NullPointerException.class, ()->VcfFormat.builder()
													.id("id")
													.number(null)
													.type(VcfValueType.Integer)
													.build());
	}
	
	@Test
	public void propertiesNullShouldThrowNpe() {
		assertThrows(NullPointerException.class, ()->VcfFormat.builder()
													.id("id")
													.number(VcfNumber.valueOf(1))
													.type(VcfValueType.Integer)
													.parameters(null)
													.build());
	}
	@Test
	public void propertyKeyNullShouldThrowNpe() {
		assertThrows(NullPointerException.class, ()->VcfFormat.builder()
													.id("id")
													.number(VcfNumber.valueOf(1))
													.type(VcfValueType.Integer)
													.parameter(null, "value")
													.build());
	}
	
	@Test
	public void propertyValueNullShouldThrowNpe() {
		assertThrows(NullPointerException.class, ()->VcfFormat.builder()
													.id("id")
													.number(VcfNumber.valueOf(1))
													.type(VcfValueType.Integer)
													.parameter("id", null)
													.build());
	}
	
	@Test
	public void typeNullShouldThrowNpe() {
		assertThrows(NullPointerException.class, ()->VcfFormat.builder()
													.id("id")
													.number(VcfNumber.valueOf(1))
													.type(null)
													.build());
	}
}
