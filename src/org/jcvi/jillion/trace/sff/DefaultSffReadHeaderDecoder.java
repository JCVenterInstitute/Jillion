/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 6, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;

enum DefaultSffReadHeaderDecoder implements SffReadHeaderDecoder {
	/**
	 * Singleton instance.
	 */
	INSTANCE;
	
    private static final int FIXED_PORTION_HEADER_LENGTH = 16;

	@Override
    public SffReadHeader decodeReadHeader(DataInputStream in)
            throws SffDecoderException {
        try{
            short headerLength =in.readShort();
            short nameLegnth = in.readShort();
            int numBases = in.readInt();
            short qualLeft = in.readShort();
            short qualRight = in.readShort();
            short adapterLeft = in.readShort();
            short adapterRight = in.readShort();
            String name = readSequenceName(in,nameLegnth);
            int bytesReadSoFar = 16+nameLegnth;
            int padding =SffUtil.caclulatePaddedBytes(bytesReadSoFar);
            if(headerLength != bytesReadSoFar+padding){
                throw new SffDecoderException("invalid header length");
            }
            IOUtil.blockingSkip(in, padding);
            
            return createNewHeader(numBases, qualLeft, qualRight, adapterLeft,
					adapterRight, name);
        }
        catch(IOException e){
            throw new SffDecoderException("error trying to decode read header",e);
        }
    }
	@Override
    public SffReadHeader decodeReadHeader(ByteBuffer buf)
            throws SffDecoderException {
        try{
            short headerLength =buf.getShort();
            short nameLegnth = buf.getShort();
            int numBases = buf.getInt();
            short qualLeft = buf.getShort();
            short qualRight = buf.getShort();
            short adapterLeft = buf.getShort();
            short adapterRight = buf.getShort();
            String name = readSequenceName(buf,nameLegnth);
            int bytesReadSoFar = FIXED_PORTION_HEADER_LENGTH+nameLegnth;
            int padding =SffUtil.caclulatePaddedBytes(bytesReadSoFar);
            if(headerLength != bytesReadSoFar+padding){
                throw new SffDecoderException("invalid header length");
            }
            buf.position(padding+ buf.position());
            
            return createNewHeader(numBases, qualLeft, qualRight, adapterLeft,
					adapterRight, name);
        }
        catch(IOException e){
            throw new SffDecoderException("error trying to decode read header",e);
        }
    }
	public SffReadHeader createNewHeader(int numBases, short qualLeft,
			short qualRight, short adapterLeft, short adapterRight, String name) {
		//if clip points are NOT computed, then left is 0
		//right clip points may be set to 0 in the file
        //if value is NOT computed, spec says to use numBases instead
		Range qualityClip, adapterClip;
		if(qualLeft==0){
			qualityClip = Range.of(CoordinateSystem.RESIDUE_BASED, 0, 0);
		}else if(qualRight==0){
			qualityClip = new Range.Builder(numBases)
							.contractBegin(qualLeft-1)
							.build();
		}else{
			qualityClip = Range.of(CoordinateSystem.RESIDUE_BASED, qualLeft, qualRight);
		}
		if(adapterLeft==0){
			adapterClip = Range.of(CoordinateSystem.RESIDUE_BASED, 0, 0);
		}else if(adapterRight==0){
			adapterClip = new Range.Builder(numBases)
							.contractBegin(adapterLeft-1)
							.build();
		}else{
			adapterClip = Range.of(CoordinateSystem.RESIDUE_BASED, adapterLeft, adapterRight);
		}
		
		return new DefaultSffReadHeader(numBases,
				qualityClip,
				adapterClip,
		         name);
	}

    private String readSequenceName(DataInputStream in, short length) throws IOException {
        byte[] name = new byte[length];
        try{
        	IOUtil.blockingRead(in, name);
        }catch(IOException e){
        	throw new SffDecoderException("error decoding seq name",e);
        }
       
        return new String(name,IOUtil.UTF_8);
    }

    private String readSequenceName(ByteBuffer buf, int length) throws IOException {
        byte[] name = new byte[length];
        try{
        	buf.get(name);
        }catch(BufferUnderflowException e){
        	throw new SffDecoderException("error decoding seq name",e);
        }
       
        return new String(name,IOUtil.UTF_8);
    }
}
