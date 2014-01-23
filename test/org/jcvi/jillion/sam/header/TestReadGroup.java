package org.jcvi.jillion.sam.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.sam.header.ReadGroup.PlatformTechnology;
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


	ReadGroup sut = new ReadGroup.Builder(id)
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
		new ReadGroup.Builder((String)null);
	}
	
	@Test
	public void setIdOnlyOtherFieldsAreNull(){
		ReadGroup readGroup = new ReadGroup.Builder(id).build();
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
		ReadGroup other = new ReadGroup.Builder(sut)
								.build();
		TestUtil.assertEqualAndHashcodeSame(sut, other);
	}
	
	@Test
	public void differentIdIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setId("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	@Test(expected = NullPointerException.class)
	public void settingIdToNullShouldThrowNPE(){
		new ReadGroup.Builder(sut)
								.setId(null)
								;
	}
	
	@Test
	public void differentSeqCenterIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setSequencingCenter("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	
	
	@Test
	public void differentDescriptionIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setDescription("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentLibraryIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setLibrary("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	@Test
	public void differentProgramsIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setPrograms("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentPlatformUnitIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setPlatformUnit("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentPlatformIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setPlatform(PlatformTechnology.CAPILLARY)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentSampleNameIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setSampleOrPoolName("different")
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentInsertSizeIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setPredictedInsertSize(predictedMedianInsertSize +1000)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentRunDateIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setRunDate(new Date(datetime +10000))
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentKeySequenceIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setKeySequence(new NucleotideSequenceBuilder(keySequence)
													.append("G")
													.build())
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentFlowOrderIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setFlowOrder(new NucleotideSequenceBuilder(flowOrder)
													.append("G")
													.build())
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	////////////////////
	@Test
	public void nullDescriptionIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setDescription(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullLibraryIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setLibrary(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	
	@Test
	public void nullProgramsIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setPrograms(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullPlatformUnitIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setPlatformUnit(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullPlatformIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setPlatform(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullSampleNameIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setSampleOrPoolName(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullInsertSizeIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setPredictedInsertSize(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullRunDateIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setRunDate(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullKeySequenceIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setKeySequence(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullFlowOrderIsNotEqual(){
		ReadGroup other = new ReadGroup.Builder(sut)
								.setFlowOrder(null)
								.build();
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}

}
