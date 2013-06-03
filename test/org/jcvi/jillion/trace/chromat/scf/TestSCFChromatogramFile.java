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
package org.jcvi.jillion.trace.chromat.scf;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.trace.chromat.scf.ScfChromatogramImpl;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.chromat.ChromatogramXMLSerializer;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestSCFChromatogramFile {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestSCFChromatogramFile.class);
    private static final ScfChromatogramImpl EXPECTED_SCF;
    static{
        try {
            Trace fromXML = ChromatogramXMLSerializer.fromXML(RESOURCES.getFileAsStream("files/GBKAK82TF.scf.xml"));
            EXPECTED_SCF= (ScfChromatogramImpl)fromXML;
        } catch (Exception e) {
            throw new IllegalStateException("could not parse expected chromatogram",e);
        }
    }

    @Test
    public void parseScfFile() throws IOException{
        File scfFile = RESOURCES.getFile("files/GBKAK82TF.scf");
        ScfChromatogram actual = new ScfChromatogramBuilder("id", scfFile)
									.build();
        assertEquals(EXPECTED_SCF, actual);
    }
    
    @Test
    public void scfWithGaps() throws IOException{
        File scfFile = RESOURCES.getFile("files/containsGaps.scf");
        ScfChromatogram actual = new ScfChromatogramBuilder("id", scfFile)
									.build();
        assertEquals(actual.getNucleotideSequence().toString(), "-----");
        
    }
    
   
}
