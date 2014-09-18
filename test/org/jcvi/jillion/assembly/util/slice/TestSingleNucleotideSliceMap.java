package org.jcvi.jillion.assembly.util.slice;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestSingleNucleotideSliceMap {

	@Test
	public void createSliceMap(){
		SingleNucleotideSliceMap actual = new SingleNucleotideSliceMap.Builder(4)
													.add(0, new NucleotideSequenceBuilder("ACGT").build())
													.add(2, new NucleotideSequenceBuilder(  "GT").build())
													.add(1, new NucleotideSequenceBuilder( "GG").build())
													.build();
		
		
		assertEquals(4, actual.getConsensusLength());
		assertEquals(4, actual.getNumberOfSlices());
		assertEquals(createSlice("A"), actual.getSlice(0));
		assertEquals(createSlice("CG"), actual.getSlice(1));
		assertEquals(createSlice("GGG"), actual.getSlice(2));
		assertEquals(createSlice("TT"), actual.getSlice(3));
		
	}
	
	private SingleNucleotideSlice createSlice(String bases){
		Iterator<Nucleotide> iter = new NucleotideSequenceBuilder(bases).iterator();
		SingleNucleotideSlice.Builder builder = new SingleNucleotideSlice.Builder();
		while(iter.hasNext()){
			builder.add(iter.next());
		}
		return builder.build();
	}
}
