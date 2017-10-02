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
package org.jcvi.jillion.align;

import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.Builder;

/**
 * {@code NucleotideSubstitutionMatrixBuilder} is a Builder
 * that will create new {@link NucleotideSubstitutionMatrix}
 * instances using the given configuration.
 * @author dkatzel
 *
 */
public final class NucleotideSubstitutionMatrixBuilder  implements Builder<NucleotideSubstitutionMatrix>{
		
		
		private final float[][] matrix;
		/**
		 * Create a new Builder instance where the matrix
		 * is initialized so all substitution scores default 
		 * to the given default score value.
		 * All calls to the mutator methods will further modify 
		 * the matrix.
		 * @param defaultScore the substitution value a substitution score
		 * should have if it does not get changed by other mutator
		 * method calls in this Builder before {@link #build()}
		 * is called.
		 */
		public NucleotideSubstitutionMatrixBuilder(float defaultScore){
			int size = Nucleotide.getAllValues().size();
			
			matrix = new float[size][size];
			for(int i=0; i<size; i++){
			    Arrays.fill(matrix[i], defaultScore);
			}
		}
		/**
		 * Sets the substitution score for all
		 * exact matches (The diagonal in the matrix).
		 * Any future calls to any mutator methods
		 * may modify some or all of the scores set by this value.
		 * @param matchScore the score of an exact match.
		 * @return this
		 */
		public NucleotideSubstitutionMatrixBuilder setMatch(float matchScore){
			for(int i=0; i< matrix.length; i++){
				matrix[i][i] = matchScore;
			}
			return this;
		}
		/**
		 * Sets the substitution score for all 
		 * possible combinations of non-ambiguous to ambiguous (The diagonal in the matrix).
		 * Any future calls to any mutator methods
		 * may modify some or all of the scores set by this value.
		 * @param ambiguityScore the score of an ambiguity match.
		 * @return this
		 */
		public NucleotideSubstitutionMatrixBuilder ambiguityScore(float ambiguityScore){
			List<Nucleotide> values = Nucleotide.getDnaValues();
			for(int i=0; i< values.size(); i++){
				Nucleotide n = values.get(i);
				if(n.isAmbiguity()){
					Arrays.fill(matrix[i], ambiguityScore);
					for(int row=0; row<matrix[0].length; row++){
						matrix[row][i] = ambiguityScore;
					}
				}
				
			}
			
			return this;
		}
		/**
		 * Explicitly set the substitution score 
		 * of the given two {@link Nucleotide}s
		 * to the given value.
		 * @param a one {@link Nucleotide}; can not be null.
		 * @param b the {@link Nucleotide}; can not be null
		 * @param score the substitution score from a to b or b to a.
		 * @return this
		 * @throws NullPointerException if a or b are null.
		 */
		public NucleotideSubstitutionMatrixBuilder set(Nucleotide a, Nucleotide b, float score){
			int i = a.ordinal();
			int j = b.ordinal();
			matrix[i][j] = score;
			matrix[j][i] = score;
			return this;
		}
		@Override
		public NucleotideSubstitutionMatrix build() {
			return new NucleotideSubstitutionMatrixImpl(matrix);
		}

	
	private static final class NucleotideSubstitutionMatrixImpl implements NucleotideSubstitutionMatrix{
		private final float[][] matrix;
		
		private NucleotideSubstitutionMatrixImpl(float[][] matrix) {
			this.matrix = matrix;
		}


		@Override
		public float getValue(Nucleotide a, Nucleotide b) {
			return getScore(a.getOrdinalAsByte(), b.getOrdinalAsByte());
		}

		
		private float getScore(byte ordinalOfBase1, byte ordinalOfBase2) {
			return matrix[ordinalOfBase1][ordinalOfBase2];
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(matrix);
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
			if (!(obj instanceof NucleotideSubstitutionMatrixImpl)) {
				return false;
			}
			NucleotideSubstitutionMatrixImpl other = (NucleotideSubstitutionMatrixImpl) obj;
			if (!Arrays.deepEquals(matrix, other.matrix)) {
				return false;
			}
			return true;
		}
		
		
	}

}
