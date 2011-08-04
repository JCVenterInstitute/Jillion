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
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class ShortGlyphFactory implements SymbolFactory<ShortSymbol, Short>{
    private static final Map<Number, ShortSymbol> MAP = new HashMap<Number, ShortSymbol>();
    
    private static final ShortGlyphFactory INSTANCE = new ShortGlyphFactory();
    
    private ShortGlyphFactory(){}
    
    public static ShortGlyphFactory getInstance(){
        return INSTANCE;
    }
    
    public List<ShortSymbol> getGlyphsFor(short[] shorts) {
        List<ShortSymbol> glyphs = new ArrayList<ShortSymbol>();
        for(int i=0; i<shorts.length; i++){
            glyphs.add(getGlyphFor(shorts[i]));
        }
        return glyphs;
    }
    
    public synchronized ShortSymbol getGlyphFor(int b) {
        return getSymbolFor(Short.valueOf((short)Math.min(b, Short.MAX_VALUE)));
    }
    public synchronized ShortSymbol getSymbolFor(Short b) {
        if(MAP.containsKey(b)){
            return MAP.get(b);
        }
        ShortSymbol newGlyph = new ShortSymbol(b);
        MAP.put(b, newGlyph);
        return newGlyph;
    }
    
    @Override
    public List<ShortSymbol> getSymbolsFor(List<Short> shorts) {
        List<ShortSymbol> glyphs = new ArrayList<ShortSymbol>();
        for(int i=0; i<shorts.size(); i++){
            glyphs.add(getSymbolFor(shorts.get(i)));
        }
        return glyphs;
    }


}
