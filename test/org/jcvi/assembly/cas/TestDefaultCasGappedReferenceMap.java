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
 * Created on Jan 14, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.util.Arrays;

import org.jcvi.assembly.cas.alignment.CasAlignment;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegionType;
import org.jcvi.assembly.cas.alignment.DefaultCasAlignment;
import org.jcvi.assembly.cas.alignment.DefaultCasMatch;
import org.jcvi.assembly.cas.read.CasNucleotideDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestDefaultCasGappedReferenceMap {

    private static final String REFERENCE_CALLS_AS_STRING = "GTTCAAATTG";
    CasNucleotideDataStore referenceNucleotideDataStore;
    CasIdLookup contigNameLookup;
    DefaultCasGappedReferenceMap sut;
    long referenceId= 12345;
    String referenceName = "refName";
    NucleotideEncodedGlyphs referenceCalls = new DefaultNucleotideEncodedGlyphs(REFERENCE_CALLS_AS_STRING);
    @Before
    public void setup() throws DataStoreException{
        referenceNucleotideDataStore = createMock(CasNucleotideDataStore.class);
        contigNameLookup = createMock(CasIdLookup.class);
        sut = new DefaultCasGappedReferenceMap(referenceNucleotideDataStore, contigNameLookup);
        expect(referenceNucleotideDataStore.get(referenceName)).andReturn(referenceCalls);
        expect(contigNameLookup.getLookupIdFor(referenceId)).andReturn(referenceName);
    }
    
    private CasMatch createMatchFor(CasAlignment... alignments){
        return new DefaultCasMatch(true, false, false, false, Arrays.asList(alignments));
    }
    @Test
    public void oneReadNoInsertsShouldNotAddAnyGaps(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .build();
        CasMatch match = createMatchFor(alignment);
        replay(referenceNucleotideDataStore, contigNameLookup);
        sut.visitMatch(match);
        sut.visitEndOfFile();
        assertEquals(referenceCalls.decode(),sut.getGappedReferenceFor(referenceId).decode());
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
    
    @Test(expected = IllegalStateException.class)
    public void dataStoreExceptionShouldThrowIllegalStateException() throws DataStoreException{
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .build();
        CasMatch match = createMatchFor(alignment);
        reset(referenceNucleotideDataStore);
        DataStoreException expectedDataStoreException = new DataStoreException("expected");
        expect(referenceNucleotideDataStore.get(referenceName)).andThrow(expectedDataStoreException);
        replay(referenceNucleotideDataStore, contigNameLookup);
        sut.visitMatch(match);
        sut.visitEndOfFile();
             
    }
    @Test
    public void ifLastAlignmentIsInsertShouldBeIgnored(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .addRegion(CasAlignmentRegionType.INSERT, 10)
                                                .build();
        CasMatch match = createMatchFor(alignment);
        replay(referenceNucleotideDataStore, contigNameLookup);
        sut.visitMatch(match);
        sut.visitEndOfFile();
        assertEquals(referenceCalls.decode(),sut.getGappedReferenceFor(referenceId).decode());
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
    @Test
    public void twoReadsNoInsertsShouldNotAddAnyGaps(){
        CasAlignment alignment1 = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .build();
        CasAlignment alignment2 = new DefaultCasAlignment.Builder(referenceId,0,false)
                                            .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 5)
                                            .build();
        CasMatch match = createMatchFor(alignment1,alignment2);
        replay(referenceNucleotideDataStore, contigNameLookup);
        sut.visitMatch(match);
        sut.visitEndOfFile();
        assertEquals(referenceCalls.decode(),sut.getGappedReferenceFor(referenceId).decode());
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
    @Test
    public void oneReadOneInsertShouldAddOneGap(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
                                                .build();
        CasMatch match = createMatchFor(alignment);
        replay(referenceNucleotideDataStore, contigNameLookup);
        sut.visitMatch(match);
        sut.visitEndOfFile();
        assertEquals("GTTC-AAATTG",
                NucleotideGlyph.convertToString(sut.getGappedReferenceFor(referenceId).decode()));
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
    @Test
    public void twoReadsOneHasInsertShouldAddOneGap(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
                                                .build();
        CasAlignment alignment2 = new DefaultCasAlignment.Builder(referenceId,0,false)
        .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 5)
        .build();
        CasMatch match = createMatchFor(alignment,alignment2);
        replay(referenceNucleotideDataStore, contigNameLookup);
        sut.visitMatch(match);
        sut.visitEndOfFile();
        assertEquals("GTTC-AAATTG",
                NucleotideGlyph.convertToString(sut.getGappedReferenceFor(referenceId).decode()));
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
    @Test
    public void twoReadsBothHaveSameInsertShouldAddOneGap(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
                                                .build();
        CasAlignment alignment2 = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 1)
                                                .build();
        CasMatch match = createMatchFor(alignment,alignment2);
        replay(referenceNucleotideDataStore, contigNameLookup);
        sut.visitMatch(match);
        sut.visitEndOfFile();
        assertEquals("GTTC-AAATTG",
                NucleotideGlyph.convertToString(sut.getGappedReferenceFor(referenceId).decode()));
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
    @Test
    public void twoReadsBothHavedifferentInsertShouldAddTwoGaps(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
                                                .build();
        CasAlignment alignment2 = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 7)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 3)
                                                .build();
        CasMatch match = createMatchFor(alignment,alignment2);
        replay(referenceNucleotideDataStore, contigNameLookup);
        sut.visitMatch(match);
        sut.visitEndOfFile();
        assertEquals("GTTC-AAA-TTG",
                NucleotideGlyph.convertToString(sut.getGappedReferenceFor(referenceId).decode()));
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
    @Test
    public void twoReadsBothHaveDifferentLengthInsertAtSameLocationShouldAddLongestGap(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
                                                .build();
        CasAlignment alignment2 = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 2)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 3)
                                                .build();
        CasMatch match = createMatchFor(alignment,alignment2);
        replay(referenceNucleotideDataStore, contigNameLookup);
        sut.visitMatch(match);
        sut.visitEndOfFile();
        assertEquals("GTTC--AAATTG",
                NucleotideGlyph.convertToString(sut.getGappedReferenceFor(referenceId).decode()));
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
}
