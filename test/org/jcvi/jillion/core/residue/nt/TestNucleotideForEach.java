package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
public class TestNucleotideForEach {

    @Test
    public void forEachBiConsumer(){
        NucleotideSequence sut = NucleotideSequence.of("ACGTTTTGGTGTG");
        
        List<Nucleotide> expected = new ArrayList<>();
        for(Nucleotide n : sut){
            expected.add(n);
        }
        
        List<Nucleotide> actual = new ArrayList<>((int) sut.getLength());
        for(int i=0; i< sut.getLength(); i++){
            actual.add(null);
        }
        
        sut.forEach((i,n)-> actual.set((int)i,n));
                
        assertEquals(expected, actual);
    }
    @Test
    public void forEach(){
        NucleotideSequence sut = NucleotideSequence.of("ACGTTTTGGTGTG");
        
        List<Nucleotide> expected = new ArrayList<>();
        for(Nucleotide n : sut){
            expected.add(n);
        }
        
        List<Nucleotide> actual = new ArrayList<>((int) sut.getLength());
       
        
        sut.forEach(n -> actual.add(n));
                
        assertEquals(expected, actual);
    }
    
    @Test
    public void forEachRange(){
        NucleotideSequence sut = NucleotideSequence.of("ACGTTTTGGTGTG");
        
        Range range = Range.of(3,10);
        List<Nucleotide> expected = new ArrayList<>();
        for(long i= range.getBegin(); i <= range.getEnd(); i++){
            expected.add(sut.get(i));
        }
        
        List<Nucleotide> actual = new ArrayList<>((int) sut.getLength());
        
        
        sut.forEach(range, n -> actual.add(n));
                
        assertEquals(expected, actual);
    }
    @Test
    public void forEachRangeBiConsumer(){
        NucleotideSequence sut = NucleotideSequence.of("ACGTTTTGGTGTG");
        
        Range range = Range.of(3,10);
        List<Nucleotide> expected = new ArrayList<>();
        for(long i= range.getBegin(); i <= range.getEnd(); i++){
            expected.add(sut.get(i));
        }
        
        
        List<Nucleotide> actual = new ArrayList<>((int) range.getLength());
        for(int i=0; i< range.getLength(); i++){
            actual.add(null);
        }
        
        sut.forEach(range, (i,n) -> actual.set((int)(i - range.getBegin()), n));
                
        assertEquals(expected, actual);
    }
}
