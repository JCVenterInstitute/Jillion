/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
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
 * @author dkatzel
 *
 */
public final class BlosumMatrices {
	//matrices are lazy loaded from files.
	private static AminoAcidSubstitutionMatrix BLOSUM_50, BLOSUM_62, BLOSUM_90;
	
	private static ResourceHelper RESOURCE_HELPER = new ResourceHelper(BlosumMatrices.class);
	private BlosumMatrices(){
		//can not instantiate
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
