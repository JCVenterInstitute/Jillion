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
package org.jcvi.jillion.assembly.util.slice;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestSingleNucleotideSliceMap {

	@Test
	public void createSliceMap(){
		SingleNucleotideSliceMap actual = new SingleNucleotideSliceMap.Builder(new NucleotideSequenceBuilder("ACGT").build())
													.add(0, new NucleotideSequenceBuilder("ACGT").build())
													.add(2, new NucleotideSequenceBuilder(  "GT").build())
													.add(1, new NucleotideSequenceBuilder( "GG").build())
													.build();
		
		
		assertEquals(4, actual.getConsensusLength());
		assertEquals(4, actual.getNumberOfSlices());
		assertEquals(createSlice('A', "A"), actual.getSlice(0));
		assertEquals(createSlice('C',"CG"), actual.getSlice(1));
		assertEquals(createSlice('G',"GGG"), actual.getSlice(2));
		assertEquals(createSlice('T',"TT"), actual.getSlice(3));
		
	}
	
	private SingleNucleotideSlice createSlice(char ref, String bases){
		Iterator<Nucleotide> iter = new NucleotideSequenceBuilder(bases).iterator();
		SingleNucleotideSlice.Builder builder = new SingleNucleotideSlice.Builder(Nucleotide.parse(ref));
		while(iter.hasNext()){
			builder.add(iter.next());
		}
		return builder.build();
	}
}
