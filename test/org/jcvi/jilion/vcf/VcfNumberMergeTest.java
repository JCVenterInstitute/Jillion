package org.jcvi.jilion.vcf;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

import org.jcvi.jillion.vcf.VcfNumber;

public class VcfNumberMergeTest {

	@Test
	public void nonNumberSameTypeMergesToItself() {

		numberMergedWithItselfReturnsItself(VcfNumber.A);
		numberMergedWithItselfReturnsItself(VcfNumber.R);
		numberMergedWithItselfReturnsItself(VcfNumber.G);
		numberMergedWithItselfReturnsItself(VcfNumber.DOT);
	}
	
	@Test
	public void numberSameValueMergesToItself() {

		numberMergedWithItselfReturnsItself(VcfNumber.valueOf(1));
		numberMergedWithItselfReturnsItself(VcfNumber.valueOf(2));
		numberMergedWithItselfReturnsItself(VcfNumber.valueOf(0));
		numberMergedWithItselfReturnsItself(VcfNumber.valueOf(10));
	}
	
	@Test
	public void numberWithDifferentValueMergesToDot() {
		assertEquals(VcfNumber.DOT, VcfNumber.valueOf(1).merge(VcfNumber.valueOf(2)).get() );
	}
	
	@Test
	public void differentTypesReturnEmpty() {
		List<VcfNumber> numbers = List.of(
				VcfNumber.valueOf(1),
				VcfNumber.A,
				VcfNumber.DOT,
				VcfNumber.G,
				VcfNumber.R);
		for(VcfNumber n1 : numbers) {
			for(VcfNumber n2 : numbers) {
				if(n1.getType().equals(n2.getType())) {
					continue;
				}
				assertEquals(Optional.empty(), n1.merge(n2));
			}
		}
	}
	

	private void numberMergedWithItselfReturnsItself(VcfNumber number) {
		assertEquals(number, number.merge(number).get() );
	}
}
