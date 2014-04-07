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
package org.jcvi.jillion.sam.header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
public class TestSamProgram {

	private final String id = "id";
	private final String description = "program descr";
	private final String commandLine = "-foo bar";
	private final String version = "1.0";
	private final String name = "name";
	private final String previousProgramId = "otherProgramId";
	
	private final SamProgram sut = new SamProgram.Builder(id)
												.setDescription(description)
												.setCommandLine(commandLine)
												.setVersion(version)
												.setName(name)
												.setPrevousProgramId(previousProgramId)
												.build();
					
	@Test
	public void onlyIdSetEverythingElseIsNull(){
		SamProgram prog = new SamProgram.Builder(id).build();
		assertEquals(id, prog.getId());
		assertNull(prog.getDescription());
		assertNull(prog.getCommandLine());
		assertNull(prog.getVersion());
		assertNull(prog.getName());
		assertNull(prog.getPreviousProgramId());
	}
	
	@Test
	public void allFieldsSet(){
		assertEquals(id, sut.getId());
		assertEquals(description, sut.getDescription());
		assertEquals(commandLine, sut.getCommandLine());		
		assertEquals(version, sut.getVersion());
		assertEquals(name, sut.getName());
		assertEquals(previousProgramId, sut.getPreviousProgramId());
	}
	
	@Test(expected = NullPointerException.class)
	public void nullIdShouldThrowNPE(){
		new SamProgram.Builder((String)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullCopyProgramShouldThrowNPE(){
		new SamProgram.Builder((SamProgram)null);
	}
	
	@Test
	public void notEqualToNull(){
		assertFalse(sut.equals(null));
	}
	@Test
	public void notEqualToOtherClass(){
		assertFalse(sut.equals("not a Program"));
	}
	@Test
	public void equalsSameRef(){
		TestUtil.assertEqualAndHashcodeSame(sut, sut);
	}
	@Test
	public void equalsSameValues(){
		SamProgram other = new SamProgram.Builder(sut)
								.build();
		TestUtil.assertEqualAndHashcodeSame(sut, other);
	}
	@Test(expected = NullPointerException.class)
	public void changingIdToNullShouldThrowNPE(){
		new SamProgram.Builder(id)
						.setId(null);
	}
	@Test
	public void differentIdShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setId("different" + id)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentCommandLineShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setCommandLine("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentDescriptionShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setDescription("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentNameShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setName("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentVersionShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setVersion("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentPreviousProgShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setPrevousProgramId("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	///////////////////////
	@Test
	public void nullCommandLineShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setCommandLine(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullDescriptionShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setDescription(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullNameShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setName(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullVersionShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setVersion(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullPreviousProgShouldNotBeEqual(){
		SamProgram other = new SamProgram.Builder(sut)
									.setPrevousProgramId(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
}
