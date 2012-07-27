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

package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.read.trace.Trace;
import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ChromatogramXMLSerializer;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramFile;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramImpl;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestSCFChromatogramFile {

    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestSCFChromatogramFile.class);
    private static final SCFChromatogramImpl EXPECTED_SCF;
    static{
        try {
            Trace fromXML = ChromatogramXMLSerializer.fromXML(RESOURCES.getFileAsStream("files/GBKAK82TF.scf.xml"));
            EXPECTED_SCF= (SCFChromatogramImpl)fromXML;
        } catch (Exception e) {
            throw new IllegalStateException("could not parse expected chromatogram",e);
        }
    }

    @Test
    public void parseScfFile() throws IOException, TraceDecoderException{
        File scfFile = RESOURCES.getFile("files/GBKAK82TF.scf");
        SCFChromatogram actual = SCFChromatogramFile.create(scfFile);
        assertEquals(EXPECTED_SCF, actual);
    }
    
    @Test
    public void scfWithGaps() throws IOException, TraceDecoderException{
        File scfFile = RESOURCES.getFile("files/containsGaps.scf");
        SCFChromatogram actual = SCFChromatogramFile.create(scfFile);
        assertEquals(actual.getNucleotideSequence().toString(), "-----");
        
    }
}
