/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
	
	private final SamProgram sut = new SamProgramBuilder(id)
												.setDescription(description)
												.setCommandLine(commandLine)
												.setVersion(version)
												.setName(name)
												.setPrevousProgramId(previousProgramId)
												.build();
					
	@Test
	public void onlyIdSetEverythingElseIsNull(){
		SamProgram prog = new SamProgramBuilder(id).build();
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
		new SamProgramBuilder((String)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullCopyProgramShouldThrowNPE(){
		new SamProgramBuilder((SamProgram)null);
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
		SamProgram other = new SamProgramBuilder(sut)
								.build();
		TestUtil.assertEqualAndHashcodeSame(sut, other);
	}
	@Test(expected = NullPointerException.class)
	public void changingIdToNullShouldThrowNPE(){
		new SamProgramBuilder(id)
						.setId(null);
	}
	@Test
	public void differentIdShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setId("different" + id)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentCommandLineShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setCommandLine("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentDescriptionShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setDescription("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentNameShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setName("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentVersionShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setVersion("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void differentPreviousProgShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setPrevousProgramId("different")
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	///////////////////////
	@Test
	public void nullCommandLineShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setCommandLine(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullDescriptionShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setDescription(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullNameShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setName(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullVersionShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setVersion(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
	@Test
	public void nullPreviousProgShouldNotBeEqual(){
		SamProgram other = new SamProgramBuilder(sut)
									.setPrevousProgramId(null)
									.build();
		
		TestUtil.assertNotEqualAndHashcodeDifferent(sut, other);
	}
}
