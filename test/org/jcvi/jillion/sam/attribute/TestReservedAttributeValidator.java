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
package org.jcvi.jillion.sam.attribute;

import org.jcvi.jillion.core.util.UnsignedByteArray;
import org.jcvi.jillion.core.util.UnsignedIntArray;
import org.jcvi.jillion.core.util.UnsignedShortArray;
import org.jcvi.jillion.sam.header.SamProgramBuilder;
import org.jcvi.jillion.sam.header.SamReadGroup;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
import org.jcvi.jillion.sam.header.SamProgram;
import org.jcvi.jillion.sam.header.SamReadGroupBuilder;
import org.junit.Test;

public class TestReservedAttributeValidator {

	private final ReservedAttributeValidator sut = ReservedAttributeValidator.INSTANCE;
	private final String libId = "libraryId";
	private final String readGroupId ="readGroupId";
	private final String platformUnit = "platformUnit";
	private final String programId = "programId";
	
	@Test
	public void defaultValidatorShouldDoNothing() throws InvalidAttributeException{
		SamHeader header = new SamHeaderBuilder().build();
		
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
		SamReadGroup group = new SamReadGroupBuilder(readGroupId)
										.setLibrary(libId)
										.build();
		SamHeader header = new SamHeaderBuilder()
								.addReadGroup(group)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.LIBRARY, libId);
		sut.validate(header, attr);
	}
	@Test
	public void libraryShouldBeInHeaderWithMultipleLibraries() throws InvalidAttributeException{
		SamReadGroup group1 = new SamReadGroupBuilder(readGroupId)
										.setLibrary(libId)
										.build();
		SamReadGroup group2 = new SamReadGroupBuilder(readGroupId + 2)
											.setLibrary("not"+libId)
											.build();
		SamHeader header = new SamHeaderBuilder()
								.addReadGroup(group2)
								.addReadGroup(group1)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.LIBRARY, libId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void libraryNotInHeaderWithMultipleLibrariesShouldThrowException() throws InvalidAttributeException{
		
		SamReadGroup group1 = new SamReadGroupBuilder(readGroupId)
										//null library id
										.build();
		SamReadGroup group2 = new SamReadGroupBuilder(readGroupId+2)
											.setLibrary("not"+libId)
											.build();
		SamHeader header = new SamHeaderBuilder()
								.addReadGroup(group2)
								.addReadGroup(group1)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.LIBRARY, libId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void libraryNotInHeaderShouldThrowException() throws InvalidAttributeException{

		SamHeader header = new SamHeaderBuilder()
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.LIBRARY, libId);
		sut.validate(header, attr);
	}
	
	@Test(expected = InvalidAttributeException.class)
	public void readGroupNotInHeaderShouldThrowException() throws InvalidAttributeException{

		SamHeader header = new SamHeaderBuilder()
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.READ_GROUP, readGroupId);
		sut.validate(header, attr);
	}
	
	@Test
	public void readGroupIdShouldBeInHeader() throws InvalidAttributeException{
		SamReadGroup group = new SamReadGroupBuilder(readGroupId)
										.build();
		SamHeader header = new SamHeaderBuilder()
								.addReadGroup(group)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.READ_GROUP, readGroupId);
		sut.validate(header, attr);
	}
	@Test
	public void readGroupIdShouldBeInHeaderWithMultipleReadGroups() throws InvalidAttributeException{
		SamReadGroup group = new SamReadGroupBuilder("not" +readGroupId)
												.build();
		SamReadGroup group2 = new SamReadGroupBuilder(readGroupId)
										.build();
		SamHeader header = new SamHeaderBuilder()
								.addReadGroup(group)
								.addReadGroup(group2)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.READ_GROUP, readGroupId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void readGroupNotInHeaderWithMultipleReadGroupsShouldThrowException() throws InvalidAttributeException{
		SamReadGroup group = new SamReadGroupBuilder("not" +readGroupId)
												.build();
		SamReadGroup group2 = new SamReadGroupBuilder("not"+ readGroupId +"either")
										.build();
		SamHeader header = new SamHeaderBuilder()
								.addReadGroup(group)
								.addReadGroup(group2)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.READ_GROUP, readGroupId);
		sut.validate(header, attr);
	}
	
	
	@Test
	public void platformUnitShouldBeInHeader() throws InvalidAttributeException{
		SamReadGroup group = new SamReadGroupBuilder(readGroupId)
										.setPlatformUnit(platformUnit)
										.build();
		SamHeader header = new SamHeaderBuilder()
								.addReadGroup(group)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PLATFORMT_UNIT, platformUnit);
		sut.validate(header, attr);
	}
	@Test
	public void platformUnitShouldBeInHeaderWithMultipleLibraries() throws InvalidAttributeException{
		SamReadGroup group1 = new SamReadGroupBuilder(readGroupId)
												.setPlatformUnit(platformUnit)
												.build();
		SamReadGroup group2 = new SamReadGroupBuilder(readGroupId + 2)
											//null platformUnit
											.build();
		SamHeader header = new SamHeaderBuilder()
								.addReadGroup(group2)
								.addReadGroup(group1)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PLATFORMT_UNIT, platformUnit);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void platformUnitNotInHeaderWithMultipleLibrariesShouldThrowException() throws InvalidAttributeException{
		
		SamReadGroup group1 = new SamReadGroupBuilder(readGroupId)
										//null library id
										.build();
		SamReadGroup group2 = new SamReadGroupBuilder(readGroupId+2)
											.setPlatformUnit("not"+platformUnit)
											.build();
		SamHeader header = new SamHeaderBuilder()
								.addReadGroup(group2)
								.addReadGroup(group1)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PLATFORMT_UNIT, platformUnit);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void platformNotInHeaderShouldThrowException() throws InvalidAttributeException{

		SamHeader header = new SamHeaderBuilder()
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PLATFORMT_UNIT, platformUnit);
		sut.validate(header, attr);
	}
	
	@Test
	public void programShouldBeInHeader() throws InvalidAttributeException{
		SamProgram program = new SamProgramBuilder(programId)
									.build();
		
		SamHeader header = new SamHeaderBuilder()
								.addProgram(program)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PROGRAM, programId);
		sut.validate(header, attr);
	}
	@Test
	public void programShouldBeInHeaderWithMultiplePrograms() throws InvalidAttributeException{
		SamProgram program = new SamProgramBuilder(programId)
								.build();
		SamProgram otherProgram = new SamProgramBuilder("other"+programId)
										.build();
		SamHeader header = new SamHeaderBuilder()
								.addProgram(otherProgram)
								.addProgram(program)
								.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PROGRAM, programId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void programNotInHeaderWithMultipleProgramsShouldThrowException() throws InvalidAttributeException{
		
		SamProgram diffProgram = new SamProgramBuilder("diff"+programId)
									.build();
		SamProgram otherProgram = new SamProgramBuilder("other"+programId)
									.build();
		SamHeader header = new SamHeaderBuilder()
				.addProgram(otherProgram)
				.addProgram(diffProgram)
				.build();
		
		SamAttribute attr = new SamAttribute(ReservedSamAttributeKeys.PROGRAM, programId);
		sut.validate(header, attr);
	}
	@Test(expected = InvalidAttributeException.class)
	public void programNotInHeaderShouldThrowException() throws InvalidAttributeException{

		SamHeader header = new SamHeaderBuilder()
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
