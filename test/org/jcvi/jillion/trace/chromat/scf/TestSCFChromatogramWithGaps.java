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
/*
 * Created on Apr 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfDecoderException;
import org.junit.Test;
/**
 * Tests to see if SCF parser can handle 
 * data that is not A,C,G or T.  SCF spec
 * says that kind  of data is stored in the T channel.
 * @author dkatzel
 *
 *
 */
public class TestSCFChromatogramWithGaps {
	 private final static ResourceHelper RESOURCES = new ResourceHelper(TestSCFChromatogramWithGaps.class);
		
    private static final String File_path = "files/containsGaps.scf";
    
    @Test
    public void parse() throws ScfDecoderException, IOException{
    	ScfChromatogramBuilder builder = new ScfChromatogramBuilder("id", RESOURCES.getFile(File_path));
    	ScfChromatogram actual = builder.build();
        assertEquals(actual.getNucleotideSequence().toString(), "-----");
        
    }
}
