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

package org.jcvi.common.core.symbol.residue.aa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.Codon;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
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
public class TestCodon {

    
    private static final String aminoAcids  = "KNKKNNTTTTTTTTTTTTTTTRSRRSSIIMIIIIIQHQQHHPPPPPPPPPPPPPPPRRRRRRRRRRRRRRRLLLLLLLLLLLLLLLEDEEDDAAAAAAAAAAAAAAAGGGGGGGGGGGGGGGVVVVVVVVVVVVVVVRRR*Y**YYSSSSSSSSSSSSSSS*CWCCLFLLFFLLL";
    private static final String base1       = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGMMMTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTYYY";
    private static final String base2       = "AAAAAACCCCCCCCCCCCCCCGGGGGGTTTTTTTTAAAAAACCCCCCCCCCCCCCCGGGGGGGGGGGGGGGTTTTTTTTTTTTTTTAAAAAACCCCCCCCCCCCCCCGGGGGGGGGGGGGGGTTTTTTTTTTTTTTTGGGAAAAAACCCCCCCCCCCCCCCGGGGGTTTTTTTTT";
    private static final String base3       = "ACGRTYABCDGHKMNRSTVWYACGRTYACGHMTWYACGRTYABCDGHKMNRSTVWYABCDGHKMNRSTVWYABCDGHKMNRSTVWYACGRTYABCDGHKMNRSTVWYABCDGHKMNRSTVWYABCDGHKMNRSTVWYAGRACGRTYABCDGHKMNRSTVWYACGTYACGRTYAGR";

    private static final List<Nucleotide> EXPECTED_START_CODON = Nucleotides.getNucleotidesFor("ATG");
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
            AminoAcid aa = aminoAbbreviation !='*'? AminoAcid.getGlyphFor(aminoAbbreviation): null;
            List<Nucleotide> codon = Nucleotides.getNucleotidesFor(
                                            Arrays.asList(base1.charAt(i),
                                                            base2.charAt(i),
                                                            base3.charAt(i)));
            boolean isStartCodon = codon.equals(EXPECTED_START_CODON);
           
            Codon sut = Codon.getCodonFor(codon);
            boolean isStopCodon = Codon.getStopCodons().contains(sut);
            data.add(new Object[]{sut, codon,aa, isStartCodon, isStopCodon});
        }
        return data;
        
        }
    
    
    /**
     * @param expectedBasecalls
     * @param expectedAminoAcid
     * @param isStartCodon
     * @param isStopCodon
     */
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
