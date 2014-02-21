package org.jcvi.jillion.sam.header;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

public class TestReferenceSequence {

	private final String name = "refName";
	private final int length = 1234;
	
	private final String genomeAssemblyId = "genomeAsmId";
	private final String species = "species_name";
	private final String uri = "prot://foo";
	private final String md5 = "md5checkSum";
	
	
	private ReferenceSequence sut = new ReferenceSequence.Builder(name, length)
											.setGenomeAssemblyId(genomeAssemblyId)
											.setSpecies(species)
											.setUri(uri)
											.setMd5(md5)
											.build();
	
	@Test(expected = NullPointerException.class)
	public void nameCanNotBeNull(){
		new ReferenceSequence.Builder(null, length);
	}
	@Test(expected = NullPointerException.class)
	public void copyConstructorCanNotHaveNullArg(){
		new ReferenceSequence.Builder(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeLengthShouldThrowException(){
		new ReferenceSequence.Builder(name, -1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroLengthShouldThrowException(){
		new ReferenceSequence.Builder(name, 0);
	}
	
	@Test
	public void getters(){
		assertEquals(name, sut.getName());
		assertEquals(length, sut.getLength());
		assertEquals(genomeAssemblyId, sut.getGenomeAssemblyId());
		assertEquals(species, sut.getSpecies());
		assertEquals(uri, sut.getUri());
		assertEquals(md5, sut.getMd5());
	}
	
	@Test
	public void notEqualToNull(){
		assertThat(sut, not(equalTo(null)));
	}
	@Test
	public void equalSameRef(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void equalSameValues(){
		TestUtil.assertEqualAndHashcodeSame(sut, new ReferenceSequence.Builder(sut).build());
	}
	
	@Test
	public void notEqualDifferentName(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new ReferenceSequence.Builder(sut)
																.setName("different"+name)
																.build());
				
	}
	
	@Test
	public void notEqualDifferentLength(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new ReferenceSequence.Builder(sut)
																.setLength(length+100)
																.build());				
	}
	@Test
	public void notEqualDifferentMd5(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new ReferenceSequence.Builder(sut)
																.setMd5("different")
																.build());				
	}
	@Test
	public void notEqualDifferentSpecies(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new ReferenceSequence.Builder(sut)
																.setSpecies("different")
																.build());				
	}
	@Test
	public void notEqualDifferentUri(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new ReferenceSequence.Builder(sut)
																.setUri("different")
																.build());				
	}
	@Test
	public void notEqualDifferentGenomeAssemblyId(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new ReferenceSequence.Builder(sut)
																.setGenomeAssemblyId("different")
																.build());				
	}
}
