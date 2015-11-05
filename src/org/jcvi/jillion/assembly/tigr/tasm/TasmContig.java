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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.util.Date;

import org.jcvi.jillion.assembly.Contig;

/**
 * {@code TasmContig} is a {@link Contig}
 * that contains extra metadata attributes specific
 * to the TigrAssembler format.
 * @author dkatzel
 *
 *
 */
public interface TasmContig extends Contig<TasmAssembledRead>{
   /**
    * Get the TIGR Project Database
    * Sample Id for this contig 
    * (legacy TIGR systems often referred to 
    * this value as the bac id).
    * It is possible for multiple
    * contigs to have the same sample id
    * if the sample is in many pieces.
    * @return {@code null} if this contig
    * doesn't have a sample id; or a non-null
    * integer value if it does.
    */
   Integer getSampleId();
   /**
    * Get GUID that this contig
    * was assigned by 
    * the Celera Assembler.
    * 
    * @return {@code null} if this contig
    * doesn't have a Celera Assembler Id; or a non-null
    * Long value if it does.
    */
   Long getCeleraAssemblerId();
   
   /**
    * Get the TIGR Project Database 
    * assembly Id for for this
    * contig.
    * (legacy TIGR systems often referred to 
    * this value as the asmbl_id).
    * This value should be unique across
    * the sample sample id.
    * @return {@code null} if this contig
    * doesn't have a asmbl_id; or a non-null
    * integer value if it does.
    */
   Long getTigrProjectAssemblyId();
   /**
    * Get the method used to create this
	 * contig.  This value is often
	 * the name of the assembler used
	 * or the software program
	 * that generated this contig.
    * @return a String description 
    * of what software created this contig;
    * or null if no such information is provided.
    */
   String getAssemblyMethod();
   /**
    * Comment explaining what this contig is which 
	 * might include the name of the chromosome or
	 * segment this contig belongs.
    * @return a String; may be null
    * if no comment exists.
    */
   String getComment();
   /**
	 * Common name for this contig.
	 * Often this value is a combination 
	 * of the comment and sample id.
	 * @return a String; may be null
    * if no common name exists.
	 */
   String getCommonName();
   /**
    * Get the username 
    * of the last person
    * who edited this contig.
    * @return {@code null} 
    * if no edit information is available;
    * or the user name as a string.
    * @see #getEditDate()
    */
   String getEditPerson();
   /**
    * The date of the last edit to this contig.
    * @return the {@link Date}
    * when this contig was last edited;
    * or null if no edit information is available.
    * @see #getEditPerson()
    */
   Date getEditDate();
   /**
    * Is this contig circular.
    * @return {@code true} if this contig 
    * is circulr; {@code false} 
    * if linear.  Defaults to {@code false}.
    */
   boolean isCircular();
   /**
    * Get the average ungapped read coverage
    * as a double.
    * @return the coverage will always
    * by &ge; 0.
    */
   double getAvgCoverage();
   /**
    * Is this contig a TIGR "annotation" contig.
    * An annotation contig has information
    * such as average coveage and number of reads set
    * to non-zero values, but does not actually
    * contain the underlying reads.  It is essentially
    * only the consensus plus meta data.
    * @return {@code true} if this contig is an
    * annotation contig; {@code false} otherwise.
    */
   boolean isAnnotationContig();
}
