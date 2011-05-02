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
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import java.io.IOException;

import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.scf.SCFCodecs;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramParser;
import org.jcvi.trace.sanger.phd.SinglePhdFile;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSangerTraceParser {

    private static final String ZTR_FILE = "chromatogram/ztr/files/GBKAK82TF.ztr";
    private static final String SCF3_FILE = "chromatogram/scf/files/GBKAK82TF.scf";
    private static final String PHD_FILE = "phd/files/1095595674585.phd.1";
    
    SangerTraceParser sut = SangerTraceParser.getInstance();
    private final static ResourceFileServer RESOURCES = new ResourceFileServer(TestSangerTraceParser.class);
    
    @Test
    public void parseZTR() throws TraceDecoderException, IOException{
        SangerTrace actual =sut.decode(RESOURCES.getFileAsStream(ZTR_FILE));
        SangerTrace expected = new ZTRChromatogramParser().decode(RESOURCES.getFileAsStream(ZTR_FILE));
        assertEquals(expected, actual);
    }
    @Test
    public void parseSCF_v3() throws TraceDecoderException, IOException{
        SangerTrace actual =sut.decode(RESOURCES.getFileAsStream(SCF3_FILE));
        SangerTrace expected = SCFCodecs.VERSION_3.decode(RESOURCES.getFileAsStream(SCF3_FILE));
        assertEquals(expected, actual);
    }
    @Test
    public void parsePhd() throws TraceDecoderException, IOException{
        SangerTrace actual =sut.decode(RESOURCES.getFileAsStream(PHD_FILE));
        SangerTrace expected = new SinglePhdFile(RESOURCES.getFile(PHD_FILE));
        assertEquals(expected, actual);
    }
}
