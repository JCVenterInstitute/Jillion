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
