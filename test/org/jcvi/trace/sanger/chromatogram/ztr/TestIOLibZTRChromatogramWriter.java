/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.trace.sanger.chromatogram.ztr;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.TraceEncoderException;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOLibZTRChromatogramWriter {

	ResourceFileServer RESOURCES = new ResourceFileServer(TestIOLibZTRChromatogramWriter.class);

	@Test
	public void testEncodeAndDecode() throws FileNotFoundException, TraceDecoderException, IOException, TraceEncoderException{
		ZTRChromatogram chromatogram = new ZTRChromatogramFile(RESOURCES.getFile("files/GBKAK82TF.ztr"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DefaultZTRChromatogramWriter.IO_LIB_ZTR_WRITER.write(chromatogram, out);
		ZTRChromatogramFile reParsed = new ZTRChromatogramFile();
		ByteArrayInputStream ztrStream = new ByteArrayInputStream(out.toByteArray());
		ZTRChromatogramFileParser.parseZTRFile(ztrStream, reParsed);
		
		assertEquals(chromatogram, reParsed);
		
	}
	

}
