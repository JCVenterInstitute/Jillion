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
 * Created on Mar 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nuc;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.util.Caches;

/**
 * {@code DefaultNucleotideSequence} is the default
 * implementation of a {@link NucleotideSequence}.  Internally,
 * each {@link Nucleotide} is encoded as 2 bits.
 * @author dkatzel
 *
 *
 */
class DefaultNucleotideSequence extends AbstractNucleotideSequence{
    
    private static final Map<Integer,NucleotideSequence> CACHE;
    
    static{
        CACHE = Caches.createSoftReferencedValueCache();
    }
    
    private final NucleotideEncodedSequence encodedBasecalls;
    
    private static synchronized NucleotideSequence cache(String seq){
        Integer key = seq.hashCode();
        if(CACHE.containsKey(key)){
            return CACHE.get(key);
        }
        NucleotideSequence value= new DefaultNucleotideSequence(Nucleotides.parse(seq));
        CACHE.put(key,value);
        return value;
    }
    
    private static synchronized NucleotideSequence cache(Collection<Nucleotide> seq){
        Integer key = Nucleotides.asString(seq).hashCode();
        if(CACHE.containsKey(key)){
            return CACHE.get(key);
        }
        NucleotideSequence value= new DefaultNucleotideSequence(seq);
        CACHE.put(key,value);
        return value;
    }
    
    private static synchronized NucleotideSequence cache(Collection<Nucleotide> seq, NucleotideCodec codec){
        Integer key = Nucleotides.asString(seq).hashCode();
        if(CACHE.containsKey(key)){
            return CACHE.get(key);
        }
        NucleotideSequence value= new DefaultNucleotideSequence(seq,codec);
        CACHE.put(key,value);
        return value;
    }
    
    public static NucleotideSequence create(CharSequence nucleotides){
        return cache(nucleotides.toString());
    }
    public static NucleotideSequence create(char[] nucleotides){
        return cache(new String(nucleotides));
    }
    
    public static NucleotideSequence create(Collection<Nucleotide> nucleotides){
        return cache(nucleotides);
    }
    public static DefaultNucleotideSequence createACGTN(Collection<Nucleotide> nucleotides){
        return new DefaultNucleotideSequence(nucleotides, ACGTNNucloetideCodec.INSTANCE);
    }
    public static DefaultNucleotideSequence createNoAmbiguities(Collection<Nucleotide> nucleotides){
        return new DefaultNucleotideSequence(nucleotides, NoAmbiguitiesEncodedNucleotideCodec.INSTANCE);
    }
    public static NucleotideSequence createGappy(Collection<Nucleotide> nucleotides){
        return cache(nucleotides, DefaultNucleotideCodec.INSTANCE);
    }
    public static NucleotideSequence createGappy(CharSequence nucleotides){
        return cache(Nucleotides.parse(nucleotides), DefaultNucleotideCodec.INSTANCE);
    }
    private DefaultNucleotideSequence(Collection<Nucleotide> nucleotides){
        this(nucleotides,NucleotideCodecs.getNucleotideCodecFor(nucleotides));
   
    }
    private DefaultNucleotideSequence(Collection<Nucleotide> nucleotides,NucleotideCodec codec ){
        this.encodedBasecalls = new NucleotideEncodedSequence(codec,nucleotides);
   
    }
    
    private DefaultNucleotideSequence(String basecalls){
        this(Nucleotides.parse(basecalls));
    }

    
    @Override
    public List<Integer> getGapOffsets() {
        return encodedBasecalls.getGapOffsets();
    }

    @Override
    public List<Nucleotide> asList() {
        return encodedBasecalls.asList();
    }

    @Override
    public Nucleotide get(int index) {       
        return encodedBasecalls.get(index);
    }

    @Override
    public long getLength() {
        return encodedBasecalls.getLength();
    }
    @Override
    public boolean isGap(int index) {
        return encodedBasecalls.isGap(index);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + encodedBasecalls.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultNucleotideSequence)){
            return false;
        }
        DefaultNucleotideSequence other = (DefaultNucleotideSequence) obj;
       return encodedBasecalls.equals(other.encodedBasecalls);
    }
    @Override
    public String toString() {
        return Nucleotides.asString(asList());
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps() {
        return encodedBasecalls.getNumberOfGaps();
    }

    
}
