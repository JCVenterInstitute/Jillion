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

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRChromatogramImpl;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.chromat.ChromatogramXMLSerializer;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestZTRChromatogramFile {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestZTRChromatogramFile.class);
    private static final ZTRChromatogramImpl EXPECTED_ZTR;
    static{
        try {
            EXPECTED_ZTR= (ZTRChromatogramImpl)ChromatogramXMLSerializer.fromXML(RESOURCES.getFileAsStream("files/GBKAK82TF.ztr.xml"));
        } catch (IOException e) {
            throw new IllegalStateException("could not parse expected chromatogram",e);
        }
    }
    
    @Test
    public void parseZtrFile() throws IOException, TraceDecoderException{
        File ztrFile = RESOURCES.getFile("files/GBKAK82TF.ztr");
        ZtrChromatogram actual = new ZtrChromatogramBuilder("GBKAK82TF.ztr",ztrFile).build();
        assertEquals(EXPECTED_ZTR, actual);
    }
    
   
}
