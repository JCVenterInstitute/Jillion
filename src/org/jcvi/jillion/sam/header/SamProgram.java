package org.jcvi.jillion.sam.header;

public interface SamProgram {

    /**
     * Get the unique ID. The value of ID is used in the
     * {@link org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys#PROGRAM}
     * tag.
     * 
     * @return a String; will never be null.
     */
    String getId();

    /**
     * Get the program name.
     * @return the name of this program as a String;
     * may be {@code null} if this information is not provided.
     */
    String getName();

    /**
     * Version of the program.
     * @return the version as a String;
     * may be {@code null} if not provided.
     */
    String getVersion();

    /**
     * Description of the program.
     * @return the description of what this program
     * does as a String;
     * may be {@code null} if not provided.
     */
    String getDescription();

    /**
     * Get the Commandline invocation of the program.
     * @return the commandline used to invoke this program
     * does as a String;
     * may be {@code null} if not provided.
     */
    String getCommandLine();

    /**
     * Get the Id of the previous  {@link SamProgram}
     * that operated on this SAM file.
     * @return an Id String or
     * {@code null} if this is the last
     * (or only) program in the chain.
     */
    String getPreviousProgramId();

}