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
