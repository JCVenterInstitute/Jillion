package org.jcvi.common.core.align.pairwise;

import java.util.Arrays;

import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;

public final class DefaultNucleotideScoringMatrix implements NucleotideScoringMatrix{

	private final float[][] matrix;
	
	
	private DefaultNucleotideScoringMatrix(float[][] matrix) {
		this.matrix = matrix;
	}


	@Override
	public float getScore(Nucleotide a, Nucleotide b) {
		return getScore(a.getOrdinalAsByte(), b.getOrdinalAsByte());
	}

	
	private float getScore(byte ordinalOfBase1, byte ordinalOfBase2) {
		return matrix[ordinalOfBase1][ordinalOfBase2];
	}
	
	public static final class Builder implements org.jcvi.common.core.util.Builder<DefaultNucleotideScoringMatrix>{
		private final float[][] matrix;
		
		public Builder(float defaultScore){
			int size = Nucleotide.values().length;
			
			matrix = new float[size][size];
			for(int i=0; i<size; i++){
				Arrays.fill(matrix[i], defaultScore);
			}
		}
		
		public Builder setMatch(float matchScore){
			for(int i=0; i< matrix.length; i++){
				matrix[i][i] = matchScore;
			}
			return this;
		}
		public Builder ambiguityScore(float ambiguityScore){
			for(Nucleotide g : Nucleotides.parse("ACGT")){
				for(Nucleotide ambiguity : g.getAllPossibleAmbiguities()){
					set(g,ambiguity, ambiguityScore);
				}
	        }
			return this;
		}
		public Builder set(Nucleotide a, Nucleotide b, float score){
			int aOrdinal = a.ordinal();
			int bOrdinal = b.ordinal();
			matrix[aOrdinal][bOrdinal] = score;
			matrix[bOrdinal][aOrdinal] = score;
			return this;
		}
		@Override
		public DefaultNucleotideScoringMatrix build() {
			return new DefaultNucleotideScoringMatrix(matrix);
		}
		
	}

}
