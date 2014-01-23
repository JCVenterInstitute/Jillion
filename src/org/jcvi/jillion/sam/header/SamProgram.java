package org.jcvi.jillion.sam.header;
/**
 * {@code SamProgram} is a single
 * program that has been run to either
 * originally generate the data
 * that is contained in the SAM
 * or a program that has modified the SAM.
 * @author dkatzel
 *
 */
public class SamProgram {

	private final String id;
	private final String name;
	private final String version;
	private final String description;
	private final String commandLine;
	private final String previousProgramid;
	
	private SamProgram(Builder builder){
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
	public String getId() {
		return id;
	}


	/**
	 * Get the program name.
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
	 * Get the Id of the previous  {@link SamProgram}
	 * that operated on this SAM file.
	 * @return an Id String or
	 * {@code null} if this is the last
	 * (or only) program in the chain.
	 */
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
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (commandLine == null) {
			if (other.commandLine != null) {
				return false;
			}
		} else if (!commandLine.equals(other.commandLine)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (previousProgramid == null) {
			if (other.previousProgramid != null) {
				return false;
			}
		} else if (!previousProgramid.equals(other.previousProgramid)) {
			return false;
		}
		if (version == null) {
			if (other.version != null) {
				return false;
			}
		} else if (!version.equals(other.version)) {
			return false;
		}
		return true;
	}



	public static class Builder{
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
		public Builder(String id) {
			if(id ==null){
				throw new NullPointerException("id can not be null");
			}
			this.id = id;
		}

		public Builder(SamProgram copy) {
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
		public Builder setName(String name) {
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
		public Builder setVersion(String version) {
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
		public Builder setDescription(String description) {
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
		public Builder setCommandLine(String commandLine) {
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
		public Builder setPrevousProgramId(String prevousProgramId) {
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
		public Builder setId(String newId){
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
		public SamProgram build(){
			return new SamProgram(this);
		}
	}
}
