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

package org.jcvi.trace.frg.afg;
import java.io.IOException;

import org.jcvi.Range;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.encoder.TigrQualitiesEncodedGyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideSequence;
import org.jcvi.glyph.phredQuality.EncodedQualitySequence;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAmosFragmentDataStore {

    private static ResourceFileServer RESOURCES = new ResourceFileServer(TestDefaultAmosFragmentDataStore.class);
    private static final TigrQualitiesEncodedGyphCodec QUALITY_CODEC = TigrQualitiesEncodedGyphCodec.getINSTANCE();
    
    DefaultAmosFragmentFileDataStore sut;
    
    AmosFragment JGBAA01T21H05PB2A2341BRB = new DefaultAmosFragment(
            "JGBAA01T21H05PB2A2341BRB", 48, 
           new DefaultNucleotideSequence(
                    "aacgggtcgtttttacattcgacattaattgatggccatccgaatccttttggtcgctgtctggctgtca" +
"gaaattatgctagagtcccgtttccgtttcattaccaacaccacgtctccttgcccaattagcacattag" +
"ccttttctccctttgcaagattgctcagttcattgatgctcaatgctgggccgtatctcttgtcttcttt" +
"gcccaaaatgaggaaacctcttaaaacagcagactctactccagctgtgccttcatctggatcttcggtc" +
"aatgcacctgcatctttcccgagaaccgtaagtctcttggtggccttgttgtaattgaacactggagaat" +
"tgcctctcaccagtatcctcatccctgatcctctcacattcacagtcaaggaggagaactgcatcctact" +
"ctgttctggtggggcagcagcaaaggggagaagttttattatctggacagtgtcaaacgtcccaagcaca" +
"tcccgcatctgctggaatagtgtcctcacgaatccactgtactgacctctggctgccttagggaccagag" +
"actgaaatggctcgaattccatcttgttatacaaaattgtgggatcctgtgaccattgaattttcacagt" +
"ttcccaatttctgatgatccactgataagtattgaccaacactgactcaggaccattgatctcccacatc" +
"actggtt"),
new EncodedQualitySequence(
        RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, 
        QUALITY_CODEC.decode(
        ("66667778<<<>A>>>F>@AA>>><<GFEAA@ACAAAHLCSSMSSRR\\\\\\SSSSS\\____]V]\\SKC=>7"+
        "99999==AICKXXWWLXXX_YVVXXXXNMJJPPVVRMMHE@=999>DPTXPXLERM\\XXYYV\\]\\V\\___"+
        "_]a__________X\\\\\\XVVXVV_____Y_WWCCWW__\\___YV\\aaaa]aY_Y__Yaa_aaa]Y__Y__"+
        "_XX\\\\S\\________Y_T________Y______V_____\\\\\\S\\V____Y__Y____Y\\\\V\\V\\a]___Y"+
        "___]_YY____Y___WWAAWW_VV_______Y_____Y_a_YV____Y__Y]______\\__\\]\\]VQRX\\"+
        "VV]aa__________V_______a]______Y_YYY_______Y__YY_____V___V_\\____Y_____"+
        "_aaaaa_____________aa_________________Y___Ya_______________Y__________"+
        "__aa______aaaY______aa]aa_]____\\V_______________VVX\\]\\___]X__X]V\\V\\XXX"+
        "XX]_\\\\\\VVV_____\\T__]\\XV\\_YV\\]KJ=@WW__Y_aa__Y_Y_aa\\V\\\\\\\\_____T__Y______"+
        "aa____\\__aa_______V__]]\\\\\\]_\\\\\\X\\\\\\\\\\]\\\\_V\\]XX]XXWDDDKAA99D99KHSJNQQQS"+
        "\\\\F@977").getBytes())
        ), 

Range.buildRange(17,678), Range.buildRange(0,678), Range.buildRange(17,678));
    @Before
    public void setup() throws IOException{
        sut = new DefaultAmosFragmentFileDataStore(RESOURCES.getFile("files/PB2.afg"));
    }
    
    @Test
    public void size() throws DataStoreException{
        assertEquals(48, sut.size());
    }
    @Test
    public void lastRecord() throws DataStoreException{
        AmosFragment actual = sut.get("JGBAA01T21H05PB2A2341BRB");
        assertEquals(JGBAA01T21H05PB2A2341BRB, actual);
    }
    
    
}
