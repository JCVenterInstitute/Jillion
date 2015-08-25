/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
/**
 * {@code NucleotideSubstitutionMatrices}
 * is a utility class containing 
 * common {@link NucleotideSubstitutionMatrix}
 * objects.
 * @author dkatzel
 *
 */
public final class NucleotideSubstitutionMatrices {

	
	
	private static final NucleotideSubstitutionMatrix NUC_4_4;
	
	private static final NucleotideSubstitutionMatrix IDENTITY = new NucleotideSubstitutionMatrixBuilder(-4)
																		.setMatch(1)
																		.build();
	static{
		try {
			NUC_4_4 = NucleotidePropertyFileScoringMatrixParser.parse(NucleotideSubstitutionMatrices.class.getResourceAsStream("nuc4.4.matrix"));
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("could not parse nuc4.4.matrix file", e);
		}
		
	}
	
	private NucleotideSubstitutionMatrices(){
		//can not instantiate
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
	
	/**
	 * Parse an {@link InputStream} which is a text file
	 * of a single nucleotide substitution matrix.
	 * <p/>
	 * Rules for parsing file:
	 * <ol>
	 * <li>Any lines that start with '#' are ignored</li>
	 * <li>First line of data must be the order of the nucleotides in the matrix separated by white space</li>
	 * <li>Each of the following lines must start with a nucleotide followed by the substitution scores.  Each element should be spearated by only whitespace</li>
	 * </ol>
	 * @param in the input stream to parse; can not be null.
	 * @return a new {@link NucleotideSubstitutionMatrix} with the given scores.
	 * 
	 * @throws IOException if there is a problem parsing the file.
	 */
	public static NucleotideSubstitutionMatrix parsePropertyFile(InputStream in) throws IOException{
		return NucleotidePropertyFileScoringMatrixParser.parse(in);
	}
}
