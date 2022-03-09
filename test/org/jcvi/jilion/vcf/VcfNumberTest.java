package org.jcvi.jilion.vcf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.vcf.VcfNumber;
import org.jcvi.jillion.vcf.VcfNumberType;
import org.junit.Test;

public class VcfNumberTest {

	@Test
	public void nonValueShouldHaveNullValues() {
		assertNull(VcfNumber.A.getValue());
		assertNull(VcfNumber.DOT.getValue());
		assertNull(VcfNumber.G.getValue());
		assertNull(VcfNumber.R.getValue());
	}
	
	@Test
	public void number() {
		VcfNumber one = VcfNumber.valueOf(1);
		assertEquals(1, one.getValue().intValue());
		assertEquals(VcfNumberType.NUMBER, one.getType());
	}
	
	@Test
	public void numberEquality() {
		VcfNumber one = VcfNumber.valueOf(1);
		VcfNumber two = VcfNumber.valueOf(2);
		
		TestUtil.assertNotEqualAndHashcodeDifferent(one, two);
	}
	
	@Test
	public void parseNonNumber() {
		assertEquals(VcfNumber.A, VcfNumber.parse("A"));
		assertEquals(VcfNumber.G, VcfNumber.parse("G"));
		assertEquals(VcfNumber.R, VcfNumber.parse("R"));
		assertEquals(VcfNumber.DOT, VcfNumber.parse("."));
	}
	
	@Test
	public void parseNumber() {
		VcfNumber five = VcfNumber.parse("5");
		assertEquals(VcfNumberType.NUMBER, five.getType());
		assertEquals(5, five.getValue().intValue());
	}
	
	@Test
	public void encodeNonNumber() {

		assertEncodedStringParsesCorrectly(VcfNumber.A);
		assertEncodedStringParsesCorrectly(VcfNumber.G);
		assertEncodedStringParsesCorrectly(VcfNumber.R);
		assertEncodedStringParsesCorrectly(VcfNumber.DOT);
	}
	
	@Test
	public void encodeNumber() {
		assertEncodedStringParsesCorrectly(VcfNumber.parse("0"));
		assertEncodedStringParsesCorrectly(VcfNumber.parse("1"));
		assertEncodedStringParsesCorrectly(VcfNumber.parse("5"));
		assertEncodedStringParsesCorrectly(VcfNumber.parse("10"));
	}

	private void assertEncodedStringParsesCorrectly(VcfNumber num) {
		assertEquals(num, VcfNumber.parse(num.toEncodedString()));
	}
}
