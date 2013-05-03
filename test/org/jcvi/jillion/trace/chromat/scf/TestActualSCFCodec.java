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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFCodecs;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfDecoderException;
import org.junit.Test;
public class TestActualSCFCodec {

	 private final static ResourceHelper RESOURCES = new ResourceHelper(TestActualSCFCodec.class);
	   
    private SCFCodec sut = SCFCodecs.VERSION_3;
    @Test
    public void decodeAndEncodeMatch() throws ScfDecoderException, IOException{
        ScfChromatogram decoded = new ScfChromatogramBuilder("id", RESOURCES.getFile("files/GBKAK82TF.scf"))
        							.build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        sut.write(decoded, out);
        ScfChromatogramBuilder builder = new ScfChromatogramBuilder("id", new ByteArrayInputStream(out.toByteArray()));
    	ScfChromatogram decodedAgain = builder.build();        
        assertEquals(decoded, decodedAgain);
        
    }
}
