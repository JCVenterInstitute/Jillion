package org.jcvi.jillion.sam.header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ProgramChain {

	private final List<Program> programs;
	
	private ProgramChain(List<Program> programs){
		this.programs = programs;
	}
	
	
	
	public List<Program> getPrograms() {
		//unmodifiable so we can hand out references
		return programs;
	}



	public static final class Builder{
		
		Map<String, Program.Builder> builderMap = new LinkedHashMap<String, Program.Builder>();
		
		public Builder(){
			//default constructor
		}
		public Program.Builder createNewProgramBuilder(String programId){
			if(builderMap.containsKey(programId)){
				throw new IllegalStateException("duplicate program ids not allowed" + programId);
				
			}
			Program.Builder programBuilder = new Program.Builder(programId);
			builderMap.put(programId, programBuilder);
			return programBuilder;
		}
		
		public ProgramChain build(){
			if(builderMap.isEmpty()){
				return new ProgramChain(Collections.<Program>emptyList());
			}
			if(builderMap.size() ==1){
				return new ProgramChain(Collections.singletonList(builderMap.values().iterator().next().build()));
				
			}
			Map<String, String> prevProgMap = new LinkedHashMap<String, String>();
			for(Program.Builder builder : builderMap.values()){
				String prevId = builder.getPrevousProgramId();
				if(prevId !=null){
					if(!builderMap.containsKey(prevId)){
						throw new IllegalStateException("unknown previous program id :" + prevId);
					}
					prevProgMap.put(builder.getId(), prevId);
				}
			}
			//only 1 program should not have a prev
			if(prevProgMap.size() != builderMap.size() -1){
				throw new IllegalStateException("invalid program chain, only 1 program can start a chain");
			}
			//find first program and build up from there
			Program firstProgram = findFirstProgram(prevProgMap)
										.build();
			LinkedList<Program> programList = new LinkedList<Program>();
			programList.add(firstProgram);
			Program prevProgram = firstProgram;
			String prevId = prevProgram.getId();				
			prevProgMap.remove(prevId);
			
			while(!prevProgMap.isEmpty()){
				Program.Builder nextProgramBuilder = findNextProgram(prevId);
				nextProgramBuilder.setPreviousProgram(prevProgram);
				Program nextProgram = nextProgramBuilder.build();
				programList.addFirst(nextProgram);
				prevProgram = nextProgram;
				prevId = prevProgram.getId();				
				prevProgMap.remove(prevId);
				
			}
			
			return new ProgramChain(Collections.unmodifiableList(programList));
			
		}

		private org.jcvi.jillion.sam.header.Program.Builder findNextProgram(
				String prevId) {
			for(Program.Builder builder : builderMap.values()){
				if(prevId.equals(builder.getPrevousProgramId())){
					return builder;
				}
			}
			return null;
		}

		private org.jcvi.jillion.sam.header.Program.Builder findFirstProgram(
				Map<String, String> prevProgMap) {
			for(Entry<String,Program.Builder> entry : builderMap.entrySet()){
				if(!prevProgMap.containsKey(entry.getKey())){
					return entry.getValue();
				}
			}
			//can't happen
			throw new IllegalStateException("could not find intial program");
		}
	}
}
