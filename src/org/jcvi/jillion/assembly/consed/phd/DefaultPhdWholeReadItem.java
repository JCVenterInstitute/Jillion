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

import java.util.Collections;
import java.util.List;

class DefaultPhdWholeReadItem implements PhdWholeReadItem{

	private final List<String> lines;

	public DefaultPhdWholeReadItem(List<String> lines) {
		if(lines ==null){
			throw new NullPointerException("lines can not be null");
		}
		this.lines = Collections.unmodifiableList(lines);
	}

	@Override
	public List<String> getLines() {
		return lines;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lines == null) ? 0 : lines.hashCode());
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
		if (!(obj instanceof DefaultPhdWholeReadItem)) {
			return false;
		}
		DefaultPhdWholeReadItem other = (DefaultPhdWholeReadItem) obj;
		if (lines == null) {
			if (other.lines != null) {
				return false;
			}
		} else if (!lines.equals(other.lines)) {
			return false;
		}
		return true;
	}
	
}
