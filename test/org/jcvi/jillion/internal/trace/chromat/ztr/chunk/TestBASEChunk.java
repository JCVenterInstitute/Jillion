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
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;

import java.nio.ByteBuffer;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.trace.chromat.ztr.chunk.Chunk;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.TraceEncoderException;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestBASEChunk {
   
    private static final String decodedBases = "ACGTACGTNW-";
   Chunk sut =Chunk.BASE;
   private static final byte[] encodedBases;
	static{
		ByteBuffer buf = ByteBuffer.allocate(decodedBases.length()+1);
	    buf.put((byte)0 ); //padding
	    buf.put(decodedBases.getBytes());
	    encodedBases = buf.array();
	}
	
    @Test
    public void valid() throws TraceDecoderException{
       
        ZtrChromatogramBuilder builder = new ZtrChromatogramBuilder("id");
        sut.parseData(encodedBases, builder);        
        assertEquals(decodedBases, builder.basecalls().toString());
    }
    
    @Test
    public void encode() throws TraceEncoderException, TraceDecoderException{
    	ZtrChromatogram mockChromatogram = createMock(ZtrChromatogram.class);
    	NucleotideSequence basecalls = new NucleotideSequenceBuilder(decodedBases).build();
    	expect(mockChromatogram.getNucleotideSequence()).andReturn(basecalls);
    	
    	replay(mockChromatogram);
    	byte[] actual =sut.encodeChunk(mockChromatogram);
    	assertArrayEquals(encodedBases, actual);
    	verify(mockChromatogram);
    }
}
