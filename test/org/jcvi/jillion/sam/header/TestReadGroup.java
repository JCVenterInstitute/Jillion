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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.sam.header.SamReadGroup.PlatformTechnology;
import org.junit.Test;

public class TestReadGroup {

	private final String id = "id";
	private final String sequencingCenter = "seq center";
	private final String description = "description";
	private final String library = "library";
	private final String programs = " program_name";
	private final String platformUnit = " platformUnit_1";
	private final String sampleOrPoolName = "pool 123";
	
	private final Integer predictedMedianInsertSize = 500;
	private final PlatformTechnology platform = PlatformTechnology.ILLUMINA;
	
	private final Long datetime = 123456789L;
	
	private final NucleotideSequence keySequence = new NucleotideSequenceBuilder("ACGT").build();
	private final NucleotideSequence flowOrder =new NucleotideSequenceBuilder("ACGTACGTACGTACGTACGTACGT").build();


	SamReadGroup sut = new SamReadGroupBuilder(id)
						.setSequencingCenter(sequencingCenter)
						.setDescription(description)
						.setLibrary(library)
						.setPrograms(programs)
						.setPlatformUnit(platformUnit)
						.setPlatform(platform)
						.setSampleOrPoolName(sampleOrPoolName)
						.setPredictedInsertSize(predictedMedianInsertSize)
						.setRunDate(new Date(datetime))
						.setKeySequence(keySequence)
						.setFlowOrder(flowOrder)
						.build();
	
	@Test
	public void getters(){
		assertEquals(id, sut.getId());
		assertEquals(sequencingCenter, sut.getSequencingCenter());
		assertEquals(description, sut.getDescription());
		assertEquals(library, sut.getLibrary());
		assertEquals(programs, sut.getPrograms());
		assertEquals(platformUnit, sut.getPlatformUnit());
		assertEquals(platform, sut.getPlatform());
		assertEquals(sampleOrPoolName, sut.getSampleOrPoolName());
		assertEquals(predictedMedianInsertSize, sut.getPredictedInsertSize());
		assertEquals(new Date(datetime), sut.getRunDate());
		assertEquals(keySequence, sut.getKeySequence());
		assertEquals(flowOrder, sut.getFlowOrder());
	}
	
	@Test(expected = NullPointerException.class)
	public void nullIdShouldThrowNPE(){
		new SamReadGroupBuilder((String)null);
	}
	
	@Test
	public void setIdOnlyOtherFieldsAreNull(){
		SamReadGroup readGroup = new SamReadGroupBuilder(id).build();
		assertEquals(id, readGroup.getId());
		
		assertNull(readGroup.getSequencingCenter());
		assertNull( readGroup.getDescription());
		assertNull(readGroup.getLibrary());
		assertNull(readGroup.getPrograms());
		assertNull(readGroup.getPlatformUnit());
		assertNull(readGroup.getPlatform());
		assertNull(readGroup.getSampleOrPoolName());
		assertNull(readGroup.getPredictedInsertSize());
		assertNull(readGroup.getRunDate());
		assertNull(readGroup.getKeySequence());
		assertNull(readGroup.getFlowOrder());
		
	}
	
	@Test
	public void notEqualToNull(){
		assertFalse(sut.equals(null));
	}
	
	@Test
	public void notEqualToDifferentClass(){
		assertFalse(sut.equals("not a readGroup"));
	}
	
	@Test
	public void equalsSameRef(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void equalsSameValues(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.build();
		TestUtil.assertEqualAndHashcodeSame(sut, other);
	}
	
	@Test
	public void differentIdIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setId("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	@Test(expected = NullPointerException.class)
	public void settingIdToNullShouldThrowNPE(){
		new SamReadGroupBuilder(sut)
								.setId(null)
								;
	}
	
	@Test
	public void differentSeqCenterIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setSequencingCenter("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	
	
	@Test
	public void differentDescriptionIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setDescription("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentLibraryIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setLibrary("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	@Test
	public void differentProgramsIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setPrograms("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentPlatformUnitIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setPlatformUnit("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentPlatformIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setPlatform(PlatformTechnology.CAPILLARY)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentSampleNameIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setSampleOrPoolName("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentInsertSizeIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setPredictedInsertSize(predictedMedianInsertSize +1000)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentRunDateIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setRunDate(new Date(datetime +10000))
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentKeySequenceIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setKeySequence(new NucleotideSequenceBuilder(keySequence)
													.append("G")
													.build())
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentFlowOrderIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setFlowOrder(new NucleotideSequenceBuilder(flowOrder)
													.append("G")
													.build())
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	////////////////////
	@Test
	public void nullDescriptionIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setDescription(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullLibraryIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setLibrary(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	@Test
	public void nullProgramsIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setPrograms(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullPlatformUnitIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setPlatformUnit(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullPlatformIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setPlatform(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullSampleNameIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setSampleOrPoolName(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullInsertSizeIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setPredictedInsertSize(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullRunDateIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setRunDate(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullKeySequenceIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setKeySequence(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullFlowOrderIsNotEqual(){
		SamReadGroup other = new SamReadGroupBuilder(sut)
								.setFlowOrder(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}

}
