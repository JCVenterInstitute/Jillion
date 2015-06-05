/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.jillion.core.residue.Residue;
/**
 * {@code Nucleotide} is a {@link Residue}
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

    private static final Map<Nucleotide,Set<Nucleotide>> AMBIGUITY_TO_CONSTIUENT;
    private static final Map<Nucleotide,Set<Nucleotide>> CONSTIUENT_TO_AMBIGUITY;
   
    //42 - 121
    private static Nucleotide[] CACHE = new Nucleotide[80];
    
    private static final Nucleotide[] VALUES_ARRAY = values();
    public static final List<Nucleotide> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
    static{
        
       
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
        for(Nucleotide value : VALUES_ARRAY){
        	char uppercase = value.c.charValue();
        	
        	char lowercase = Character.toLowerCase(uppercase);
        	
        	CACHE[computeOffsetFor(uppercase)] = value;
        	CACHE[computeOffsetFor(lowercase)] = value;
        }
        //add consed gap
        CACHE[computeOffsetFor('*')] = Gap;
        //treat X's as Ns
        CACHE[computeOffsetFor('x')] = Unknown;
        CACHE[computeOffsetFor('X')] = Unknown;
        
    }
    
  
    private static int computeOffsetFor(char c){
    	return c-42;
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

    /**
     * Get the complement this {@link Nucleotide}.
     * @return the complement of this.
     */
    public Nucleotide complement() {
    	Nucleotide ret=null;
    	switch(this){
    		case Unknown : ret = Unknown;
							break;
    		case NotThymine : ret = NotAdenine;
    						break;
    		case NotGuanine : ret = NotCytosine;
    						break;
    		case NotCytosine : ret = NotGuanine;
								break;
    		case NotAdenine : ret = NotThymine;
							break; 
    		case Weak : ret = Weak;
								break; 		
    		case Amino : ret = Keto;
							break; 
    		case Purine : ret = Pyrimidine;
								break; 
    		case Strong : ret = Strong;
							break; 
    		case Pyrimidine : ret = Purine;
							break; 
    		case Keto : ret = Amino;
							break; 
    		case Gap : ret = Gap;
							break;				
    		case Adenine : ret = Thymine;
    						break;
    		case Cytosine : ret = Guanine;
								break;
    		case Guanine : ret = Cytosine;
								break;
    		case Thymine : ret = Adenine;
    						break;
			default : //can't happen 
				throw new IllegalStateException("a new nucleotide ordinal has been added" + this);
    	}
       return ret;
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
        return parse(base.charAt(0));
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
        final Nucleotide ret = parseOrNull(base);
        if(ret==null){
            throw new IllegalArgumentException("invalid character " + base + " ascii value " + (int)base);
        }
        return ret;
    }
    /**
     * Same as {@link #parse(char)}
     * except if the character is ASCII whitespace
     * then return null.
     * @param base
     * @return
     */
	protected static Nucleotide parseOrNull(char base) {
		//if it's a whitespace character return null.
		if(base == 32 || (base >=0 && base <=13)){
			return null;
		}
		int offset =computeOffsetFor(base);
		Nucleotide ret=null;
		if(offset >=0 && offset < CACHE.length){
			ret = CACHE[offset];
		}
		//if we're still null then it's invalid character
    	if(ret==null){
            throw new IllegalArgumentException("invalid character " + base + " ascii value " + (int)base);
        }
		return ret;
	}
    /**
     * Returns this Nucleotide as a single character String.  For example {@link #Adenine} 
     * will return "A".
     */
    @Override
    public String toString() {
        return c.toString();
    }
    
    @Override
    public boolean isGap(){
        return this == Gap;
    }
    /**
     * Is This Nucleotide an ambiguity?
     * An ambiguity is any Nucleotide that is not an 
     * A , C, G or T or gap.
     * @return {@code true} if it is am ambiguity;
     * {@code false} otherwise.
     */
    public boolean isAmbiguity(){
        return !isGap() && this !=Adenine  
         && this !=Cytosine && this != Guanine && this != Thymine;
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
    
    
    public static Nucleotide getByOrdinal(int ordinal){
    	return VALUES_ARRAY[ordinal];
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
