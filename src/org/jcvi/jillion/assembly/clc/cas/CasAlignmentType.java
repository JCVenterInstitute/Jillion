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
