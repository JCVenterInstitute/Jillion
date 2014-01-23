package org.jcvi.jillion.sam.header;

public class Program {

	private final String id;
	private final String name;
	private final String version;
	private final String description;
	private final String commandLine;
	private final Program previousProgram;
	
	private Program(Builder builder){
		this.id = builder.id;
		this.name = builder.name;
		this.version = builder.version;
		this.description = builder.description;
		this.commandLine = builder.commandLine;
		this.previousProgram = builder.previousProgram;
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
	 * Program name.
	 * @return the name of this program as a String;
	 * may be {@code null} if this information is not provided.
	 */
	public String getName() {
		return name;
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
	 * Description of the program.
	 * @return the description of what this program
	 * does as a String;
	 * may be {@code null} if not provided.
	 */
	public String getDescription() {
		return description;
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
	 * Get the previous {@link Program}
	 * that operated on this SAM file.
	 * @return a {@link Program} of
	 * {@code null} if this is the last
	 * (or only) program in the chain.
	 */
	public Program getPreviousProgram() {
		return previousProgram;
	}



	@Override
	public String toString() {
		return "Program [id=" + id + ", name=" + name + ", version=" + version
				+ ", description=" + description + ", commandLine="
				+ commandLine + ", previousProgram=" + previousProgram + "]";
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
		result = prime * result
				+ ((previousProgram == null) ? 0 : previousProgram.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Program other = (Program) obj;
		if (commandLine == null) {
			if (other.commandLine != null)
				return false;
		} else if (!commandLine.equals(other.commandLine))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (previousProgram == null) {
			if (other.previousProgram != null)
				return false;
		} else if (!previousProgram.equals(other.previousProgram))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}



	public static class Builder{
		private final String id;

		private String name;
		private String version;
		private String description;
		private String commandLine;
		private Program previousProgram;
		private String prevousProgramName;
		
		
		public Builder(String id) {
			this.id = id;
		}

		/**
		 * Program name.
		 * @return the name of this program as a String;
		 * may be null if this information is not provided.
		 */
		public String getName() {
			return name;
		}


		public Builder setName(String name) {
			this.name = name;
			return this;
		}


		public String getVersion() {
			return version;
		}


		public Builder setVersion(String version) {
			this.version = version;
			return this;
		}


		public String getDescription() {
			return description;
		}


		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}


		public String getCommandLine() {
			return commandLine;
		}


		public Builder setCommandLine(String commandLine) {
			this.commandLine = commandLine;
			return this;
		}


		

		public String getPrevousProgramId() {
			return prevousProgramName;
		}

		public Builder setPrevousProgramId(String prevousProgramName) {
			this.prevousProgramName = prevousProgramName;
			return this;
		}

		Builder setPreviousProgram(Program previousProgram) {
			this.previousProgram = previousProgram;
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
		
		Program build(){
			return new Program(this);
		}
	}
}
