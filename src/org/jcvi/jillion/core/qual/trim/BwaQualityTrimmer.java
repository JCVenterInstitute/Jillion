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
package org.jcvi.jillion.core.qual.trim;

import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
/**
 * {@code BwaQualityTrimmer} is a {@link QualityTrimmer}
 * implementation that uses the algorithm 
 * from BWA in the file bwaseqio.c.
 * @author dkatzel
 * @see <a href="http://bio-bwa.sourceforge.net/">Burrows Wheeler Aligner webpage</a>
 */
public class BwaQualityTrimmer implements QualityTrimmer {

	private final byte threshold;
	
	/**
	 * Create a new instance of BwaQualityTrimmer
	 * with the given  quality threshold.  The quality threshold is the 
	 * same value as the bwa -q option.
	 * @param threshold
	 */
	public BwaQualityTrimmer(PhredQuality threshold) {
		this.threshold = threshold.getQualityScore();
	}

	@Override
	public Range trim(QualitySequence qualities) {
		int goodQualityWindowLength= (int)qualities.getLength();
		//since BWA trimming is used on
		//fastq data, fastq quality profile
		//is starts off good then gets worse
		//so we only really have to look at the 5' end
		//since the beginning of the read should 
		//be the best quality.
		QualitySequenceBuilder reversedQualities = new QualitySequenceBuilder(qualities)
													.reverse();
		
		Iterator<PhredQuality> iter =reversedQualities.iterator();
		int currentLength=goodQualityWindowLength;
		int badnessFactor=0;
		int worstScore=0;
		//as soon as we have a negative "badness factor"
		//we have good enough quality
		while(iter.hasNext() && badnessFactor >=0){			
			byte qualityScore = iter.next().getQualityScore();
			badnessFactor +=threshold - qualityScore;
			
			
			if(badnessFactor > worstScore){
				worstScore = badnessFactor;
				goodQualityWindowLength=currentLength;
			}
			currentLength--;
		}
		//if(badnessFactor>=0){
		//there is an off by 1 error in bwa code
		//if badnessFactor is >=0 and currentLength is 0
		//this will say you didn't find a good range
		//even if the goodqualityWindowLength is ok
		//change code to match 
		if(currentLength==0){
			//never found a good window
			return new Range.Builder().build();
		}
		return new Range.Builder(goodQualityWindowLength).build();
	}

	
	
}
