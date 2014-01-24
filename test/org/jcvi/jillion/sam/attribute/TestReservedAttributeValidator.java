package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.core.util.UnsignedByteArray;
import org.jcvi.jillion.core.util.UnsignedIntArray;
import org.jcvi.jillion.core.util.UnsignedShortArray;
import org.jcvi.jillion.sam.header.ReadGroup;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamProgram;
import org.junit.Test;

public class TestReservedAttributeValidator {

	private final ReservedAttributeValidator sut = ReservedAttributeValidator.INSTANCE;
	private final String libId = "libraryId";
	private final String readGroupId ="readGroupId";
	private final String platformUnit = "platformUnit";
	private final String programId = "programId";
	
	@Test
	public void defaultValidatorShouldDoNothing() throws InvalidAttributeException{
		SamHeader header = new SamHeader.Builder().build();
		
		for(ReservedSamAttributeKeys k : ReservedSamAttributeKeys.values()){
			if(k != ReservedSamAttributeKeys.LIBRARY 
					&& k !=ReservedSamAttributeKeys.PROGRAM
					&& k !=ReservedSamAttributeKeys.PLATFORMT_UNIT
					&& k !=ReservedSamAttributeKeys.READ_GROUP
					){
				SamAttribute attr =new SamAttribute(k, createValueFor(k.getType()));
				sut.validate(header, attr);
			}
		}
	}
	
