/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
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
	
	
	private SamReferenceSequence sut = new SamReferenceSequenceBuilder(name, length)
											.setGenomeAssemblyId(genomeAssemblyId)
											.setSpecies(species)
											.setUri(uri)
											.setMd5(md5)
											.build();
	
	@Test(expected = NullPointerException.class)
	public void nameCanNotBeNull(){
		new SamReferenceSequenceBuilder(null, length);
	}
	@Test(expected = NullPointerException.class)
	public void copyConstructorCanNotHaveNullArg(){
		new SamReferenceSequenceBuilder(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeLengthShouldThrowException(){
		new SamReferenceSequenceBuilder(name, -1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroLengthShouldThrowException(){
		new SamReferenceSequenceBuilder(name, 0);
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
		TestUtil.assertEqualAndHashcodeSame(sut, new SamReferenceSequenceBuilder(sut).build());
	}
	
	@Test
	public void notEqualDifferentName(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new SamReferenceSequenceBuilder(sut)
																.setName("different"+name)
																.build());
				
	}
	
	@Test
	public void notEqualDifferentLength(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new SamReferenceSequenceBuilder(sut)
																.setLength(length+100)
																.build());				
	}
	@Test
	public void notEqualDifferentMd5(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new SamReferenceSequenceBuilder(sut)
																.setMd5("different")
																.build());				
	}
	@Test
	public void notEqualDifferentSpecies(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new SamReferenceSequenceBuilder(sut)
																.setSpecies("different")
																.build());				
	}
	@Test
	public void notEqualDifferentUri(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new SamReferenceSequenceBuilder(sut)
																.setUri("different")
																.build());				
	}
	@Test
	public void notEqualDifferentGenomeAssemblyId(){
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, new SamReferenceSequenceBuilder(sut)
																.setGenomeAssemblyId("different")
																.build());				
	}
}
