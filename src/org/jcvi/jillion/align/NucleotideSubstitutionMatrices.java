package org.jcvi.jillion.align;

import java.io.File;
import java.io.FileNotFoundException;
/**
 * {@code NucleotideSubstitutionMatrices}
 * is a utility class containing 
 * common {@link NucleotideSubstitutionMatrix}
 * objects.
 * @author dkatzel
 *
 */
public final class NucleotideSubstitutionMatrices {

	private NucleotideSubstitutionMatrices(){
		//can not instantiate
	}
	
	private static final NucleotideSubstitutionMatrix NUC_4_4;
	
	private static final NucleotideSubstitutionMatrix IDENTITY = new NucleotideSubstitutionMatrixBuilder(-4)
																		.setMatch(1)
																		.build();
	static{
		try {
			NUC_4_4 = NucleotidePropertyFileScoringMatrixParser.parse(new File(NucleotideSubstitutionMatrices.class.getResource("nuc4.4.matrix").getFile()));
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("could not parse nuc4.4.matrix file", e);
		}
		
	}
	/**
	 * {@link NucleotideSubstitutionMatrix} created by 
	 * Todd Lowe on 12/10/1992 commonly referred to 
	 * as "NUC.4.4" and used by NCBI Blast .  The lowest score = -4, 
	 * and the Highest score = 5.  All probabilities are
	 * rounded to the nearest integer.
	 * 
	 * @return a {@link NucleotideSubstitutionMatrix}
	 * instance; never null.  The returned instance may
	 * be not be unique so synchronizing on the returned value
	 * is not recommended. 
	 */
	public static NucleotideSubstitutionMatrix getNuc44(){
		return NUC_4_4;
	}
	/**
	 * A {@link NucleotideSubstitutionMatrix} 
	 * that has a match score of {@code 1} and a mismatch
	 * score of {@code -4}.  This mismatch score
	 * allows users to provide different gap open and extension
	 * penalties to get different alignment results.
	 * @return a {@link NucleotideSubstitutionMatrix}
	 * instance; never null.  The returned instance may
	 * be not be unique so synchronizing on the returned value
	 * is not recommended.
	 */
	public static NucleotideSubstitutionMatrix getIdentityMatrix(){
		return IDENTITY;
	}
}
