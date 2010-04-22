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
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.glyph.Glyph;

public enum NucleotideGlyph implements Glyph {
    //order is in ambiguity traversal order that is most efficient.
    Unknown(Character.valueOf('N')),
    NotThymine(Character.valueOf('V')),
    NotGuanine(Character.valueOf('H')),
    NotCytosine(Character.valueOf('D')),
    NotAdenine(Character.valueOf('B')),
    Weak(Character.valueOf('W')),
    Amino(Character.valueOf('M')),   
    Purine(Character.valueOf('R')),
    Strong(Character.valueOf('S')),  
    Pyrimidine(Character.valueOf('Y')),   
    Keto(Character.valueOf('K')),    
    Adenine(Character.valueOf('A')),
    Cytosine(Character.valueOf('C')),
    Guanine(Character.valueOf('G')),
    Thymine(Character.valueOf('T')),
    Gap(Character.valueOf('-')),
    ;
    
    private static final Map<NucleotideGlyph,NucleotideGlyph> COMPLIMENT_MAP;
    private static final Map<NucleotideGlyph,Set<NucleotideGlyph>> AMBIGUITY_TO_CONSTIUENT;
    private static final Map<NucleotideGlyph,Set<NucleotideGlyph>> CONSTIUENT_TO_AMBIGUITY;
    private static final Map<Character,NucleotideGlyph> CHARACTER_MAP;
    static{
        COMPLIMENT_MAP = new EnumMap<NucleotideGlyph, NucleotideGlyph>(NucleotideGlyph.class);
        COMPLIMENT_MAP.put(Adenine, Thymine);
        COMPLIMENT_MAP.put(Thymine, Adenine);
        COMPLIMENT_MAP.put(Guanine, Cytosine);
        COMPLIMENT_MAP.put(Cytosine, Guanine);
        COMPLIMENT_MAP.put(Pyrimidine, Purine);
        COMPLIMENT_MAP.put(Purine, Pyrimidine);        
        COMPLIMENT_MAP.put(Keto, Amino);
        COMPLIMENT_MAP.put(Amino, Keto);
        COMPLIMENT_MAP.put(NotCytosine, NotGuanine);
        COMPLIMENT_MAP.put(NotGuanine, NotCytosine);
        COMPLIMENT_MAP.put(NotThymine, NotAdenine);
        COMPLIMENT_MAP.put(NotAdenine, NotThymine);
        COMPLIMENT_MAP.put(Weak, Weak);
        COMPLIMENT_MAP.put(Strong, Strong);
        COMPLIMENT_MAP.put(Gap, Gap);
        COMPLIMENT_MAP.put(Unknown, Unknown);  
        
        CHARACTER_MAP = new HashMap<Character, NucleotideGlyph>();
        for(NucleotideGlyph g: NucleotideGlyph.values()){
            CHARACTER_MAP.put(g.getCharacter(), g);
        }
        //add support for X which some systems use instead of N
        CHARACTER_MAP.put(Character.valueOf('X'), Unknown);
        AMBIGUITY_TO_CONSTIUENT = new EnumMap<NucleotideGlyph, Set<NucleotideGlyph>>(NucleotideGlyph.class);
       
        AMBIGUITY_TO_CONSTIUENT.put(Unknown, EnumSet.of(Adenine,Cytosine,Guanine,Thymine));
        AMBIGUITY_TO_CONSTIUENT.put(NotThymine, EnumSet.of(Adenine,Cytosine,Guanine));
        AMBIGUITY_TO_CONSTIUENT.put(NotGuanine, EnumSet.of(Adenine,Cytosine,Thymine));
        AMBIGUITY_TO_CONSTIUENT.put(NotCytosine, EnumSet.of(Adenine,Guanine,Thymine));
        AMBIGUITY_TO_CONSTIUENT.put(NotAdenine, EnumSet.of(Cytosine,Guanine,Thymine));
        
        AMBIGUITY_TO_CONSTIUENT.put(Weak, EnumSet.of(Adenine,Thymine));
        AMBIGUITY_TO_CONSTIUENT.put(Amino, EnumSet.of(Adenine,Cytosine));
        
        AMBIGUITY_TO_CONSTIUENT.put(Purine, EnumSet.of(Adenine,Guanine));
        AMBIGUITY_TO_CONSTIUENT.put(Strong, EnumSet.of(Cytosine,Guanine));
        
        AMBIGUITY_TO_CONSTIUENT.put(Pyrimidine, EnumSet.of(Cytosine,Thymine));
        AMBIGUITY_TO_CONSTIUENT.put(Keto, EnumSet.of(Guanine,Thymine));
        
        AMBIGUITY_TO_CONSTIUENT.put(Adenine, EnumSet.of(Adenine));
        AMBIGUITY_TO_CONSTIUENT.put(Cytosine, EnumSet.of(Cytosine));
        AMBIGUITY_TO_CONSTIUENT.put(Guanine, EnumSet.of(Guanine));
        AMBIGUITY_TO_CONSTIUENT.put(Thymine, EnumSet.of(Thymine));
        
        CONSTIUENT_TO_AMBIGUITY = new EnumMap<NucleotideGlyph, Set<NucleotideGlyph>>(NucleotideGlyph.class);
        for(NucleotideGlyph glyph : EnumSet.of(Adenine,Cytosine,Guanine,Thymine)){
            CONSTIUENT_TO_AMBIGUITY.put(glyph, EnumSet.noneOf(NucleotideGlyph.class));
        }
        for(Entry<NucleotideGlyph, Set<NucleotideGlyph>> entry : AMBIGUITY_TO_CONSTIUENT.entrySet()){
            for(NucleotideGlyph glyph : entry.getValue()){
                final NucleotideGlyph glyphToAdd = entry.getKey();
                if(glyphToAdd != glyph){
                    CONSTIUENT_TO_AMBIGUITY.get(glyph).add(glyphToAdd);
                }
            }
        }
    }
    
