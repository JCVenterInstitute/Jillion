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
package org.jcvi.jillion.maq.bfa;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;
import org.junit.Test;

public class TestBinaryFastaFileWriter extends AbstractTestBinaryFastaFile{

	
	@Test
	public void writeAllRecordsShouldMatchMaqByteForByte() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		NucleotideFastaWriter writer = new BfaWriterBuilder(out)
													.endian(ByteOrder.LITTLE_ENDIAN)
													.build();
		
		writer.write(forward);
		writer.write(reverse);
		writer.close();
		byte[] actual = out.toByteArray();
		byte[] expected = IOUtil.toByteArray(getHelper().getFile("seqs.bfa"));
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void encodeBigEndian() throws IOException, DataStoreException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		NucleotideFastaWriter writer = new BfaWriterBuilder(out)
													.endian(ByteOrder.BIG_ENDIAN)
													.build();
		writer.write(forward);
		writer.write(reverse);
		writer.close();
		FastaParser parser = BfaParser.create(new ByteArrayInputStream(out.toByteArray()), ByteOrder.BIG_ENDIAN);
		
		NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(parser)
													.build();
		assertEquals(2, datastore.getNumberOfRecords());
		assertEquals(forward, datastore.get(forward.getId()));
		assertEquals(reverse, datastore.get(reverse.getId()));
	}
}
