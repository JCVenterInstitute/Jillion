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

import java.util.Arrays;
import java.util.Collection;

import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
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
public class TestAminoAcid {
    @Parameters
    public static Collection<?> data(){

        return Arrays.asList(new Object[][]{
                new Object[]{AminoAcid.Isoleucine,"Isolucine","Ile","I"},
                new Object[]{AminoAcid.Leucine,"Leucine","Leu","L"},
                new Object[]{AminoAcid.Lysine,"Lysine","Lys","K"},
                new Object[]{AminoAcid.Methionine,"Methionine","Met","M"},
                new Object[]{AminoAcid.Phenylalanine,"Phenylalanine","Phe","F"},
                new Object[]{AminoAcid.Threonine,"Threonine","Thr","T"},
                new Object[]{AminoAcid.Tryptophan,"Tryptophan","Trp","W"},
                new Object[]{AminoAcid.Valine,"Valine","Val","V"},
                new Object[]{AminoAcid.Cysteine,"Cysteine","Cys","C"},
                new Object[]{AminoAcid.Glutamine,"Glutamine","Gln","Q"},
                new Object[]{AminoAcid.Glycine,"Glycine","Gly", "G"},
                new Object[]{AminoAcid.Proline,"Proline","Pro","P"},
                new Object[]{AminoAcid.Serine,"Serine","Ser","S"},
                new Object[]{AminoAcid.Tyrosine,"Tyrosine","Tyr", "Y"},
                new Object[]{AminoAcid.Arginine,"Arginine","Arg","R"},
                new Object[]{AminoAcid.Histidine,"Histidine","His","H"},
                new Object[]{AminoAcid.Alanine,"Alanine","Ala","A"},
                new Object[]{AminoAcid.Asparagine,"Asparagine","Asn","N"},
                new Object[]{AminoAcid.Aspartic_Acid,"Aspartic Acid", "Asp","D"},
                new Object[]{AminoAcid.Glutamic_Acid,"Glutamic Acid","Glu","E"}
        });
    }
    
    private final String abbreviation;
    private final String threeLetterAbbreviation;
    private final String name;
    private final AminoAcid expectedAminoAcid;
    /**
     * @param name
     * @param threeLetterAbbreviation
     * @param abbreviation
     */
    public TestAminoAcid(AminoAcid expectedAminoAcid,String name, String threeLetterAbbreviation,
            String abbreviation) {
        this.expectedAminoAcid = expectedAminoAcid;
        this.name = name;
        this.threeLetterAbbreviation = threeLetterAbbreviation;
        this.abbreviation = abbreviation;
    }
    
   @Test
   public void getGlyphFor(){
       assertSame("full name",expectedAminoAcid,AminoAcid.getGlyphFor(name));
       assertSame("full name lowercase",expectedAminoAcid,AminoAcid.getGlyphFor(name.toLowerCase()));
       assertSame("full name uppercase",expectedAminoAcid,AminoAcid.getGlyphFor(name.toUpperCase()));
       
       assertSame("3 letter abbreviation",expectedAminoAcid,AminoAcid.getGlyphFor(threeLetterAbbreviation));
       assertSame("3 letter abbreviation lowercase",expectedAminoAcid,AminoAcid.getGlyphFor(threeLetterAbbreviation.toLowerCase()));
       assertSame("3 letter abbreviation uppercase",expectedAminoAcid,AminoAcid.getGlyphFor(threeLetterAbbreviation.toUpperCase()));
       
       assertSame("abbreviation",expectedAminoAcid,AminoAcid.getGlyphFor(abbreviation));
       assertSame("abbreviation lowercase",expectedAminoAcid,AminoAcid.getGlyphFor(abbreviation.toLowerCase()));
       assertSame("abbreviation uppercase",expectedAminoAcid,AminoAcid.getGlyphFor(abbreviation.toUpperCase()));
   }
   @Test
   public void name(){
       assertEquals(name, expectedAminoAcid.getName());
   }
   @Test
   public void threeLetterAbreviation(){
       assertEquals(threeLetterAbbreviation, expectedAminoAcid.get3LetterAbbreviation());
   }
   @Test
   public void abbreviation(){
       assertEquals(abbreviation, expectedAminoAcid.getAbbreviation().toString());
   }
}
