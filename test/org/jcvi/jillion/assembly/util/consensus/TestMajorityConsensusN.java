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
package org.jcvi.jillion.assembly.util.consensus;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceBuilder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.junit.Test;

public class TestMajorityConsensusN {
	PhredQuality qual = PhredQuality.valueOf(30);
	Direction dir = Direction.FORWARD;
	private Slice slice = new SliceBuilder()
								.add("1", Nucleotide.Unknown, qual, dir)
								.add("2", Nucleotide.Unknown, qual, dir)
								.add("3", Nucleotide.Unknown, qual, dir)
								.add("4", Nucleotide.Unknown, qual, dir)
								.add("5", Nucleotide.Unknown, qual, dir)
								.add("6", Nucleotide.Unknown, qual, dir)
								.add("7", Nucleotide.Unknown, qual, dir)
								.build();
	@Test
	public void conic(){
		assertEquals(Nucleotide.Gap, new ConicConsensusCaller(qual).callConsensus(slice).getConsensus());
	}
	@Test
	public void mostFrequentACGT(){
		assertEquals(Nucleotide.Unknown, MostFrequentBasecallConsensusCaller.INSTANCE.callConsensus(slice).getConsensus());
	}
	
	@Test
	public void noAmbiguities(){
		assertEquals(Nucleotide.Adenine, new NoAmbiguityConsensusCaller(qual).callConsensus(slice).getConsensus());
		
	}
	@Test
	public void acgtn(){
		assertEquals(Nucleotide.Unknown, new AcgtnConsensusCaller(qual).callConsensus(slice).getConsensus());
		
	}
}
