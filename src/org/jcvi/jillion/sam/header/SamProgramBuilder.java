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

/**
 * {@code Builder} is the Builder object
 * used to create {@link SamProgram}
 * instances.
 * @author dkatzel
 *
 */
public final class SamProgramBuilder{
	private String id;

	private String name;
	private String version;
	private String description;
	private String commandLine;
	private String prevousProgramId;
	
	/**
	 * Create a new Builder object
	 * that will be used to create a new 
	 * {@link SamProgram} instance.
	 * @param id the SamProgram id to use;
	 * can not be null.
	 * @throws NullPointerException if id is null.
	 */
	public SamProgramBuilder(String id) {
		if(id ==null){
			throw new NullPointerException("id can not be null");
		}
		this.id = id;
	}

	public SamProgramBuilder(SamProgram copy) {
		this.id = copy.getId();
		this.name = copy.getName();
		this.version = copy.getVersion();
		this.description = copy.getDescription();
		this.prevousProgramId = copy.getPreviousProgramId();
		this.commandLine = copy.getCommandLine();
	}

	
	/**
	 * Get the name of the program.
	 * @return the name of this program as a String;
	 * may be null if this information is not provided.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Change the name of the program,
	 * if not called, then the built {@link SamProgram#getName()}
	 * will return null.
	 * @param name the name to set;
	 * may be null.
	 * @return this
	 */
	public SamProgramBuilder setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Version of the program.
	 * @return the version as a String;
	 * may be {@code null} if not provided.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Change the version of the program,
	 * if not called, then the built {@link SamProgram#getVersion()}
	 * will return null.
	 * @param version the version to set;
	 * may be null.
	 * @return this
	 */
	public SamProgramBuilder setVersion(String version) {
		this.version = version;
		return this;
	}

	/**
	 * Description of the program.
	 * @return the description of what this program
	 * does as a String;
	 * may be {@code null} if not provided.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Change the description of the program,
	 * if not called, then the built {@link SamProgram#getDescription()}
	 * will return null.
	 * @param description the description to set;
	 * may be null.
	 * @return this
	 */
	public SamProgramBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Get the Commandline invocation of the program.
	 * @return the commandline used to invoke this program
	 * does as a String;
	 * may be {@code null} if not provided.
	 */
	public String getCommandLine() {
		return commandLine;
	}

	/**
	 * Change the command line of the program,
	 * if not called, then the built {@link SamProgram#getCommandLine()}
	 * will return null.
	 * @param commandLine the commandLine to set;
	 * may be null.
	 * @return this
	 */
	public SamProgramBuilder setCommandLine(String commandLine) {
		this.commandLine = commandLine;
		return this;
	}


	
	/**
	 * Get the Id of the previous  {@link SamProgram}
	 * that operated on this SAM file.
	 * @return an Id String or
	 * {@code null} if this is the last
	 * (or only) program in the chain.
	 */
	public String getPrevousProgramId() {
		return prevousProgramId;
	}
	/**
	 * Change the previous program id of the program,
	 * if not called, then the built {@link SamProgram#getPreviousProgramId()}
	 * will return null.
	 * @param prevousProgramId the  previous program id to set;
	 * may be null.
	 * @return this
	 */
	public SamProgramBuilder setPrevousProgramId(String prevousProgramId) {
		this.prevousProgramId = prevousProgramId;
		return this;
	}


	/**
	 * Get the unique ID. The value of ID is used in the
	 * {@link org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys#PROGRAM}
	 * tag. PG IDs may be modified when merging SAM files in order to handle
	 * collisions.
	 * 
	 * @return a String; will never be null.
	 */
	public String getId() {
		return id;
	}
	/**
	 * Change the unique ID.
	 * IDs may be modified when merging SAM files in order to handle
	 * collisions.
	 * @param newId the new SamProgram id to use;
	 * can not be null.
	 * @throws NullPointerException if newId is null.
	 * @return this
	 */
	public SamProgramBuilder setId(String newId){
		if(newId ==null){
			throw new NullPointerException("new id can not be null");
		}
		this.id = newId;
		return this;
	}
	/**
	 * Create a new {@link SamProgram}
	 * instance using the fields set so far.
	 * @return a new {@link SamProgram} instance;
	 * will never be null.
	 */
	public SamProgramImpl build(){
		return new SamProgramImpl(this);
	}
	
	private static final class SamProgramImpl implements SamProgram {

	        private final String id;
	        private final String name;
	        private final String version;
	        private final String description;
	        private final String commandLine;
	        private final String previousProgramid;
	        
	        SamProgramImpl(SamProgramBuilder builder){
	                this.id = builder.id;
	                this.name = builder.name;
	                this.version = builder.version;
	                this.description = builder.description;
	                this.commandLine = builder.commandLine;
	                this.previousProgramid = builder.prevousProgramId;
	        }
	        
	        
	        /**
	         * Get the unique ID. The value of ID is used in the
	         * {@link org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys#PROGRAM}
	         * tag.
	         * 
	         * @return a String; will never be null.
	         */
	        @Override
	    public String getId() {
	                return id;
	        }


	        /**
	         * Get the program name.
	         * @return the name of this program as a String;
	         * may be {@code null} if this information is not provided.
	         */
	        @Override
	    public String getName() {
	                return name;
	        }


	        /**
	         * Version of the program.
	         * @return the version as a String;
	         * may be {@code null} if not provided.
	         */
	        @Override
	    public String getVersion() {
	                return version;
	        }


	        /**
	         * Description of the program.
	         * @return the description of what this program
	         * does as a String;
	         * may be {@code null} if not provided.
	         */
	        @Override
	    public String getDescription() {
	                return description;
	        }


	        /**
	         * Get the Commandline invocation of the program.
	         * @return the commandline used to invoke this program
	         * does as a String;
	         * may be {@code null} if not provided.
	         */
	        @Override
	    public String getCommandLine() {
	                return commandLine;
	        }


	        /**
	         * Get the Id of the previous  {@link SamProgram}
	         * that operated on this SAM file.
	         * @return an Id String or
	         * {@code null} if this is the last
	         * (or only) program in the chain.
	         */
	        @Override
	    public String getPreviousProgramId() {
	                return previousProgramid;
	        }



	        @Override
	        public String toString() {
	                return "SamProgram [id=" + id + ", name=" + name + ", version=" + version
	                                + ", description=" + description + ", commandLine="
	                                + commandLine + ", previousProgramid=" + previousProgramid + "]";
	        }



	        @Override
	        public int hashCode() {
	                final int prime = 31;
	                int result = 1;
	                result = prime * result
	                                + ((commandLine == null) ? 0 : commandLine.hashCode());
	                result = prime * result
	                                + ((description == null) ? 0 : description.hashCode());
	                result = prime * result + ((id == null) ? 0 : id.hashCode());
	                result = prime * result + ((name == null) ? 0 : name.hashCode());
	                result = prime
	                                * result
	                                + ((previousProgramid == null) ? 0 : previousProgramid
	                                                .hashCode());
	                result = prime * result + ((version == null) ? 0 : version.hashCode());
	                return result;
	        }


	        @Override
	        public boolean equals(Object obj) {
	                if (this == obj) {
	                        return true;
	                }
	                if (obj == null) {
	                        return false;
	                }
	                if (!(obj instanceof SamProgram)) {
	                        return false;
	                }
	                SamProgram other = (SamProgram) obj;
	                if (id == null) {
	                        if (other.getId() != null) {
	                                return false;
	                        }
	                } else if (!id.equals(other.getId())) {
	                        return false;
	                }
	                if (commandLine == null) {
	                        if (other.getCommandLine() != null) {
	                                return false;
	                        }
	                } else if (!commandLine.equals(other.getCommandLine())) {
	                        return false;
	                }
	                if (description == null) {
	                        if (other.getDescription() != null) {
	                                return false;
	                        }
	                } else if (!description.equals(other.getDescription())) {
	                        return false;
	                }
	                
	                if (name == null) {
	                        if (other.getName() != null) {
	                                return false;
	                        }
	                } else if (!name.equals(other.getName())) {
	                        return false;
	                }
	                if (previousProgramid == null) {
	                        if (other.getPreviousProgramId() != null) {
	                                return false;
	                        }
	                } else if (!previousProgramid.equals(other.getPreviousProgramId())) {
	                        return false;
	                }
	                if (version == null) {
	                        if (other.getVersion() != null) {
	                                return false;
	                        }
	                } else if (!version.equals(other.getVersion())) {
	                        return false;
	                }
	                return true;
	        }
	}
}
