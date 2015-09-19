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
package org.jcvi.jillion.core.residue.aa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestProteinSequence {
	private final AminoAcid[] aminoAcids = AminoAcidUtil.parse("ILKMFDEX").toArray(new AminoAcid[8]);
	ProteinSequence sut;
	@Before
	public void setup(){
		sut = encode(aminoAcids);
	}
	protected abstract ProteinSequence encode(AminoAcid[] aminoAcids);
	
	@Test
	public void length(){
		assertEquals(aminoAcids.length, sut.getLength());
	}
	
	@Test
	public void decode(){
		for(int i=0; i<aminoAcids.length; i++){
			assertEquals(aminoAcids[i],sut.get(i));
		}
	}
	
	@Test
	public void singleBase(){
		AminoAcid[] expected = new AminoAcid[]{AminoAcidUtil.parse("L").get(0)};
		ProteinSequence seq = encode(expected);
		assertEquals(1, seq.getLength());
		assertEquals(expected[0],seq.get(0));
	}
	
	@Test
	public void get(){
		for(int i=0; i< aminoAcids.length; i++){
			assertEquals(aminoAcids[i], sut.get(i));
		}		
	}
	
	@Test
	public void noGaps(){
		assertEquals(0, sut.getNumberOfGaps());
		assertTrue(sut.getGapOffsets().isEmpty());
		assertEquals(aminoAcids.length, sut.getUngappedLength());
		for(int i=0; i< aminoAcids.length; i++){
			assertEquals(i, sut.getUngappedOffsetFor(i));
		}	
	}
	
	@Test
	public void serialze() throws IOException, ClassNotFoundException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(sut);
		oos.close();
		
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
	
		ProteinSequence deserializedSequence = (ProteinSequence)in.readObject();
		
		assertEquals(sut, deserializedSequence);
	}
	
	@Test
	public void hasPyrrolysine(){
		ProteinSequence seq = encode(
				AminoAcidUtil.parse("GTOSDAKIPDNQAGHEKTMTCLLPALAGANTLYGAGMLELGMTFSMEQLVIDNDIIKMTKK").toArray(new AminoAcid[0])
				);
		assertEquals(AminoAcid.Pyrrolysine, seq.get(2));
	}
	
	
}
