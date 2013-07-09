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
package org.jcvi.jillion.core.residue.nt;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jcvi.jillion.internal.core.util.GrowableIntArray;

/**
 * {@code Nucleotides} is a helper class
 * that works with Collections of {@link Nucleotide}
 * objects.
 * @author dkatzel
 *
 */
final class Nucleotides {
	/**
	 * Pattern to detect whitespace, used in removing whitespace from strings.
	 * This has been refactored out because calling
	 * {@link String#replaceAll(String, String)}
	 * recompiles the pattern for every invocation.
	 */
	private static Pattern WHITESPACE = Pattern.compile("\\s+");
    /**
     * Can not instantiate.
     */
    private Nucleotides(){
        throw new IllegalStateException("not allowed to instantiate");
    }
    /**
     * Parse the given {@link CharSequence} containing
     * nucleotides and return an equivalent List
     * of {@link Nucleotide}s.
     * @param nucleotides the charSequence to parse, may have
     * whitespace which will be ignored.
     * @return a new List will never be null.
     * @throws NullPointerException if nucleotides is null.
     * @throws IllegalArgumentException if there is a
     * character in the nucleotides aside from leading or trailing 
     * whitespace that can not be parsed into a Nucleotide.
     *
     */
    public static List<Nucleotide> parse(CharSequence nucleotides){
        String trimmed = WHITESPACE.matcher(nucleotides).replaceAll("");
        List<Nucleotide> result = new ArrayList<Nucleotide>(trimmed.length());
        try{
            for(int i=0; i<trimmed.length(); i++){            
                result.add(Nucleotide.parse(trimmed.charAt(i)));
            }
            return result;
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("could not parse "+ nucleotides,e);
        }
        
    }
   
    static byte[] encodeWithNSentientals(NucleotideCodec codec, List<Nucleotide> bases){
    	GrowableIntArray ns = new GrowableIntArray(20);
    	for(int i=0; i<bases.size(); i++){
    		if(bases.get(i)==Nucleotide.Unknown){
    			ns.append(i);
    		}
    	}
    	
    	return codec.encode(bases.size(), ns.toArray(), bases.iterator());
    	
    }
    
    static byte[] encodeWithGapSentientals(NucleotideCodec codec, List<Nucleotide> bases){
    	GrowableIntArray gaps = new GrowableIntArray(20);
    	for(int i=0; i<bases.size(); i++){
    		if(bases.get(i).isGap()){
    			gaps.append(i);
    		}
    	}
    	
    	return codec.encode(bases.size(), gaps.toArray(), bases.iterator());
    	
    }
}
