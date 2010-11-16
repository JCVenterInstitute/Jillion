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
 * Created on Apr 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.IOException;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Tests to see if SCF parser can handle 
 * data that is not A,C,G or T.  SCF spec
 * says that kind  of data is stored in the T channel.
 * @author dkatzel
 *
 *
 */
public class TestSCFChromatogramWithGaps {
	 private final static ResourceFileServer RESOURCES = new ResourceFileServer(TestSCFChromatogramWithGaps.class);
		
    private static final String File_path = "files/containsGaps.scf";
    
    @Test
    public void parse() throws SCFDecoderException, IOException{
        SCFChromatogram actual =Version3SCFCodec.INSTANCE.decode(
        		RESOURCES.getFileAsStream(File_path));
        assertEquals(NucleotideGlyph.convertToString(actual.getBasecalls().decode()), "-----");
        
    }
}
