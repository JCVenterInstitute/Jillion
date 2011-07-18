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

package org.jcvi.common.core.seq.read.trace.sanger.chromat;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramFile;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramFileParser;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramFile.SCFChromatogramFileBuilderVisitor;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramFile;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramFileParser;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramFile.ZTRChromatogramFileBuilderVisitor;
import org.jcvi.io.fileServer.ResourceFileServer;
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
        ZTRChromatogram ztr = ZTRChromatogramFile.create(ztrFile);
        SCFChromatogramFileBuilderVisitor visitor = SCFChromatogramFile.createNewBuilderVisitor();
        ZTRChromatogramFileParser.parseZTRFile(ztrFile, visitor);
        
        assertValuesMatch(visitor.build(), ztr);
    }
    
    @Test
    public void parseScfAsZtrFile() throws IOException, TraceDecoderException{
        File scfFile = RESOURCES.getFile("scf/files/GBKAK82TF.scf");
        SCFChromatogram scf = SCFChromatogramFile.create(scfFile);
        ZTRChromatogramFileBuilderVisitor visitor = ZTRChromatogramFile.createNewBuilderVisitor();
        SCFChromatogramFileParser.parseSCFFile(scfFile, visitor);
        
        assertValuesMatch(scf, visitor.build());
    }

    protected void assertValuesMatch(SCFChromatogram scf,
            ZTRChromatogram ztr) {
        assertEquals(ztr.getBasecalls(), scf.getBasecalls());
        assertEquals(ztr.getPeaks(), scf.getPeaks());
        assertEquals(ztr.getQualities(),scf.getQualities());
        assertEquals(ztr.getChannelGroup(), scf.getChannelGroup());
        assertEquals(ztr.getNumberOfTracePositions(),scf.getNumberOfTracePositions());
    }
}
