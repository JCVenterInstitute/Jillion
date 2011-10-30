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
 * Object representation of the various
 * ANSI fonts supported by the operating system.
 * Please note that not all fonts are supported
 * on all systems.
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
    /**
     * Get the nth alternate ANSI font.  Please not that 
     * not all alternatives are supported by all systems.
     * @param n the nth alternative font to use.
     * {@code 0} is the primary (default) font, some
     * systems support up to 9 alternate fonts.
     * @return the AnsiFont instance for the
     * given alternate font.  An instance
     * will be returned even if the system does 
     * not support it.  If a system does not support
     * the alternate font, the default font
     * will be printed instead.
     * @throws IllegalArgumentException if n <0 or n>=10
     */
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
