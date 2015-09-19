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
