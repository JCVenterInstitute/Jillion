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
/*
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr;


import java.io.IOException;

import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChromatogramXMLSerializer;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramImpl;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramParser;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestZTRChromatogramParser {
	 private final static ResourceFileServer RESOURCES = new ResourceFileServer(TestZTRChromatogramParser.class);
		
    ZTRChromatogramParser sut = new ZTRChromatogramParser();
    ZTRChromatogramImpl expected;

    @Before
    public void setup() throws IOException{
    	expected = (ZTRChromatogramImpl)ChromatogramXMLSerializer.fromXML(
        		RESOURCES.getFileAsStream("files/GBKAK82TF.ztr.xml"));
    }
    @Test
    public void parse() throws TraceDecoderException, IOException{
        ZTRChromatogram actual =sut.decode(RESOURCES.getFileAsStream("files/GBKAK82TF.ztr"));
        assertEquals(expected,actual);
        assertEquals(actual.getClip(), expected.getClip());
    }
}
