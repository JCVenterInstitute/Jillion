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

package org.jcvi.common.io.ansi;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author dkatzel
 *
 *
 */
public final class AnsiFont implements AnsiAttribute{
    
    private static final Map<Integer, AnsiFont> FONTS;
    static{
        FONTS = new TreeMap<Integer, AnsiFont>();
        for(int i=0; i<10; i++){
            FONTS.put(i, new AnsiFont(10+i));
        }
        
    }
    
    public static AnsiFont getAlteranteFont(int n){
        if(n <0 && n>9){
            throw new IllegalArgumentException("nth alternate must be between 0 and 9");
        }
        return FONTS.get(Integer.valueOf(n));
    }
    private final EscapeCode escapeCode;
    
    private AnsiFont(int code){
        escapeCode = new EscapeCode(code);
    }
    
    @Override
    public EscapeCode getEscapeCode() {
        return escapeCode;
    }
    
    @Override
    public String toString(){
        return escapeCode.toString();
    }

}
