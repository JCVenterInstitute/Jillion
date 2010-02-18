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
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.glyph.GlyphFactory;

public final class NucleotideGlyphFactory implements GlyphFactory<NucleotideGlyph,Character> {

    private static final NucleotideGlyphFactory INSTANCE = new NucleotideGlyphFactory();
    
    private NucleotideGlyphFactory(){}
    public static NucleotideGlyphFactory getInstance(){
        return INSTANCE;
    }
    @Override
    public NucleotideGlyph getGlyphFor(Character ch) {
        return NucleotideGlyph.getGlyphFor(ch);
    }

    @Override
    public List<NucleotideGlyph> getGlyphsFor(List<Character> list) {
        StringBuilder builder = new StringBuilder();
        for(Character c: list){
            builder.append(c);
        }
        return  getGlyphsFor(builder);
    }
    public List<NucleotideGlyph> getGlyphsFor(CharSequence s){
        List<NucleotideGlyph> result = new ArrayList<NucleotideGlyph>(s.length());
        for(int i=0; i<s.length(); i++){
            result.add(getGlyphFor(s.charAt(i)));
        }
        return result;
    }


}
