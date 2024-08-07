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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

final class SingleNucleotideSlice implements VariableWidthSlice<Nucleotide, NucleotideSequence>{

	private static final int NUMBER_OF_NUCLEOTIDES = Nucleotide.values().length;
	/**
	 * Cache of all possible NucleotideSequences of 1 bp length.
	 */
	private static final Map<Nucleotide, NucleotideSequence> SEQ_MAP;
	
	private final Map<Nucleotide, SingleNucleotideSliceElement> map = new EnumMap<Nucleotide, SingleNucleotideSliceElement>(Nucleotide.class);
	private final NucleotideSequence refSeq;
	
	
	static{
		SEQ_MAP = new EnumMap<>(Nucleotide.class);
		
		for(Nucleotide n : Nucleotide.getDnaValues()){
			SEQ_MAP.put(n, new NucleotideSequenceBuilder(1).append(n).build());
		}
	}
	
	
	
	
	private SingleNucleotideSlice(Builder builder){
		for(int i=0; i<NUMBER_OF_NUCLEOTIDES; i++){
			int count = builder.counts[i];
			if(count>0){
				Nucleotide n = Nucleotide.getByOrdinal(i);
				map.put(n, new SingleNucleotideSliceElement(n, count));
			}
		}
		refSeq = SEQ_MAP.get(builder.ref);
	}
	
	
	
	@Override
	public NucleotideSequence getGappedReferenceSequence() {
		return refSeq;
	}

	public int getCountFor(Nucleotide n){
		Objects.requireNonNull(n);
		
		SingleNucleotideSliceElement ret =map.get(n);
		if(ret ==null){
			return 0;
		}
		
		return ret.getCount();
		
	}

	@Override
	public int getCountFor(List<Nucleotide> sliceElementSeq) {
		int seqLength = sliceElementSeq.size();
		if(seqLength !=1){
			return 0;
		}
		Nucleotide n =sliceElementSeq.get(0);
		return getCountFor(n);
		
	}



	@Override
	public int getSliceLength() {
		return 1;
	}



	@Override
	public int getCoverageDepth() {
		int coverage=0;
		for(VariableWidthSliceElement<Nucleotide> e : map.values()){
			coverage +=e.getCount();
		}
		return coverage;
	}



	@Override
	public Stream<SingleNucleotideSliceElement> elements() {
		return map.values().stream();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + map.hashCode();
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
		if( !(obj instanceof VariableWidthSlice)){
			return false;
		}
		if (obj instanceof SingleNucleotideSlice) {
			SingleNucleotideSlice other = (SingleNucleotideSlice) obj;
			if (!map.equals(other.map)) {
				return false;
			}
			return true;
		}
		VariableWidthSlice<?,?> other = (VariableWidthSlice<?,?>) obj;
		return map.equals(other.elements()
				.map(o-> (VariableWidthSlice<?,?>)o)
				.collect(Collectors.toList()));
	}

	public static class Builder{
		private int[] counts = new int[NUMBER_OF_NUCLEOTIDES];
		private final Nucleotide ref;
		
		public Builder(Nucleotide ref) {
			this.ref = ref;
		}

		public Builder add(Nucleotide n){
			counts[n.getOrdinalAsByte()]++;
			return this;
		}
		
		public SingleNucleotideSlice build(){
			return new SingleNucleotideSlice(this);
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder("{");
			for(int i=0; i<counts.length; i++){
				if(counts[i] >0){
					b.append(Nucleotide.getDnaValues().get(i)).append(':').append(counts[i]).append(' ');
				}
			}
			b.append('}');
			return b.toString();
		}
	}

	@Override
	public String toString() {
		return "SingleNucleotideSlice [list=" + map + "]";
	}

}
