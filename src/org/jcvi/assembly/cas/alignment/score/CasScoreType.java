/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment.score;

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
