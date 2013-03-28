/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
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
        //dkatzel - 2013-03-21
    	//profiling indicated that parsing to Nucleotides was slow.
    	//changed auto-boxed Map lookup to switch statement.
    	//This switch includes all characters upper and lowercase
    	//that can be a Nucleotide 
    	//AND ALL CHARACTERS IN BETWEEN.
    	//This is an optimization to allow the 
    	//compiler to use a tableswitch opcode
    	//instead of the more general purpose
    	//lookupswitch opcode.
    	//tableswitch is an O(1) lookup
    	//while lookupswitch is O(n) where n
    	//is the number of case statements in the switch.
    	//tableswitch requires consecutive case values.
    	//DO NOT CHANGE THE ORDER OF THE CASE STATEMENTS
    	//the cases have to be in ascii order
    	//so the JVM can do offset arithmetic
    	//to jump immediately to the correct case
    	//for an O(1) lookup, changing the order
    	//might not allow that. 
    	//(not sure if compiler is smart enough to re-order)
    	//
    	//for more information:
    	//The book Beautiful Code Chapter 6
    	//or
    	//http://www.artima.com/underthehood/flowP.html
    	final Nucleotide ret;
    	switch(base){
    	//we support gap characters from both consed and TIGR
    	//so we need to start with special characters
    		case '*': ret = Gap;
					break;
    		case '+': ret = null;
						break;
    		case ',': ret = null;
					break;
    		case '-': ret = Gap;
					break;		
    		case '.': ret = null;
						break;	
    		case '/': ret = null;
						break;	
			//numbers
    		case '0': ret = null;
						break;	
    		case '1': ret = null;
						break;
    		case '2': ret = null;
						break;
    		case '3': ret = null;
						break;
    		case '4': ret = null;
						break;
    		case '5': ret = null;
						break;
    		case '6': ret = null;
						break;
    		case '7': ret = null;
						break;
    		case '8': ret = null;
						break;
    		case '9': ret = null;
						break;
    		case ':': ret = null;
						break;
    		case ';': ret = null;
						break;
    		case '<': ret = null;
						break;
    		case '=': ret = null;
						break;
    		case '>': ret = null;
						break;
    		case '?': ret = null;
						break;
			//uppercase letters
			case 'A':
				ret = Adenine;
				break;
			case 'B':
				ret = NotAdenine;
				break;
			case 'C':
				ret = Cytosine;
				break;
			case 'D':
				ret = NotCytosine;
				break;
			case 'E':
				ret = null;
				break;
			case 'F':
				ret = null;
				break;
			case 'G':
				ret = Guanine;
				break;
			case 'H':
				ret = NotGuanine;
				break;
			case 'I':
				ret = null;
				break;
			case 'J':
				ret = null;
				break;
			case 'K':
				ret = Keto;
				break;
			case 'L':
				ret = null;
				break;
			case 'M':
				ret = Amino;
				break;
			case 'N':
				ret = Unknown;
				break;
			case 'O':
				ret = null;
				break;
			case 'P':
				ret = null;
				break;
			case 'Q':
				ret = null;
				break;
			case 'R':
				ret = Purine;
				break;
			case 'S':
				ret = Strong;
				break;
			case 'T':
				ret = Thymine;
				break;
			case 'U':
				ret = null;
				break;
			case 'V':
				ret = NotThymine;
				break;
			case 'W':
				ret = Weak;
				break;
			case 'X':
				ret = Unknown;
				break;
			case 'Y':
				ret = Pyrimidine;
				break;
			case 'Z':
				ret = null;
				break;
			//have to include all special characters in between
			case '[':
				ret = null;
				break;
			case '\\':
				ret = null;
				break;
			case ']':
				ret = null;
				break;
			case '^':
				ret = null;
				break;
			case '_':
				ret = null;
				break;
			case '`':
				ret = null;
				break;
    	//lowercase
    		case 'a' : ret = Adenine;
    					break;
    		case 'b' : ret = NotAdenine;
    					break;
    		case 'c' : ret =Cytosine;
    					break;
    		case 'd' : ret = NotCytosine;
    					break;
    		case 'e' : ret = null;
    					break;
    		case 'f' : ret = null;
						break;
    		case 'g' : ret = Guanine;
						break;	
    		case 'h' : ret = NotGuanine;
						break;
    		case 'i' : ret = null;
						break;
    		case 'j' : ret = null;
						break;	
    		case 'k' : ret = Keto;
						break;
    		case 'l' : ret = null;
						break;
    		case 'm' : ret = Amino;
						break;
    		case 'n' : ret = Unknown;
						break;
    		case 'o' : ret = null;
						break;
    		case 'p' : ret = null;
						break;
    		case 'q' : ret = null;
						break;
    		case 'r' : ret = Purine;
						break;
    		case 's' : ret = Strong;
						break;
    		case 't' : ret = Thymine;
						break;
    		case 'u' : ret = null;
						break;
    		case 'v' : ret = NotThymine;
						break;
    		case 'w' : ret = Weak;
						break;
    		case 'x' : ret = Unknown;
						break;
    		case 'y' : ret = Pyrimidine;
						break;
			default : ret = null;
						break;
			
    	}
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
