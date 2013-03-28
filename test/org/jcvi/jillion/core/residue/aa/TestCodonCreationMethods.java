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
package org.jcvi.jillion.core.residue.aa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.jillion.core.residue.aa.Codon;
import org.jcvi.jillion.core.residue.aa.Codon.Frame;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestCodonCreationMethods {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        for(Entry<List<Nucleotide>, Codon> entry : Codon.getCodonMap().entrySet()){
            data.add(new Object[]{entry.getKey(), entry.getValue()});
        }
        return data;
    }
    
    private final List<Nucleotide> triplet;
    private final Codon expectedCodon;
    private final  String tripletBases;
    /**
     * @param triplet
     * @param expectedCodon
     */
    public TestCodonCreationMethods(List<Nucleotide> triplet,
            Codon expectedCodon) {
        this.triplet = triplet;
        this.expectedCodon = expectedCodon;
        this.tripletBases = new NucleotideSequenceBuilder(triplet).toString();
    }
    
    @Test
    public void translateSingleCodon(){
        assertEquals(expectedCodon, Codon.getCodonFor(triplet));
    }
    @Test
    public void translateSingleCodonNonList(){
        assertEquals(expectedCodon, Codon.getCodonFor(triplet.get(0), triplet.get(1), triplet.get(2)));
    }
    @Test
    public void translateSingleCodonOffset1(){
        String basecalls = "N"+tripletBases;
        assertEquals(expectedCodon, Codon.getCodonByOffset(basecalls,1));
    }
    @Test
    public void translateSingleCodonOffset2(){
        String basecalls = "NN"+tripletBases;
        assertEquals(expectedCodon, Codon.getCodonByOffset(basecalls,2));
    }
    @Test
    public void translateMultipleCodonsAsString(){
        String basecalls = tripletBases+tripletBases;
        assertEquals(Arrays.asList(expectedCodon,expectedCodon), Codon.getCodonsFor(basecalls));
    }
    @Test
    public void translateMultipleCodons(){
        String basecalls = tripletBases+tripletBases;
        assertEquals(Arrays.asList(expectedCodon,expectedCodon), Codon.getCodonsFor(new NucleotideSequenceBuilder(basecalls).build()));
    }
    @Test
    public void translateMultipleCodonsFrame1(){
        String basecalls = "N"+tripletBases+tripletBases+"N";
        assertEquals(Arrays.asList(expectedCodon,expectedCodon), 
                Codon.getCodonsFor(new NucleotideSequenceBuilder(basecalls).build(), Frame.ONE));
    }
    @Test
    public void translateMultipleCodonsFrame2(){
        String basecalls = "NN"+tripletBases+tripletBases+"NN";
        assertEquals(Arrays.asList(expectedCodon,expectedCodon), 
                Codon.getCodonsFor(new NucleotideSequenceBuilder(basecalls).build(), Frame.TWO));
    }
}
