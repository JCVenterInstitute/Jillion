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
package org.jcvi.jillion.assembly.consed.phd;

import java.util.List;
/**
 * {@code PhdWholeReadItem} is specially
 * formatted optional additional information
 * about an entire phd record.  WholeReadItems can be 
 * used by Phrap/Consed to process reads differently.
 * For example, some whole read items designate which 
 * reads are mate pairs or which reads have faked data.
 * 
 * WholeReadItems contain free form text that will vary
 * by what kind of data is provided and which program
 * is producing or consuming these tags so a generic
 * {@link PhdWholeReadItem} object can only provide 
 * the free form data as Strings.
 * @author dkatzel
 *
 */
public interface PhdWholeReadItem {
	/**
	 * Get a list of all the lines
	 * contained in this WholeReadItem.
	 * Each String may still contain whitespace or end of line
	 * data.
	 * @return a list of Strings, one for each line
	 * in the read item; if no lines exist for this 
	 * item, then an empty list will be returned;
	 * will never return null.
	 */
	List<String> getLines();
	
	
}