    /**
     * A predefined matrix of nucleotide matching results.  This is a simple 2-index matrix
     * where each index represents one of the nucleotides to attempt to match.  The order of the
     * indexes does not matter.
     */
    private static final boolean[][] MATCH = new boolean[NucleotideGlyph.values().length][NucleotideGlyph.values().length];

    /*
     * This pre-populates the match table.
     * 
     * Note: Some simplistic optization happens here.  The match value is only calculated when 
     * the second nucleotide in the pair is not less than the first.  After the calculation is
     * done, the result is loaded into the matrix locations of [a,b] and [b,a].  This cuts the
     * number of calculations roughly in half.  There isn't much savings here, really, but the
     * optization was so simple to do, there wasn't much of a reason not to do it.
     */
    static
    {
        for (final NucleotideGlyph glyphA : NucleotideGlyph.values())
        {
            int glyphAindex = glyphA.ordinal();
            for (final NucleotideGlyph glyphB : NucleotideGlyph.values())
            {
                int glyphBindex = glyphB.ordinal();
                if (glyphAindex <= glyphBindex)
                {
                    boolean val = NucleotideGlyph.calculateMatch(glyphA, glyphB);
                    NucleotideGlyph.MATCH[glyphAindex][glyphBindex] = val;
                    NucleotideGlyph.MATCH[glyphBindex][glyphAindex] = val;
                }
            }
        }
    }
    
    private final Character c;
    
    NucleotideGlyph(Character c){
        this.c = c;
    }
    public Character getCharacter() {
        return c;
    }
    @Override
    public String getName() {
        return toString();
    }

    public NucleotideGlyph reverseCompliment() {
       return COMPLIMENT_MAP.get(this);
    }
    
    public static List<NucleotideGlyph> reverseCompliment(List<NucleotideGlyph> glyphs) {
        List<NucleotideGlyph> reversed = new ArrayList<NucleotideGlyph>(glyphs.size());
        for(int i=glyphs.size()-1; i>=0; i--){
            reversed.add(glyphs.get(i).reverseCompliment());
        }
        return reversed;
     }

    public static NucleotideGlyph getGlyphFor(Character c){
        
        Character upperCased = Character.toUpperCase(c);
        if(CHARACTER_MAP.containsKey(upperCased)){
            return CHARACTER_MAP.get(upperCased);
        }
        throw new IllegalArgumentException("invalid character " + c + " ascii value " + (int)c.charValue());
    }
    /**
     * Returns this glyph as a single character String.  For example {@link #Adenine} 
     * will return "A".
     */
    @Override
    public String toString() {
        return getCharacter().toString();
    }
    
