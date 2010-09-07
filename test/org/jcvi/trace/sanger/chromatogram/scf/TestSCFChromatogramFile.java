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

package org.jcvi.trace.sanger.chromatogram.scf;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChromatogramXMLSerializer;
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
            EXPECTED_SCF= (SCFChromatogramImpl)ChromatogramXMLSerializer.fromXML(RESOURCES.getFileAsStream("files/GBKAK82TF.scf.xml"));
        } catch (IOException e) {
            throw new IllegalStateException("could not parse expected chromatogram",e);
        }
    }

    @Test
    public void parseScfFile() throws IOException, TraceDecoderException{
        File scfFile = RESOURCES.getFile("files/GBKAK82TF.scf");
        SCFChromatogramFile actual = new SCFChromatogramFile(scfFile);
        assertEquals(EXPECTED_SCF, actual);
    }
    
    @Test
    public void scfWithGaps() throws IOException, TraceDecoderException{
        File scfFile = RESOURCES.getFile("files/containsGaps.scf");
        SCFChromatogramFile actual = new SCFChromatogramFile(scfFile);
        assertEquals(NucleotideGlyph.convertToString(actual.getBasecalls().decode()), "-----");
        
    }
}
