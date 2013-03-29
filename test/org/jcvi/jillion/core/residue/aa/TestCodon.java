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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestCodon {

    
    private static final String aminoAcids  = "KNKKNNTTTTTTTTTTTTTTTRSRRSSIIMIIIIIQHQQHHPPPPPPPPPPPPPPPRRRRRRRRRRRRRRRLLLLLLLLLLLLLLLEDEEDDAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGVVVVVVVVVVVVVVVRRR*Y**YYSSSSSSSSSSSSSSS*CWCCLFLLFFLLL";
    private static final String base1       = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGMMMTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTYYY";
    private static final String base2       = "AAAAAACCCCCCCCCCCCCCCGGGGGGTTTTTTTTAAAAAACCCCCCCCCCCCCCCGGGGGGGGGGGGGGGTTTTTTTTTTTTTTTAAAAAACCCCCCCCCCCCCCCGGGGGGGGGGGGGGGTTTTTTTTTTTTTTTGGGAAAAAACCCCCCCCCCCCCCCGGGGGTTTTTTTTT";
    private static final String base3       = "ACGRTYABCDGHKMNRSTVWYACGRTYACGHMTWYACGRTYABCDGHKMNRSTVWYABCDGHKMNRSTVWYABCDGHKMNRSTVWYACGRTYABCDGHKMNRSTVWYABCDGHKMNRSTVWYABCDGHKMNRSTVWYAGRACGRTYABCDGHKMNRSTVWYACGTYACGRTYAGR";

    private static final List<Nucleotide> EXPECTED_START_CODON = Arrays.asList(Nucleotide.Adenine,Nucleotide.Thymine, Nucleotide.Guanine);
    private final AminoAcid expectedAminoAcid;
    private final boolean isStartCodon;
    private final boolean isStopCodon;
    private final Codon sut;
    private final List<Nucleotide> actualBases;
    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        for(int i=0; i<aminoAcids.length(); i++){
            final char aminoAbbreviation = aminoAcids.charAt(i);
            AminoAcid aa = aminoAbbreviation !='*'? AminoAcid.parse(aminoAbbreviation): null;
            List<Nucleotide> codon = 
                                            Arrays.asList(Nucleotide.parse(base1.charAt(i)),
                                            		Nucleotide.parse(base2.charAt(i)),
                                            		Nucleotide.parse(base3.charAt(i)));
            boolean isStartCodon = codon.equals(EXPECTED_START_CODON);
           
            Codon sut = Codon.getCodonFor(codon);
            boolean isStopCodon = Codon.getStopCodons().contains(sut);
            data.add(new Object[]{sut, codon,aa, isStartCodon, isStopCodon});
        }
        return data;
        
        }
    
    
 
    public TestCodon(Codon sut,List<Nucleotide> actualBases,
            AminoAcid expectedAminoAcid, boolean isStartCodon,
            boolean isStopCodon) {
        this.sut = sut;
        this.actualBases = actualBases;
        this.expectedAminoAcid = expectedAminoAcid;
        this.isStartCodon = isStartCodon;
        this.isStopCodon = isStopCodon;
    }

    
    @Test
    public void aminoAcid(){
        assertEquals(actualBases.toString(),expectedAminoAcid, sut.getAminoAcid());
    }
    @Test
    public void isStartCodon(){
        assertEquals(actualBases.toString(),isStartCodon, sut.isStartCodon());
    }
    @Test
    public void isStopCodon(){
        assertEquals(actualBases.toString(),isStopCodon, sut.isStopCodon());
    }
}
