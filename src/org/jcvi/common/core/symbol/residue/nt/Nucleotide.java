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
package org.jcvi.common.core.symbol.residue.nt;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jcvi.common.core.symbol.Symbol;
import org.jcvi.common.core.symbol.residue.Residue;
/**
 * {@code Nucleotide} is a {@link Symbol}
 * implementation for DNA Nucleotides.
 * @author dkatzel
 *
 *
 */
public enum Nucleotide implements Residue {
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
    Gap(Character.valueOf('-')),
    Adenine(Character.valueOf('A')),
    Cytosine(Character.valueOf('C')),
    Guanine(Character.valueOf('G')),
    Thymine(Character.valueOf('T')),
    
    ;
    
    private static final Map<Nucleotide,Nucleotide> COMPLEMENT_MAP;
    private static final Map<Nucleotide,Set<Nucleotide>> AMBIGUITY_TO_CONSTIUENT;
    private static final Map<Nucleotide,Set<Nucleotide>> CONSTIUENT_TO_AMBIGUITY;
    private static final Map<Character,Nucleotide> CHARACTER_MAP;
    static{
        COMPLEMENT_MAP = new EnumMap<Nucleotide, Nucleotide>(Nucleotide.class);
        COMPLEMENT_MAP.put(Adenine, Thymine);
        COMPLEMENT_MAP.put(Thymine, Adenine);
        COMPLEMENT_MAP.put(Guanine, Cytosine);
        COMPLEMENT_MAP.put(Cytosine, Guanine);
        COMPLEMENT_MAP.put(Pyrimidine, Purine);
        COMPLEMENT_MAP.put(Purine, Pyrimidine);        
        COMPLEMENT_MAP.put(Keto, Amino);
        COMPLEMENT_MAP.put(Amino, Keto);
        COMPLEMENT_MAP.put(NotCytosine, NotGuanine);
        COMPLEMENT_MAP.put(NotGuanine, NotCytosine);
        COMPLEMENT_MAP.put(NotThymine, NotAdenine);
        COMPLEMENT_MAP.put(NotAdenine, NotThymine);
        COMPLEMENT_MAP.put(Weak, Weak);
        COMPLEMENT_MAP.put(Strong, Strong);
        COMPLEMENT_MAP.put(Gap, Gap);
        COMPLEMENT_MAP.put(Unknown, Unknown);  
        
        CHARACTER_MAP = new TreeMap<Character, Nucleotide>();
        for(Nucleotide n: Nucleotide.values()){
            CHARACTER_MAP.put(n.getCharacter(), n);
        }
        //add support for X which some systems use instead of N
        CHARACTER_MAP.put(Character.valueOf('X'), Unknown);
      //add support for * which consed uses instead of -
        CHARACTER_MAP.put(Character.valueOf('*'), Gap);
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
    /**
     * Return the Character equivalent of this
     * {@link Nucleotide}.  For example
     * calling this method for {@link #Adenine}
     * will return 'A'.
     * @return the Character equivalent of this.
     */
    @Override
    public Character getCharacter() {
        return c;
    }
    @Override
    public String getName() {
        return toString();
    }
    /**
     * Get the complement this {@link Nucleotide}.
     * @return the complement of this.
     */
    public Nucleotide complement() {
       return COMPLEMENT_MAP.get(this);
    }
    
    /**
     * Get the {@link Nucleotide} for the given
     * String  representation.  If the given String is more than
     * one character long, only the first character will be considered.
     * For example,
     * {@link #parse(String) parse("A")} will return
     * {@link #Adenine}. This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
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
     * {@link #Adenine}.  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
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
    
    @Override
	public byte getOrdinalAsByte() {
		return (byte)ordinal();
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
     * @see #getBasesFor()
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
    /**
     * Get the non-ambiguous bases that make up this
     * {@link Nucleotide}.
     * If this Nucleotide is ambiguous, then
     * the returned {@link Set} will contain
     * all the {@link Nucleotide}s
     * that make up this ambiguity.
     * Calling this method on a non-ambiguous
     * {@link Nucleotide} will return
     * a Set containing a single element, this.
     * For example calling this method
     * on
     * {@link Nucleotide#Purine}
     * will return a set containing
     * the two {@link Nucleotide}s
     * {@link Nucleotide#Adenine} and
     * {@link Nucleotide#Guanine}.
     * <p/>
     * This method mirrors {@link #getAmbiguityFor(Collection)}
     * such that the input of one of these methods should
     * be the return value of the other.
     * <pre> 
     * Nucleotide n = ...;
     * n == Nucleotide.getAmbiguityFor(n.getBasesFor());
     * </pre> 
     * @return a {@link Set} of Nucleotides
     * will never be null or empty.
     */
    public Set<Nucleotide> getBasesFor(){
    	if(this== Gap){
    		return EnumSet.of(Gap);
    	}
    	return AMBIGUITY_TO_CONSTIUENT.get(this);
    }
    
    /**
     * Two {@link Nucleotide}s match if one of the {@link Nucleotide}'s
     * set of unambiguous bases
     * is a complete subset of the other.
     * For example, V (which is A,C or G) would
     * match A, C, G, M, R, S and N. However, V would not
     * match W since that could also represent a T.
     * @param other the other Nucleotide to match.
     * @return {@code true} if this Nucleotide matches the other given
     * {@link Nucleotide}; {@code false} otherwise.
     */
    public boolean matches(Nucleotide other){
    	if(other ==null){
    		throw new NullPointerException("other can not be null");
    	}
    	if(this==other){
    		return true;
    	}

    	Set<Nucleotide> basesForOther =other.getBasesFor();
    	Set<Nucleotide> basesForThis =getBasesFor();
    	return basesForThis.containsAll(basesForOther)
    			|| basesForOther.containsAll(basesForThis);
    }
}
