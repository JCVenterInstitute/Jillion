/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