	@Test
	public void libraryShouldBeInHeader() throws InvalidAttributeException{
		ReadGroup group = new ReadGroup.Builder(readGroupId)
										.setLibrary(libId)
										.build();
		SamHeader header = new SamHeader.Builder()
								.addReadGroup(group)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.LIBRARY, libId);
		sut.validate(header, attr);
	}
	@Test
	public void libraryShouldBeInHeaderWithMultipleLibraries() throws InvalidAttributeException{
		ReadGroup group1 = new ReadGroup.Builder(readGroupId)
										.setLibrary(libId)
										.build();
		ReadGroup group2 = new ReadGroup.Builder(readGroupId + 2)
											.setLibrary("not"+libId)
											.build();
		SamHeader header = new SamHeader.Builder()
								.addReadGroup(group2)
								.addReadGroup(group1)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.LIBRARY, libId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void libraryNotInHeaderWithMultipleLibrariesShouldThrowException() throws InvalidAttributeException{
		
		ReadGroup group1 = new ReadGroup.Builder(readGroupId)
										//null library id
										.build();
		ReadGroup group2 = new ReadGroup.Builder(readGroupId+2)
											.setLibrary("not"+libId)
											.build();
		SamHeader header = new SamHeader.Builder()
								.addReadGroup(group2)
								.addReadGroup(group1)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.LIBRARY, libId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void libraryNotInHeaderShouldThrowException() throws InvalidAttributeException{

		SamHeader header = new SamHeader.Builder()
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.LIBRARY, libId);
		sut.validate(header, attr);
	}
	
	@Test(expected = InvalidAttributeException.class)
	public void readGroupNotInHeaderShouldThrowException() throws InvalidAttributeException{

		SamHeader header = new SamHeader.Builder()
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.READ_GROUP, readGroupId);
		sut.validate(header, attr);
	}
	
	@Test
	public void readGroupIdShouldBeInHeader() throws InvalidAttributeException{
		ReadGroup group = new ReadGroup.Builder(readGroupId)
										.build();
		SamHeader header = new SamHeader.Builder()
								.addReadGroup(group)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.READ_GROUP, readGroupId);
		sut.validate(header, attr);
	}
	@Test
	public void readGroupIdShouldBeInHeaderWithMultipleReadGroups() throws InvalidAttributeException{
		ReadGroup group = new ReadGroup.Builder("not" +readGroupId)
												.build();
		ReadGroup group2 = new ReadGroup.Builder(readGroupId)
										.build();
		SamHeader header = new SamHeader.Builder()
								.addReadGroup(group)
								.addReadGroup(group2)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.READ_GROUP, readGroupId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void readGroupNotInHeaderWithMultipleReadGroupsShouldThrowException() throws InvalidAttributeException{
		ReadGroup group = new ReadGroup.Builder("not" +readGroupId)
												.build();
		ReadGroup group2 = new ReadGroup.Builder("not"+ readGroupId +"either")
										.build();
		SamHeader header = new SamHeader.Builder()
								.addReadGroup(group)
								.addReadGroup(group2)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.READ_GROUP, readGroupId);
		sut.validate(header, attr);
	}
	
	
	@Test
	public void platformUnitShouldBeInHeader() throws InvalidAttributeException{
		ReadGroup group = new ReadGroup.Builder(readGroupId)
										.setPlatformUnit(platformUnit)
										.build();
		SamHeader header = new SamHeader.Builder()
								.addReadGroup(group)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PLATFORMT_UNIT, platformUnit);
		sut.validate(header, attr);
	}
	@Test
	public void platformUnitShouldBeInHeaderWithMultipleLibraries() throws InvalidAttributeException{
		ReadGroup group1 = new ReadGroup.Builder(readGroupId)
												.setPlatformUnit(platformUnit)
												.build();
		ReadGroup group2 = new ReadGroup.Builder(readGroupId + 2)
											//null platformUnit
											.build();
		SamHeader header = new SamHeader.Builder()
								.addReadGroup(group2)
								.addReadGroup(group1)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PLATFORMT_UNIT, platformUnit);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void platformUnitNotInHeaderWithMultipleLibrariesShouldThrowException() throws InvalidAttributeException{
		
		ReadGroup group1 = new ReadGroup.Builder(readGroupId)
										//null library id
										.build();
		ReadGroup group2 = new ReadGroup.Builder(readGroupId+2)
											.setPlatformUnit("not"+platformUnit)
											.build();
		SamHeader header = new SamHeader.Builder()
								.addReadGroup(group2)
								.addReadGroup(group1)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PLATFORMT_UNIT, platformUnit);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void platformNotInHeaderShouldThrowException() throws InvalidAttributeException{

		SamHeader header = new SamHeader.Builder()
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PLATFORMT_UNIT, platformUnit);
		sut.validate(header, attr);
	}
	
	@Test
	public void programShouldBeInHeader() throws InvalidAttributeException{
		SamProgram program = new SamProgram.Builder(programId)
									.build();
		
		SamHeader header = new SamHeader.Builder()
								.addProgram(program)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PROGRAM, programId);
		sut.validate(header, attr);
	}
	@Test
	public void programShouldBeInHeaderWithMultiplePrograms() throws InvalidAttributeException{
		SamProgram program = new SamProgram.Builder(programId)
								.build();
		SamProgram otherProgram = new SamProgram.Builder("other"+programId)
										.build();
		SamHeader header = new SamHeader.Builder()
								.addProgram(otherProgram)
								.addProgram(program)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PROGRAM, programId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void programNotInHeaderWithMultipleProgramsShouldThrowException() throws InvalidAttributeException{
		
		SamProgram diffProgram = new SamProgram.Builder("diff"+programId)
									.build();
		SamProgram otherProgram = new SamProgram.Builder("other"+programId)
									.build();
		SamHeader header = new SamHeader.Builder()
				.addProgram(otherProgram)
				.addProgram(diffProgram)
				.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PROGRAM, programId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void programNotInHeaderShouldThrowException() throws InvalidAttributeException{

		SamHeader header = new SamHeader.Builder()
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PROGRAM, programId);
		sut.validate(header, attr);
	}
	
	
	private Object createValueFor(SamAttributeType type){
		switch(type){
			case BYTE_ARRAY_IN_HEX : return new byte[]{};
			case FLOAT : return 0F;
			case FLOAT_ARRAY : return new float[]{};
			case PRINTABLE_CHARACTER : return 'c';
			case SIGNED_BYTE_ARRAY : return new byte[]{};
			case SIGNED_INT : return 42;
			case SIGNED_INT_ARRAY : return new int[]{};
			case SIGNED_SHORT_ARRAY : return new short[]{};
			case STRING : return "string";
			case UNSIGNED_BYTE_ARRAY : return new UnsignedByteArray(new byte[]{});
			case UNSIGNED_INT_ARRAY : return new UnsignedIntArray(new int[]{});
			case UNSIGNED_SHORT_ARRAY : return new UnsignedShortArray(new short[]{});
			default:
				//can't happen
				throw new IllegalStateException("unknown type" + type);
		}
	}
}
