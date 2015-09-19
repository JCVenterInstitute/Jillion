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
package org.jcvi.jillion.experimental.align.blast;

import java.util.List;

public interface BlastHit {

	/**
	 * Get the Id of the Query sequence in this Hsp.
	 * @return a String; will never be null.
	 */
	String getQueryId();
	/**
	 * Get the Id of the Subject sequence in this Hsp.
	 * @return a String; will never be null.
	 */
    String getSubjectId();
    /**
	 * Get the defline of the Subject in this Hsp.
	 * @return a String; may be null if not specified.
	 */
    String getSubjectDefinition();
    
    Integer getQueryLength();
    
    Integer getSubjectLength();
    
    List<Hsp> getHsps();
    
    String getBlastDbName();
    
    String getBlastProgramName();
}
