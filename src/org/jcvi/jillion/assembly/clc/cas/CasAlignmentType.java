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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

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
