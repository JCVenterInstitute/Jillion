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

import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;



/**
 * {@code BlosumMatrices}
 * contains common BLOSUM (BLOck SUbstitution Matrix)
 * substitution matrices used for amino acid alignments.
 * 
 * Blosum matrices with high numbers are designed for comparing closely related sequences,
 * low numbers are designed for comparing distant related sequences.  
 * Scores within a BLOSUM are log-odds scores that measure, in an alignment,
 * the logarithm for the ratio of the likelihood of two amino acids appearing
 * with a biological sense and the likelihood of the same amino acids appearing by chance.
 * A positive score is given to the more likely substitutions while
 * a negative score is given to the less likely substitutions.
 * <p>
 * 
 * There are many different BLOSUM matrices which are designated with numbers (BLOSUM62, BLOSUM40 etc)
 * which represents the clustering percentage level.
 * The lower the number, the more distantly related the proteins begin aligned are.
 * The BLOSUM62 matrix with the amino acids in the table grouped according to
 * the chemistry of the side chain, as in (a). Each value in the
 * matrix is calculated by dividing the frequency of occurrence of
 * the amino acid pair in the BLOCKS database, clustered at the 62% level,
 * divided by the probability that the same two amino acids might align by chance.
 * The ratio is then converted to a logarithm and expressed as a log odds score.
 * <p>
 * BLOSUM matrices are usually scaled in half-bit units.
 * A score of zero indicates that the frequency with which a
 * given two amino acids were found aligned in the database was
 * as expected by chance, while a positive score indicates that the
 * alignment was found more often than by chance, and negative score
 * indicates that the alignment was found less often than by chance.
 * 
 * @author dkatzel
 *
 */
public final class BlosumMatrices {
	//matrices are lazy loaded from files.
	private static AminoAcidSubstitutionMatrix BLOSUM_30,BLOSUM_40, BLOSUM_50, BLOSUM_62, BLOSUM_90;
	
	private static ResourceHelper RESOURCE_HELPER = new ResourceHelper(BlosumMatrices.class);
	private BlosumMatrices(){
		//can not instantiate
	}
	/**
	 * Get the BLOSUM 30 matrix.
	 * @return an {@link AminoAcidSubstitutionMatrix} with BLOSUM 30 values;
	 * will never be null.
	 * 
	 * @since 5.1
	 */
	public static synchronized AminoAcidSubstitutionMatrix blosum30(){
		if(BLOSUM_30 ==null){
			BLOSUM_30 = getBlosumMatrix(30);
		}
		return BLOSUM_30;
	}
	/**
	 * Get the BLOSUM 40 matrix.
	 * @return an {@link AminoAcidSubstitutionMatrix} with BLOSUM 40 values;
	 * will never be null.
	 * 
	 * @since 5.1
	 */
	public static synchronized AminoAcidSubstitutionMatrix blosum40(){
		if(BLOSUM_40 ==null){
			BLOSUM_40 = getBlosumMatrix(40);
		}
		return BLOSUM_40;
	}
	/**
	 * Get the BLOSUM 50 matrix.
	 * @return an {@link AminoAcidSubstitutionMatrix} with BLOSUM 50 values;
	 * will never be null.
	 */
	public static synchronized AminoAcidSubstitutionMatrix blosum50(){
		if(BLOSUM_50 ==null){
			BLOSUM_50 = getBlosumMatrix(50);
		}
		return BLOSUM_50;
	}
	/**
	 * Get the BLOSUM 62 matrix which is the default substitution matrix 
	 * used by BLAST.
	 * @return an {@link AminoAcidSubstitutionMatrix} with BLOSUM 62 values;
	 * will never be null.
	 */
	public static synchronized AminoAcidSubstitutionMatrix blosum62(){
		if(BLOSUM_62 ==null){
			BLOSUM_62 = getBlosumMatrix(62);
		}
		return BLOSUM_62;
	}
	/**
	 * Get the BLOSUM 90 matrix.
	 * @return an {@link AminoAcidSubstitutionMatrix} with BLOSUM 90 values;
	 * will never be null.
	 */
	public static synchronized AminoAcidSubstitutionMatrix blosum90(){
		if(BLOSUM_90 ==null){
			BLOSUM_90 = getBlosumMatrix(90);
		}
		return BLOSUM_90;
	}
	
	
	private static final AminoAcidSubstitutionMatrix getBlosumMatrix(int value){
		String file = String.format("blosum%d.matrix",value);
		try {
			return AminoAcidSubstitutionMatrixFileParser.parse(
					RESOURCE_HELPER.getFileAsStream(file));
		} catch (IOException e) {
			throw new IllegalStateException("could not parse substitution matrix file", e);
		}
	}
	
}
