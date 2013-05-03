/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.TraceEncoderException;
import org.jcvi.jillion.trace.chromat.ChromatogramWriter2;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestIOLibZTRChromatogramWriter {

	ResourceHelper RESOURCES = new ResourceHelper(TestIOLibZTRChromatogramWriter.class);

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testEncodeAndDecodeStream() throws TraceDecoderException, IOException, TraceEncoderException{
		ZtrChromatogram chromatogram = new ZtrChromatogramBuilder("id",RESOURCES.getFile("files/GBKAK82TF.ztr")).build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		ChromatogramWriter2 writer = new ZtrChromatogramWriterBuilder(out).build();
		writer.write(chromatogram);
		writer.close();
		ZtrChromatogram reParsed = new ZtrChromatogramBuilder("id",new ByteArrayInputStream(out.toByteArray())).build();
		
		assertEquals(chromatogram, reParsed);
		
	}
	
	@Test
	public void testEncodeAndDecodeFile() throws TraceDecoderException, IOException, TraceEncoderException{
		ZtrChromatogram chromatogram = new ZtrChromatogramBuilder("id",RESOURCES.getFile("files/GBKAK82TF.ztr")).build();
		
		File ztrFile =folder.newFile();
		ChromatogramWriter2 writer = new ZtrChromatogramWriterBuilder(ztrFile).build();
		writer.write(chromatogram);
		writer.close();
		ZtrChromatogram reParsed = new ZtrChromatogramBuilder("id",ztrFile).build();
		
		assertEquals(chromatogram, reParsed);
		
	}

}
