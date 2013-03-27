package org.jcvi.common.examples;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class QualitySequenceExample {

	/**
	 * @param args
	 */
	@Test
	public void test(){
		QualitySequence quals = new QualitySequenceBuilder(new byte[]{20,30,40,40,50,60})
								.build();
		//the 4th offset (in zero based)
		//has a quality value of 50
		PhredQuality quality =quals.get(4);
		
		assertEquals(50, quality.getQualityScore());
		
		//a QV score of 50 is 10 ^(-5) = 0.00001
		//due to floating point approximations
		//it might say .000009 instead so we provide a delta range in this assertion
		assertEquals(0.00001D, quality.getErrorProbability() , 0.00001D);
	}

	@Test
	public void reverseQualityValues(){
		
		NucleotideSequenceBuilder seqBuilder = new NucleotideSequenceBuilder();
		
		
		QualitySequenceBuilder qualBuilder = new QualitySequenceBuilder();
		
		seqBuilder.reverseComplement();
		qualBuilder.reverse();
		
		
		NucleotideSequence reverseComplementedSequence = seqBuilder.build();
		QualitySequence reversedQualities = qualBuilder.build();
	}
}
