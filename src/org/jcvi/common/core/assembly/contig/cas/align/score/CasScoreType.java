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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.cas.align.score;

import java.util.HashMap;
import java.util.Map;

public enum CasScoreType {

    NO_SCORE((byte)0),
    BASIC_SCORE((byte)1),
    ALIGNMENT_SCORE((byte)2),
    COLOR_SPACE_SCORE((byte)3),
    ;
    private static Map<Byte, CasScoreType> MAP;
    
    static{
        MAP = new HashMap<Byte, CasScoreType>();
        for(CasScoreType type : values()){
            MAP.put(type.getType(), type);
        }
    }
    
    private final byte type; 
    private CasScoreType(byte type){
        this.type = type;
    }
    public byte getType() {
        return type;
    }
    
    public static CasScoreType valueOf(byte type){
        return MAP.get(Byte.valueOf(type));
    }
    
}
