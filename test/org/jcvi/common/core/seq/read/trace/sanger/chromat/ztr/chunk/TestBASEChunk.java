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
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.chunk;

import java.nio.ByteBuffer;

import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.TraceEncoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.chunk.Chunk;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

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
       
        ZTRChromatogramBuilder builder = new ZTRChromatogramBuilder();
        sut.parseData(encodedBases, builder);        
        assertEquals(decodedBases, builder.basecalls());
    }
    
    @Test
    public void encode() throws TraceEncoderException, TraceDecoderException{
    	ZTRChromatogram mockChromatogram = createMock(ZTRChromatogram.class);
    	NucleotideSequence basecalls = new DefaultNucleotideSequence(decodedBases);
    	expect(mockChromatogram.getBasecalls()).andReturn(basecalls);
    	
    	replay(mockChromatogram);
    	byte[] actual =sut.encodeChunk(mockChromatogram);
    	assertArrayEquals(encodedBases, actual);
    	verify(mockChromatogram);
    }
}
