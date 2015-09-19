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

import java.util.Iterator;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class SingleNucleotideSliceMap implements VariableWidthSliceMap<Nucleotide, NucleotideSequence>{

	private final SingleNucleotideSlice[] slices;
	
	private SingleNucleotideSliceMap(Builder builder){
		slices = new SingleNucleotideSlice[builder.builders.length];
		
		for(int i=0; i<slices.length; i++){
			slices[i] = builder.builders[i].build();
		}
	}
	
	@Override
	public SingleNucleotideSlice getSlice(int offset) {
		return slices[offset];
	}

	@Override
	public int getConsensusLength() {
		return slices.length;
	}
	
	
	
	@Override
	public int getNumberOfSlices() {
		//each slice is only 1 so number of slices matches length
		return getConsensusLength();
	}



	public static class Builder{
		private final SingleNucleotideSlice.Builder[] builders;
		
		public Builder(NucleotideSequence gappedReference){
			builders = new SingleNucleotideSlice.Builder[(int)gappedReference.getLength()];
			Iterator<Nucleotide> iter = gappedReference.iterator();
			int i=0;
			while(iter.hasNext()){
				builders[i++] = new SingleNucleotideSlice.Builder(iter.next());
			}
		}
		
		public Builder add(int offset, NucleotideSequence seq){
			int currentOffset = offset;
			Iterator<Nucleotide> iter = seq.iterator();
			
			while(iter.hasNext()){
				builders[currentOffset++].add(iter.next());
			}
			
			return this;
		}
		
		public SingleNucleotideSliceMap build(){
			return new SingleNucleotideSliceMap(this);
		}
	}

}
