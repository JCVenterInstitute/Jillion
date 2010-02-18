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
