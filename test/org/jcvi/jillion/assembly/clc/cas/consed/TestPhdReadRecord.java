/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas.consed;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestPhdReadRecord {

	private final Date date = new Date();
	private final PhdInfo phdInfo = new PhdInfo("traceName", "phdName", date);
	
	Phd phd;
	PhdReadRecord sut;
	public TestPhdReadRecord(){
		phd = createMock(Phd.class);
		sut = new PhdReadRecord(phd, phdInfo);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullPhdShouldThrowNPE(){
		new PhdReadRecord(null, phdInfo);
	}
	
	@Test(expected = NullPointerException.class)
	public void nullPhdInfoShouldThrowNPE(){		
		new PhdReadRecord(phd, null);
	}
	
	@Test
	public void getPhdInfo(){
		assertEquals(phdInfo, sut.getPhdInfo());
	}
	
	@Test
	public void getPhd(){
		assertEquals(phd, sut.getPhd());
	}
	
	@Test
	public void getIdDelegatesToPhd(){
		String id = "theId";
		expect(phd.getId()).andReturn(id);		
		replay(phd);		
		assertEquals(id, sut.getId());
	}
	
	@Test
	public void getQualitySequenceDelegatesToPhd(){
		QualitySequence quals = createMock(QualitySequence.class);

		expect(phd.getQualitySequence()).andReturn(quals);		
		replay(phd);		
		assertEquals(quals, sut.getQualitySequence());
	}
	
	@Test
	public void getNucleotideSequenceDelegatesToPhd(){
		NucleotideSequence seq = createMock(NucleotideSequence.class);

		expect(phd.getNucleotideSequence()).andReturn(seq);		
		replay(phd);		
		assertEquals(seq, sut.getNucleotideSequence());
	}
	
	@Test
	public void sameRefShouldBeEqual(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	
	@Test
	public void sameValuesShouldBeEqual(){
		TestUtil.assertEqualAndHashcodeSame(sut, new PhdReadRecord(phd, phdInfo));
	}
	
	@Test
	public void differentPhdInfoShouldNotBeEqual(){
		PhdReadRecord different = new PhdReadRecord(phd, 
					new PhdInfo("traceName2", "phdName2", date)
				);
		expect(phd.getId()).andStubReturn("traceName2");
		replay(phd);
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void differentPhdShouldNotBeEqual(){
		PhdReadRecord different = new PhdReadRecord(createMock(Phd.class), phdInfo);
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
	}
	
	@Test
	public void testToStringPrintsId(){
		String id = "theId";
		expect(phd.getId()).andReturn(id);
		
		replay(phd);		
		assertEquals(id, sut.toString());
	}
	
	@Test
	public void notEqualToNull(){
		assertFalse(sut.equals(null));
	}
	
	@Test
	public void notEqualToDifferentType(){
		assertFalse(sut.equals("Not a PhdReadRecord"));
	}
}
