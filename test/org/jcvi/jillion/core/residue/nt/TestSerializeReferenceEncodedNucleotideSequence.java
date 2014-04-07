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
package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
public class TestSerializeReferenceEncodedNucleotideSequence {
	
	private static final NucleotideSequence REFERENCE = new NucleotideSequenceBuilder("ACGTACGT-ACGTAAA")
																			.build();
	/**
	 * This is the bytes written to an object output stream of {@link #EXPECTED_SEQUENCE}
	 * from 2013 when the serialization method was first written.
	 * Make sure we can always deserialize this as expected.
	 */
	private static final byte[] SERIALIZED_BYTES = new byte[] {

			-84, -19, 0, 5, 115, 114, 0, 74, 111, 114, 103, 46, 106, 99, 118, 105, 46,
			106, 105, 108, 108, 105, 111, 110, 46, 99, 111, 114, 101, 46, 114,
			101, 115, 105, 100, 117, 101, 46, 110, 116, 46, 68, 101, 102, 97,
			117, 108, 116, 82, 101, 102, 101, 114, 101, 110, 99, 101, 69, 110,
			99, 111, 100, 101, 100, 78, 117, 99, 108, 101, 111, 116, 105, 100,
			101, 83, 101, 113, 117, 101, 110, 99, 101, 13, 7, 79, -77, -27,
			115, 97, 67, 2, 0, 4, 73, 0, 6, 108, 101, 110, 103, 116, 104, 73,
			0, 11, 115, 116, 97, 114, 116, 79, 102, 102, 115, 101, 116, 91, 0,
			15, 101, 110, 99, 111, 100, 101, 100, 83, 110, 112, 115, 73, 110,
			102, 111, 116, 0, 2, 91, 66, 76, 0, 9, 114, 101, 102, 101, 114,
			101, 110, 99, 101, 116, 0, 53, 76, 111, 114, 103, 47, 106, 99, 118,
			105, 47, 106, 105, 108, 108, 105, 111, 110, 47, 99, 111, 114, 101,
			47, 114, 101, 115, 105, 100, 117, 101, 47, 110, 116, 47, 78, 117,
			99, 108, 101, 111, 116, 105, 100, 101, 83, 101, 113, 117, 101, 110,
			99, 101, 59, 120, 112, 0, 0, 0, 16, 0, 0, 0, 0, 117, 114, 0, 2, 91,
			66, -84, -13, 23, -8, 6, 8, 84, -32, 2, 0, 0, 120, 112, 0, 0, 0, 5,
			1, 1, 1, 3, -64, 115, 114, 0, 89, 111, 114, 103, 46, 106, 99, 118,
			105, 46, 106, 105, 108, 108, 105, 111, 110, 46, 99, 111, 114, 101,
			46, 114, 101, 115, 105, 100, 117, 101, 46, 110, 116, 46, 68, 101,
			102, 97, 117, 108, 116, 78, 117, 99, 108, 101, 111, 116, 105, 100,
			101, 83, 101, 113, 117, 101, 110, 99, 101, 36, 68, 101, 102, 97,
			117, 108, 116, 78, 117, 99, 108, 101, 111, 116, 105, 100, 101, 83,
			101, 113, 117, 101, 110, 99, 101, 80, 114, 111, 120, 121, 89, -32,
			-86, 15, -60, -15, 30, -6, 2, 0, 1, 76, 0, 5, 98, 97, 115, 101,
			115, 116, 0, 18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47,
			83, 116, 114, 105, 110, 103, 59, 120, 112, 116, 0, 16, 65, 67, 71,
			84, 65, 67, 71, 84, 45, 65, 67, 71, 84, 65, 65, 65

	};
	ReferenceMappedNucleotideSequence EXPECTED_SEQUENCE = new NucleotideSequenceBuilder("ACGAACGT-ACGTAAA")
													.setReferenceHint(REFERENCE, 0)
													.buildReferenceEncodedNucleotideSequence();
	
	@Test
	public void serializeAndDeserializeObject() throws IOException, ClassNotFoundException{
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bytes);
		
		out.writeObject(EXPECTED_SEQUENCE);
		out.close();
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
		
		NucleotideSequence actual = (NucleotideSequence)in.readObject();
		
		assertEquals(EXPECTED_SEQUENCE, actual);
	}
	
	@Test
	public void backwardsCompatiable() throws Exception{
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(SERIALIZED_BYTES));
		
		NucleotideSequence actual = (NucleotideSequence)in.readObject();
		
		assertEquals(EXPECTED_SEQUENCE, actual);
	}
}