    public boolean isGap(){
        return this == Gap;
    }
    
    public boolean isAmbiguity(){
        return !isGap() && this !=Adenine && 
        this !=Cytosine && this != Guanine && this != Thymine;
    }

    public static List<NucleotideGlyph> convertToUngapped(List<NucleotideGlyph> gapped){
        List<NucleotideGlyph> ungapped = new ArrayList<NucleotideGlyph>(gapped.size());
        for(NucleotideGlyph possibleGap : gapped){
            if(!possibleGap.isGap()){
                ungapped.add(possibleGap);
            }
        }
        return ungapped;
    }
    public static List<NucleotideGlyph> getGlyphsFor(char[] array){
       return getGlyphsFor(new String(array));
    }
    public static List<NucleotideGlyph> getGlyphsFor(List<Character> list) {
        StringBuilder builder = new StringBuilder();
        for(Character c: list){
            builder.append(c);
        }
        return  getGlyphsFor(builder);
    }
    public static List<NucleotideGlyph> getGlyphsFor(CharSequence s){
        List<NucleotideGlyph> result = new ArrayList<NucleotideGlyph>(s.length());
        try{
            for(int i=0; i<s.length(); i++){
                result.add(getGlyphFor(s.charAt(i)));
            }
            return result;
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("could not getGlyphs for "+ s,e);
        }
        
    }

    public static String convertToString(List<NucleotideGlyph> glyphs){
        StringBuilder result = new StringBuilder();
        for(NucleotideGlyph g: glyphs){
            result.append(g.toString());
        }
        return result.toString();
    }
    
    /**
     * Checks to see if the given nucleotide matches this nucleotide.  A "match" is defined as
     * any relationship where a nucleotide or one of its ambiguous constituents is eqivalent to 
     * the other nucleotide or its constituents.
     * 
     * @param that The <code>NucleotideGlyph</code> to compare.
     * @return <code>true</code> if the nucleotides may represent matching bases, false if they
     * cannot represent matching bases.
     */
    public boolean matches(NucleotideGlyph that)
    {
        return NucleotideGlyph.MATCH[this.ordinal()][that.ordinal()];
    }
    
    /**
     * Pre-calculates the result of ambiguity {@link #matches(NucleotideGlyph) matching} for a
     * given pair of nucleotides.
     * 
     * @param a The first nucleotide.
     * @param b The second nucleotide.
     * @return <code>true</code> if the two nucleotides represent a potential match, 
     * <code>false</code> if they do not.
     */
    public static boolean calculateMatch(NucleotideGlyph a, NucleotideGlyph b)
    {
        if (a.equals(b)) return true;
        
        if (a.isAmbiguity())
        {
            if (b.isAmbiguity())
            {
                for (NucleotideGlyph constituent : NucleotideGlyph.AMBIGUITY_TO_CONSTIUENT.get(a))
                {
                    if (NucleotideGlyph.AMBIGUITY_TO_CONSTIUENT.get(b).contains(constituent)) return true;
                }
            }
            else if (NucleotideGlyph.AMBIGUITY_TO_CONSTIUENT.get(a).contains(b)) return true;
        }
        else if (b.isAmbiguity())
        {
            if (NucleotideGlyph.AMBIGUITY_TO_CONSTIUENT.get(b).contains(a)) return true;
        }
        
        return false;
    }
    
    public static Set<NucleotideGlyph> getAmbiguitesFor(NucleotideGlyph glyph){
        
        if(CONSTIUENT_TO_AMBIGUITY.containsKey(glyph)){
            return EnumSet.copyOf(CONSTIUENT_TO_AMBIGUITY.get(glyph));
        }
        return EnumSet.noneOf(NucleotideGlyph.class);
    }
    public static NucleotideGlyph getAmbiguityFor(Collection<NucleotideGlyph> unambiguiousBases){
        
        for(Entry<NucleotideGlyph, Set<NucleotideGlyph>> entry : AMBIGUITY_TO_CONSTIUENT.entrySet()){
            if(unambiguiousBases.containsAll(entry.getValue())){
                return entry.getKey();
            }
        }
        return Gap;        
    }
    
    public Set<NucleotideGlyph> getPossibleAmbiguites(){
        return getAmbiguitesFor(this);
    }
    
}
