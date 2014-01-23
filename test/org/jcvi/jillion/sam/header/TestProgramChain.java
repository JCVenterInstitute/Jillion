package org.jcvi.jillion.sam.header;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestProgramChain {

	String progId = "prog1";
	String progName = "foo";
	String version = "v1.0";
	String description = "blah  blah";
	String commandLine = "-bar 42 -baz bin";
	
	String progId2 = "prog2";
	String progName2 = "foo2";
	String version2 = "v1.5a";
	String description2 = "ei ei oh";
	String commandLine2 = "-fizz 987654321";
	
	@Test
	public void emptyProgramChain(){
		ProgramChain chain = new ProgramChain.Builder()
									.build();
		
		assertTrue(chain.getPrograms().isEmpty());
	}
	
	@Test
	public void oneProgramInChain(){
	
		ProgramChain.Builder sut = new ProgramChain.Builder();
		
		sut.createNewProgramBuilder(progId)
				.setName(progName)
				.setVersion(version)
				.setDescription(description)
				.setCommandLine(commandLine)
				;
		
		ProgramChain chain = sut.build();
		List<Program> programs = chain.getPrograms();
		assertEquals(1, programs.size());
		Program actual = programs.get(0);
		
		assertProgramCorrect(actual, 
				progId, progName, version, description,	commandLine);
		
	}
	
	@Test
	public void twoProgramInChain(){
	
		ProgramChain.Builder sut = new ProgramChain.Builder();
		
		sut.createNewProgramBuilder(progId)
				.setName(progName)
				.setVersion(version)
				.setDescription(description)
				.setCommandLine(commandLine)
				;
		
		sut.createNewProgramBuilder(progId2)
		.setName(progName2)
		.setVersion(version2)
		.setDescription(description2)
		.setCommandLine(commandLine2)
		.setPrevousProgramId(progId);
		;
		
		ProgramChain chain = sut.build();
		List<Program> programs = chain.getPrograms();
		assertEquals(2, programs.size());
		
		Program expectedProg1 = new Program.Builder(progId)
									.setName(progName)
									.setVersion(version)
									.setDescription(description)
									.setCommandLine(commandLine)
									.build();
		
		Program expectedProg2 = new Program.Builder(progId2)
		.setName(progName2)
		.setVersion(version2)
		.setDescription(description2)
		.setCommandLine(commandLine2)
		.setPreviousProgram(expectedProg1)
		.build();
	
		assertEquals(expectedProg2, programs.get(0));
	}

	private void assertProgramCorrect(Program actual, String progId,
			String progName, String version, String description,
			String commandLine) {
		assertProgramCorrect(actual, progId, progName, version, description, commandLine, null);
	}
	private void assertProgramCorrect(Program actual, String progId,
			String progName, String version, String description,
			String commandLine, Program prevProg) {
		assertEquals(progId, actual.getId());
		assertEquals(progName, actual.getName());
		assertEquals(version, actual.getVersion());
		assertEquals(description, actual.getDescription());
		assertEquals(commandLine, actual.getCommandLine());
		assertEquals(prevProg, actual.getPreviousProgram());
	}
	
}
