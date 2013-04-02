package org.jcvi.jillion.assembly.clc.cas.consed;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Date;

import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.trace.sanger.phd.Phd;
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
