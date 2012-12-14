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
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.scf;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.common.core.seq.trace.sanger.chromat.scf.impl.SCFCodec;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.impl.SCFCodecs;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
public class TestActualSCFCodec {

	 private final static ResourceFileServer RESOURCES = new ResourceFileServer(TestActualSCFCodec.class);
	   
    private SCFCodec sut = SCFCodecs.VERSION_3;
    @Test
    public void decodeAndEncodeMatch() throws SCFDecoderException, IOException{
        SCFChromatogram decoded = new SCFChromatogramBuilder("id", RESOURCES.getFile("files/GBKAK82TF.scf"))
        							.build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        sut.write(decoded, out);
        SCFChromatogramBuilder builder = new SCFChromatogramBuilder("id", new ByteArrayInputStream(out.toByteArray()));
    	SCFChromatogram decodedAgain = builder.build();        
        assertEquals(decoded, decodedAgain);
        
    }
}
