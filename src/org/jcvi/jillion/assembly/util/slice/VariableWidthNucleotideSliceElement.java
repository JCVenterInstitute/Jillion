package org.jcvi.jillion.assembly.util.slice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

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
		return "VariableWidthNucleotideSliceElement [list=" + list + ", count="
				+ count + "]";
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
