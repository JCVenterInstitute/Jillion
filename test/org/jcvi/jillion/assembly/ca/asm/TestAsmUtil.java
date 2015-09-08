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
package org.jcvi.jillion.assembly.ca.asm;

import java.util.Arrays;
import java.util.Collections;

import org.jcvi.jillion.assembly.ca.asm.AsmUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestAsmUtil {
	String ungappedSequence = "ACGTACGTACGT";
	@Test
	public void computeGappedSequenceWithNoGaps(){
		
		assertEquals(ungappedSequence,
				AsmUtil.computeGappedSequence(
						asBuilder(ungappedSequence), 
						Collections.<Integer>emptyList())
						.toString()
						);
	}
	
	@Test
	public void computeGappedSequenceWith1Gap(){
		assertEquals("ACGTA-CGTACGT",
				AsmUtil.computeGappedSequence(
						asBuilder(ungappedSequence), 
						Arrays.asList(5))
						.toString());
	}
	@Test
	public void computeGappedSequenceWith2Gaps(){
		assertEquals("ACGTA-CG-TACGT",
				AsmUtil.computeGappedSequence(
						asBuilder(ungappedSequence), 
						Arrays.asList(5,8))
						.toString());
	}
	@Test
	public void computeGappedSequenceWith2ConsecutiveGaps(){
		assertEquals("AC--GT",
				AsmUtil.computeGappedSequence(
						asBuilder("ACGT"), 
						Arrays.asList(2,2))
						.toString());
	}
	
	
	private static NucleotideSequenceBuilder asBuilder(String s){
		return new NucleotideSequenceBuilder(s);
	}
}
