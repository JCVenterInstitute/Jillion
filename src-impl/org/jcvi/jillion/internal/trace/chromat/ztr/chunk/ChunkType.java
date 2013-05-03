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
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code ChunkType} describes the type of data that is contained
 * in a ZTR Chunk.  The Type is determined by the Header which is a 
 * 4-byte ASCII String.
 * @author dkatzel
 *
 *
 */
public enum ChunkType {

    
   
    /**
     * This chunk will encode a series of 16-bit trace samples.
     */
   SAMPLES("SMP4"),
    /**
     * This chunk encodes the basecalls.
     */
    BASECALLS("BASE"),
    /**
     * This chunk encodes the mappings of the basecall numbers to the sample
     * numbers.  It defines the position of each basecall in the trace data.
     */
    POSITIONS("BPOS"),
    /**
     * This Chunk encodes the confidence values for each called base for each
     * channel.
     */
    CONFIDENCE("CNF4"),
    /**
     * This Chunk encodes comments as key value pairs.
     */
    COMMENTS("TEXT"),
    /**
     * This Chunk encodes the suggested quality clip points.
     */
    CLIP("CLIP");
    
   private static final Map<String, ChunkType> MAP;
   
   static{
       MAP = new HashMap<String, ChunkType>();
       for(ChunkType type : values()){
           MAP.put(type.getTypeName(), type);
       }
   }
   
    private final String typeName;
    
    private ChunkType(String typeName){
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
    
    public static ChunkType getChunkFor(String header) throws ChunkException{
        if(MAP.containsKey(header)){
            return MAP.get(header);
        }
        throw new ChunkException(String.format("header '%s' is unknown",header));
    }
    
}
