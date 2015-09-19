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
package org.jcvi.jillion.trace.chromat.ztr;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.ChromatogramWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestIOLibZTRChromatogramWriter {

	ResourceHelper RESOURCES = new ResourceHelper(TestIOLibZTRChromatogramWriter.class);

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testEncodeAndDecodeStream() throws IOException{
		ZtrChromatogram chromatogram = new ZtrChromatogramBuilder("id",RESOURCES.getFile("files/GBKAK82TF.ztr")).build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		ChromatogramWriter writer = new ZtrChromatogramWriterBuilder(out).build();
		writer.write(chromatogram);
		writer.close();
		ZtrChromatogram reParsed = new ZtrChromatogramBuilder("id",new ByteArrayInputStream(out.toByteArray())).build();
		
		assertEquals(chromatogram, reParsed);
		
	}
	
	@Test
	public void testEncodeAndDecodeFile() throws IOException{
		ZtrChromatogram chromatogram = new ZtrChromatogramBuilder("id",RESOURCES.getFile("files/GBKAK82TF.ztr")).build();
		
		File ztrFile =folder.newFile();
		ChromatogramWriter writer = new ZtrChromatogramWriterBuilder(ztrFile).build();
		writer.write(chromatogram);
		writer.close();
		ZtrChromatogram reParsed = new ZtrChromatogramBuilder("id",ztrFile).build();
		
		assertEquals(chromatogram, reParsed);
		
	}

}
