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
package org.jcvi.common.core.symbol.residue.nuc;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jcvi.common.core.symbol.Symbol;
/**
 * {@code Nucleotide} is a {@link Symbol}
 * implementation for DNA Nucleotides.
 * @author dkatzel
 *
 *
 */
public enum Nucleotide implements Symbol {
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
    
    private static final Map<Nucleotide,Nucleotide> COMPLIMENT_MAP;
    private static final Map<Nucleotide,Set<Nucleotide>> AMBIGUITY_TO_CONSTIUENT;
    private static final Map<Nucleotide,Set<Nucleotide>> CONSTIUENT_TO_AMBIGUITY;
    private static final Map<Character,Nucleotide> CHARACTER_MAP;
    static{
        COMPLIMENT_MAP = new EnumMap<Nucleotide, Nucleotide>(Nucleotide.class);
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
        
        CHARACTER_MAP = new TreeMap<Character, Nucleotide>();
        for(Nucleotide n: Nucleotide.values()){
            CHARACTER_MAP.put(n.getCharacter(), n);
        }
        //add support for X which some systems use instead of N
        CHARACTER_MAP.put(Character.valueOf('X'), Unknown);
        AMBIGUITY_TO_CONSTIUENT = new EnumMap<Nucleotide, Set<Nucleotide>>(Nucleotide.class);
       
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
        
        CONSTIUENT_TO_AMBIGUITY = new EnumMap<Nucleotide, Set<Nucleotide>>(Nucleotide.class);
        for(Nucleotide n : EnumSet.of(Adenine,Cytosine,Guanine,Thymine)){
            CONSTIUENT_TO_AMBIGUITY.put(n, EnumSet.noneOf(Nucleotide.class));
        }
        for(Entry<Nucleotide, Set<Nucleotide>> entry : AMBIGUITY_TO_CONSTIUENT.entrySet()){
            for(Nucleotide n : entry.getValue()){
                final Nucleotide toAdd = entry.getKey();
                if(toAdd != n){
                    CONSTIUENT_TO_AMBIGUITY.get(n).add(toAdd);
                }
            }
        }
    }
    
  
    
    private final Character c;
    
    private Nucleotide(Character c){
        this.c = c;
    }
    public static String getAllCharacters(){
        StringBuilder builder = new StringBuilder();
        for(Character c : CHARACTER_MAP.keySet()){
            builder.append(c);
        }
        return builder.toString();
    }
    /**
     * Return the Character equivalent of this
     * {@link Nucleotide}.  For example
     * calling this method for {@link #Adenine}
     * will return 'A'.
     * @return the Character equivalent of this.
     */
    public Character getCharacter() {
        return c;
    }
    @Override
    public String getName() {
        return toString();
    }
    /**
     * Get the compliment this {@link Nucleotide}.
     * @return the compliment of this.
     */
    public Nucleotide compliment() {
       return COMPLIMENT_MAP.get(this);
    }
    
    /**
     * Get the {@link Nucleotide} for the given
     * String  representation.  If the given String is more than
     * one character long, only the first character will be considered.
     * For example,
     * {@link #parse(String) parse("A")} will return
     * {@link #Adenine}.
     * @param base the nucleotide as a String of length 1.
     * @return a {@link Nucleotide} equivalent.
     * @throws IllegalArgumentException if the given
     * character can not be mapped to a {@link Nucleotide}.
     */
    public static Nucleotide parse(String base){
        return parse(base.trim().charAt(0));
    }
    /**
     * Get the {@link Nucleotide} for the given
     * character representation.  For example,
     * {@link #parse(char) parse('A')} will return
     * {@link #Adenine}.
     * @param base the nucleotide as a character.
     * @return a {@link Nucleotide} equivalent.
     * @throws IllegalArgumentException if the given
     * character can not be mapped to a {@link Nucleotide}.
     */
    public static Nucleotide parse(char base){
        
        Character upperCased = Character.toUpperCase(base);
        if(CHARACTER_MAP.containsKey(upperCased)){
            return CHARACTER_MAP.get(upperCased);
        }
        throw new IllegalArgumentException("invalid character " + base + " ascii value " + (int)base);
    }
    /**
     * Returns this Nucleotide as a single character String.  For example {@link #Adenine} 
     * will return "A".
     */
    @Override
    public String toString() {
        return getCharacter().toString();
    }
    /**
     * Is This Nucleotide a gap?
     * @return {@code true} if it is a gap;
     * {@code false} otherwise.
     */
    public boolean isGap(){
        return this == Gap;
    }
    /**
     * Is This Nucleotide an ambiguity?
     * @return {@code true} if it is am ambiguity;
     * {@code false} otherwise.
     */
    public boolean isAmbiguity(){
        return !isGap() && this !=Adenine && 
        this !=Cytosine && this != Guanine && this != Thymine;
    }
    
    
   
    
    
    /**
     * Get the Set containing all ambiguous {@link Nucleotide}s that
     * could be created from this
     * {@link Nucleotide} (plus others).
     * @return the Set of ambiguous {@link Nucleotide}s that
     * could be created from this or an empty set if
     * the given {@link Nucleotide} is already
     * an ambiguity. 
     */
    public Set<Nucleotide> getAllPossibleAmbiguities(){
        
        if(CONSTIUENT_TO_AMBIGUITY.containsKey(this)){
            return EnumSet.copyOf(CONSTIUENT_TO_AMBIGUITY.get(this));
        }
        return EnumSet.noneOf(Nucleotide.class);
    }
    /**
     * Give the ambiguity {@link Nucleotide} for
     * the corresponding collection of unambiguous {@link Nucleotide}s
     * 
     * @param unambiguiousBases collection of unambiguous {@link Nucleotide}s
     * to be turned into a single ambiguity.
     * @return the ambiguity {@link Nucleotide} or {@link #Gap}
     * if no ambiguity exists for all the given unambiguous bases.
     * @throws NullPointerException if unambiguiousBases is null.
     */
    public static Nucleotide getAmbiguityFor(Collection<Nucleotide> unambiguiousBases){
        if(unambiguiousBases ==null){
            throw new NullPointerException("unambiguousBases can not be null");
        }
        for(Entry<Nucleotide, Set<Nucleotide>> entry : AMBIGUITY_TO_CONSTIUENT.entrySet()){
            if(unambiguiousBases.containsAll(entry.getValue())){
                return entry.getKey();
            }
        }
        return Gap;        
    }
    
    
}
