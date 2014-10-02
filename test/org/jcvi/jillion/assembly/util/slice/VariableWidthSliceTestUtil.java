package org.jcvi.jillion.assembly.util.slice;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class VariableWidthSliceTestUtil {

	public static NucleotideSequence seq(String bases){
		return new NucleotideSequenceBuilder(bases).build();
	}
	
	
	public static VariableWidthNucleotideSlice createSlice(NucleotideSequence ref,String...triplets){
		
		List<List<Nucleotide>> elements = new ArrayList<>();
		for(String triple : triplets){
			char[] chars =triple.toCharArray();
			List<Nucleotide> ns = new ArrayList<>(chars.length);
			for(int i=0; i<chars.length; i++){
				ns.add(Nucleotide.parse(chars[i]));
			}
			elements.add(ns);
		}
		
		int max = elements.stream().mapToInt(l -> l.size()).max().orElse(0);
		VariableWidthNucleotideSlice.Builder builder = new VariableWidthNucleotideSlice.Builder(ref);
		for(List<Nucleotide> element : elements){
			builder.add(element);
		}
		return builder.build();
	}
}
