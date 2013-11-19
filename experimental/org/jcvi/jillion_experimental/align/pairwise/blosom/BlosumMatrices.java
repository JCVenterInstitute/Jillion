/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align.pairwise.blosom;

import org.jcvi.jillion.align.pairwise.AminoAcidScoringMatrix;
import org.jcvi.jillion.align.pairwise.PropertyFileAminoAcidScoringMatrix;


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
	private static AminoAcidScoringMatrix BLOSUM_50, BLOSUM_62, BLOSUM_90;
	
	private BlosumMatrices(){
		//can not instantiate
	}
	/**
	 * Get the BLOSUM 50 matrix.
	 * @return an {@link AminoAcidScoringMatrix} with BLOSUM 50 values;
	 * will never be null.
	 */
	public static synchronized AminoAcidScoringMatrix blosum50(){
		if(BLOSUM_50 ==null){
			BLOSUM_50 = getMatrix(50);
		}
		return BLOSUM_50;
	}
	/**
	 * Get the BLOSUM 62 matrix.
	 * @return an {@link AminoAcidScoringMatrix} with BLOSUM 62 values;
	 * will never be null.
	 */
	public static synchronized AminoAcidScoringMatrix blosum62(){
		if(BLOSUM_62 ==null){
			BLOSUM_62 = getMatrix(62);
		}
		return BLOSUM_62;
	}
	/**
	 * Get the BLOSUM 90 matrix.
	 * @return an {@link AminoAcidScoringMatrix} with BLOSUM 90 values;
	 * will never be null.
	 */
	public static synchronized AminoAcidScoringMatrix blosum90(){
		if(BLOSUM_90 ==null){
			BLOSUM_90 = getMatrix(90);
		}
		return BLOSUM_90;
	}
	
	
	private static final AminoAcidScoringMatrix getMatrix(int value){
		String file = String.format("blosum%d.matrix",value);
		return new PropertyFileAminoAcidScoringMatrix(
				BlosumMatrices.class.getResourceAsStream(file));
	}
}
