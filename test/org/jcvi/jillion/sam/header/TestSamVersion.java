package org.jcvi.jillion.sam.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

public class TestSamVersion {

	private int major = 1;
	private int minor = 2;
	
	private final SamVersion sut = new SamVersion(major, minor);
	
	@Test(expected =IllegalArgumentException.class)
	public void negativeMajorShouldThrowException(){
		new SamVersion(-1, 2);
	}
	@Test(expected =IllegalArgumentException.class)
	public void negativeMinorShouldThrowException(){
		new SamVersion(1, -2);
	}
	
	@Test
	public void getters(){
		assertEquals(major, sut.getMajor());
		assertEquals(minor, sut.getMinor());
	}
	
	@Test
	public void testToString(){
		assertEquals("1.2", sut.toString());
	}
	
	@Test
	public void isBefore(){
		assertTrue("other major bigger", sut.isBefore(new SamVersion(major+1, 0)));
		assertTrue("other minor bigger", sut.isBefore(new SamVersion(major, minor+1)));
		assertFalse("same version", sut.isBefore(sut));
		assertFalse("other major smaller", sut.isBefore(new SamVersion(major-1, 0)));
		assertFalse("other minor smaller", sut.isBefore(new SamVersion(major, minor-1)));
	}
	
	@Test
	public void isAfter(){
		assertFalse("other major bigger", sut.isAfter(new SamVersion(major+1, 0)));
		assertFalse("other minor bigger", sut.isAfter(new SamVersion(major, minor+1)));
		assertFalse("same version", sut.isAfter(sut));
		assertTrue("other major smaller", sut.isAfter(new SamVersion(major-1, 0)));
		assertTrue("other minor smaller", sut.isAfter(new SamVersion(major, minor-1)));
	}
	@Test
	public void notEqualToNull(){
		assertFalse(sut.equals(null));
	}
	@Test
	public void notEqualTodifferentClass(){
		assertFalse(sut.equals("not a version"));
	}
	@Test
	public void sameRefsAreEqual(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void sameValuesAreEqual(){
		SamVersion other = new SamVersion(major, minor);
		TestUtil.assertEqualAndHashcodeSame(sut, other);
	}
	@Test
	public void differentMajorNotEqual(){
		SamVersion other = new SamVersion(major+1, minor);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentMinorNotEqual(){
		SamVersion other = new SamVersion(major, minor+1);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	@Test
	public void parseValidVersion(){
		assertEquals(sut, SamVersion.parseVersion(String.format("%d.%d", major,minor)));
		assertEquals(new SamVersion(11,15), SamVersion.parseVersion("11.15"));
	}
	@Test(expected = NullPointerException.class)
	public void parseNullVersionShouldThrowNPE(){
		SamVersion.parseVersion(null);
	}
	@Test
	public void parseInvalidVersionReturnsNull(){
		assertNull(SamVersion.parseVersion("not A version"));
	}
	
	@Test
	public void parseInvalidVersionNoMinorShouldReturnsNull(){
		assertNull(SamVersion.parseVersion("1"));
	}
	@Test
	public void parseInvalidVersionHasWhitespaceShouldReturnsNull(){
		assertNull(SamVersion.parseVersion("1 . 2"));
		assertNull("leading whitespace",SamVersion.parseVersion("  1.2"));
		assertNull("trailing whitespace",SamVersion.parseVersion("1.2  "));
	}
	
	
}
