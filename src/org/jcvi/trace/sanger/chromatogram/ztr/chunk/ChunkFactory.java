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
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;




/**
 * The ChunkFactory reads the chunk type
 * and returns the <code>Chunk</code>
 * implementation that can parse it.
 *
 * @author dkatzel
 *
 *@see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 */
public final class ChunkFactory {
    /**
     * map of all supported chunk implementations mapped by header.
     */
    private static final Map<String, Chunk> CHUNK_MAP;
    /**
     * populate chunk_map.
     */
    static{
        Map<String, Chunk> map = new HashMap<String, Chunk>();
        map.put("SMP4", new SMP4Chunk());
        map.put("BASE", new BASEChunk());
        map.put("BPOS", new BPOSChunk());
        map.put("CNF4", new CNF4Chunk());
        map.put("TEXT", new TEXTChunk());
        map.put("CLIP", new CLIPChunk());
        CHUNK_MAP = Collections.unmodifiableMap(map);
    }
    /**
     * get {@link Chunk} by chunk header name.
     * @param chunkName the name of the chunk as seen in its header.
     * @return a non-null {@link Chunk}.
     * @throws ChunkException if chunk name not supported.
     */
    public static Chunk getChunk(String chunkName) throws ChunkException{
        if(CHUNK_MAP.containsKey(chunkName)){
            return CHUNK_MAP.get(chunkName);
        }
        throw new ChunkException("Could not find Chunk type " +chunkName );        
    }

}
