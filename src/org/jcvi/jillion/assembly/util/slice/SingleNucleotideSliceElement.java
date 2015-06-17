package org.jcvi.jillion.assembly.util.slice;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

public class SingleNucleotideSliceElement implements VariableWidthSliceElement<Nucleotide>{

	private final int count;
	private final Nucleotide base;
	
	public SingleNucleotideSliceElement(Nucleotide base, int count){
		if(count <0){
			throw new IllegalArgumentException("count must be >=0");
		}
		Objects.requireNonNull(base);
		this.base = base;
		this.count = count;
	}
	
	
	@Override
	public int getLength() {
		return 1;
	}


	@Override
	public List<Nucleotide> get() {

		return Collections.singletonList(base);
	}

	@Override
	public int getCount() {
		return count;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + base.hashCode();
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
		if(!(obj instanceof VariableWidthSliceElement)){
			return false;
		}
		//more efficient check if same type
		if (obj instanceof SingleNucleotideSliceElement) {
			SingleNucleotideSliceElement other = (SingleNucleotideSliceElement) obj;
			if (count != other.count) {
				return false;
			}
			if (base != other.base) {
				return false;
			}
			return true;
		}
		
		VariableWidthSliceElement<?> other = (VariableWidthSliceElement<?>) obj;
		//generic check
		if(count != other.getCount()){
			return false;
		}
		if(!get().equals(other.get())){
			return false;
		}
		return true;

	}
	@Override
	public String toString() {
		return "SingleNucleotideSliceElement ["	+ base + ", count=" + count + "]";
	}
	
	

}
