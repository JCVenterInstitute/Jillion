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
/**
 * {@code SamProgram} contains information about
 * a specific program and its invocation used in the pipeline
 * that generated this SAM File.
 * 
 * @author dkatzel
 *
 */
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
