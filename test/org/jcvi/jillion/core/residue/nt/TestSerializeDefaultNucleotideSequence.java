package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
public class TestSerializeDefaultNucleotideSequence {
	
	private static final String BASES = "ACGTACGT-ACGTAAA";
	/**
	 * This is the bytes written to an object output stream of {@link #BASES}
	 * from 2013 when the serialization method was first written.
	 * Make sure we can always deserialize this as expected.
	 */
	private static final byte[] SERIALIZED_BYTES = new byte[] {

			-84, -19, 0, 5, 115, 114, 0, 89, 111, 114, 103, 46, 106, 99, 118, 105, 46,
			106, 105, 108, 108, 105, 111, 110, 46, 99, 111, 114, 101, 46, 114,
			101, 115, 105, 100, 117, 101, 46, 110, 116, 46, 68, 101, 102, 97,
			117, 108, 116, 78, 117, 99, 108, 101, 111, 116, 105, 100, 101, 83,
			101, 113, 117, 101, 110, 99, 101, 36, 68, 101, 102, 97, 117, 108,
			116, 78, 117, 99, 108, 101, 111, 116, 105, 100, 101, 83, 101, 113,
			117, 101, 110, 99, 101, 80, 114, 111, 120, 121, 89, -32, -86, 15,
			-60, -15, 30, -6, 2, 0, 1, 76, 0, 5, 98, 97, 115, 101, 115, 116, 0,
			18, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114,
			105, 110, 103, 59, 120, 112, 116, 0, 16, 65, 67, 71, 84, 65, 67,
			71, 84, 45, 65, 67, 71, 84, 65, 65, 65

	};
	NucleotideSequence EXPECTED_SEQUENCE = new NucleotideSequenceBuilder(BASES)
													.build();
	
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
