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
package org.jcvi.jillion.assembly.util.slice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.JoinedStringBuilder;

public class VariableWidthNucleotideSliceElement implements VariableWidthSliceElement<Nucleotide>{

	private final int count;
	private final List<Nucleotide> list;
	
	public VariableWidthNucleotideSliceElement(List<Nucleotide> list, int count) {
		this.list = new ArrayList<Nucleotide>(list);
		this.count = count;
	}

	@Override
	public List<Nucleotide> get() {
		return Collections.unmodifiableList(list);
	}

	@Override
	public int getLength() {
		return list.size();
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		return JoinedStringBuilder.create(list)
									.prefix("[")
									.suffix(" = " + count + "]")
									.build();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + list.hashCode();
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
		if (!(obj instanceof VariableWidthSliceElement)) {
			return false;
		}
		VariableWidthSliceElement<?> other = (VariableWidthSliceElement<?>) obj;
		if (count != other.getCount()) {
			return false;
		}
		if (!list.equals(other.get())) {
			return false;
		}
		return true;
	}
	
	

}
