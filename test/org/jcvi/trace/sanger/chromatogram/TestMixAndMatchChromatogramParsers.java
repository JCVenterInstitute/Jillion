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

package org.jcvi.trace.sanger.chromatogram;

import java.io.File;
import java.io.IOException;

import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramFile;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramFileParser;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramFile;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramFileParser;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestMixAndMatchChromatogramParsers {

    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestMixAndMatchChromatogramParsers.class);
    
    @Test
    public void parseZtrAsScfFile() throws IOException, TraceDecoderException{
        File ztrFile = RESOURCES.getFile("ztr/files/GBKAK82TF.ztr");
        ZTRChromatogramFile ztr = new ZTRChromatogramFile(ztrFile);
        SCFChromatogramFile scf = new SCFChromatogramFile();
        ZTRChromatogramFileParser.parseZTRFile(ztrFile, scf);
        
        assertValuesMatch(scf, ztr);
    }
    
    @Test
    public void parseScfAsZtrFile() throws IOException, TraceDecoderException{
        File scfFile = RESOURCES.getFile("scf/files/GBKAK82TF.scf");
        SCFChromatogramFile scf = new SCFChromatogramFile(scfFile);
        ZTRChromatogramFile ztr = new ZTRChromatogramFile();
        SCFChromatogramFileParser.parseSCFFile(scfFile, ztr);
        
        assertValuesMatch(scf, ztr);
    }

    protected void assertValuesMatch(SCFChromatogramFile scf,
            ZTRChromatogramFile ztr) {
        assertEquals(ztr.getBasecalls(), scf.getBasecalls());
        assertEquals(ztr.getPeaks(), scf.getPeaks());
        assertEquals(ztr.getQualities(),scf.getQualities());
        assertEquals(ztr.getChannelGroup(), scf.getChannelGroup());
        assertEquals(ztr.getNumberOfTracePositions(),scf.getNumberOfTracePositions());
    }
}
