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
package org.jcvi.jillion.fasta.pos;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestDefaultPositionSequenceFastaRecordWriter {

	private final File peakFile;
	private final PositionFastaRecord fasta;
	public TestDefaultPositionSequenceFastaRecordWriter() throws IOException, DataStoreException{
		ResourceHelper resources = new ResourceHelper(TestDefaultPositionSequenceFastaRecordWriter.class);
		peakFile = resources.getFile("1119369023656.peak");
		
		fasta = DefaultPositionFastaFileDataStore.create(FastaFileParser.create(peakFile), id->true, null).get("1119369023656");
	}
	
	@Test
	public void rewriteShouldMatchExactly() throws IOException{
		
		String asString = asString(peakFile);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PositionFastaWriter sut = new PositionFastaWriterBuilder(out)
												.build();
		
		sut.write(fasta);
		sut.close();
		assertEquals(asString, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void differentCharSet() throws IOException{
		
		String asString = asString(peakFile);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Charset charset = Charset.forName("UTF-16");
		PositionFastaWriter sut = new PositionFastaWriterBuilder(out)
												.charset(charset)
												.build();
		
		sut.write(fasta);
		sut.close();
		assertEquals(asString, new String(out.toByteArray(), charset));
	}
	private String asString(File fastaFile) throws IOException{
		InputStream in = new FileInputStream(fastaFile);
		try{
			return IOUtil.toString(in);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}
}
