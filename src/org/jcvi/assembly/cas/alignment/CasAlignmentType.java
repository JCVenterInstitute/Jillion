/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment;

import java.util.HashMap;
import java.util.Map;

public enum CasAlignmentType {

    LOCAL((byte) 0),
    SEMI_LOCAL((byte)1),
    REVERSE_SEMI_LOCAL((byte)2),
    GLOBAL((byte)3)
    ;
    
    private static Map<Byte, CasAlignmentType> MAP;
    
    static{
        MAP = new HashMap<Byte, CasAlignmentType>();
        for(CasAlignmentType type : values()){
            MAP.put(type.getValue(), type);
        }
    }
    private final byte value;
    
    private CasAlignmentType(byte value){
        this.value =value;
    }

    public byte getValue() {
        return value;
    }
    
    public static CasAlignmentType valueOf(byte value){
        return MAP.get(Byte.valueOf(value));
    }
}
