package org.jcvi.jillion.assembly.util.slice;

import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

public class SingleNucleotideSliceElement implements VariableWidthSliceElement<Nucleotide>{

	private int count;
	private byte nucleotideOrdinal;
	
	public SingleNucleotideSliceElement(Nucleotide base, int count){
		if(count <0){
			throw new IllegalArgumentException("count must be >=0");
		}
		nucleotideOrdinal = base.getOrdinalAsByte();
		this.count = count;
	}
	
	
	@Override
	public int getLength() {
		return 1;
	}


	@Override
	public List<Nucleotide> get() {

		return Collections.singletonList(Nucleotide.getByOrdinal(nucleotideOrdinal));
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
		result = prime * result + nucleotideOrdinal;
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
			if (nucleotideOrdinal != other.nucleotideOrdinal) {
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
		return "SingleNucleotideSliceElement ["	+ Nucleotide.getByOrdinal(nucleotideOrdinal) + ", count=" + count + "]";
	}
	
	

}
