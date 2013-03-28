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
package org.jcvi.jillion.trace.sanger.chromat.ztr;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.TraceEncoderException;
import org.jcvi.jillion.trace.sanger.chromat.ztr.IOLibLikeZtrChromatogramWriter;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
public class TestIOLibZTRChromatogramWriter {

	ResourceHelper RESOURCES = new ResourceHelper(TestIOLibZTRChromatogramWriter.class);

	@Test
	public void testEncodeAndDecode() throws FileNotFoundException, TraceDecoderException, IOException, TraceEncoderException{
		ZtrChromatogram chromatogram = new ZtrChromatogramBuilder("id",RESOURCES.getFile("files/GBKAK82TF.ztr")).build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOLibLikeZtrChromatogramWriter.INSTANCE.write(chromatogram, out);
		ZtrChromatogram reParsed = new ZtrChromatogramBuilder("id",new ByteArrayInputStream(out.toByteArray())).build();
		
		assertEquals(chromatogram, reParsed);
		
	}
	

}
