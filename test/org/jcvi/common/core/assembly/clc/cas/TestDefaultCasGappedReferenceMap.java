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
package org.jcvi.common.core.assembly.clc.cas;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.assembly.clc.cas.CasIdLookup;
import org.jcvi.common.core.assembly.clc.cas.CasMatch;
import org.jcvi.common.core.assembly.clc.cas.DefaultCasGappedReferenceMap;
import org.jcvi.common.core.assembly.clc.cas.align.CasAlignment;
import org.jcvi.common.core.assembly.clc.cas.align.CasAlignmentRegionType;
import org.jcvi.common.core.assembly.clc.cas.align.DefaultCasAlignment;
import org.jcvi.common.core.assembly.clc.cas.align.DefaultCasMatch;
import org.jcvi.common.core.assembly.clc.cas.read.CasNucleotideDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestDefaultCasGappedReferenceMap {

    private static final String REFERENCE_CALLS_AS_STRING = "GTTCAAATTG";
    CasNucleotideDataStore referenceNucleotideDataStore;
    CasIdLookup contigNameLookup;
    DefaultCasGappedReferenceMap sut;
    long referenceId= 0;
    String referenceName = "refName";
    NucleotideSequence referenceCalls = new NucleotideSequenceBuilder(REFERENCE_CALLS_AS_STRING).build();
    @Before
    public void setup() throws DataStoreException{
        referenceNucleotideDataStore = createMock(CasNucleotideDataStore.class);
        contigNameLookup = createMock(CasIdLookup.class);
        sut = new DefaultCasGappedReferenceMap(referenceNucleotideDataStore, contigNameLookup);
        expect(referenceNucleotideDataStore.get(referenceName)).andReturn(referenceCalls);
        expect(contigNameLookup.getLookupIdFor(referenceId)).andReturn(referenceName);
    }
    
    private List<CasMatch> createMatchesFor(CasAlignment... alignments){
        List<CasMatch> matches = new ArrayList<CasMatch>();
        for(CasAlignment alignment: alignments){
            matches.add( new DefaultCasMatch(true, 1, 1L,false, alignment,0));
        }
        return matches;
    }
    @Test
    public void oneReadNoInsertsShouldNotAddAnyGaps(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .build();
        replay(referenceNucleotideDataStore, contigNameLookup);
        for(CasMatch match : createMatchesFor(alignment)){
            sut.visitMatch(match);
        }
        sut.visitEndOfFile();
        assertEquals(referenceCalls.asList(),sut.getGappedReferenceFor(referenceId).asList());
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
    
    @Test(expected = IllegalStateException.class)
    public void dataStoreExceptionShouldThrowIllegalStateException() throws DataStoreException{
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .build();
        reset(referenceNucleotideDataStore);
        for(CasMatch match : createMatchesFor(alignment)){
            sut.visitMatch(match);
        }
        DataStoreException expectedDataStoreException = new DataStoreException("expected");
        expect(referenceNucleotideDataStore.get(referenceName)).andThrow(expectedDataStoreException);
        replay(referenceNucleotideDataStore, contigNameLookup);
        for(CasMatch match : createMatchesFor(alignment)){
            sut.visitMatch(match);
        }
        sut.visitEndOfFile();
             
    }
    @Test
    public void ifLastAlignmentIsInsertShouldBeIgnored(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .addRegion(CasAlignmentRegionType.INSERT, 10)
                                                .build();
        replay(referenceNucleotideDataStore, contigNameLookup);
        for(CasMatch match : createMatchesFor(alignment)){
            sut.visitMatch(match);
        }
        sut.visitEndOfFile();
        assertEquals(referenceCalls.asList(),sut.getGappedReferenceFor(referenceId).asList());
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
        replay(referenceNucleotideDataStore, contigNameLookup);
        for(CasMatch match : createMatchesFor(alignment1, alignment2)){
            sut.visitMatch(match);
        }
        sut.visitEndOfFile();
        assertEquals(referenceCalls.asList(),sut.getGappedReferenceFor(referenceId).asList());
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
    @Test
    public void oneReadOneInsertShouldAddOneGap(){
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
                                                .build();
        replay(referenceNucleotideDataStore, contigNameLookup);
        for(CasMatch match : createMatchesFor(alignment)){
            sut.visitMatch(match);
        }
        sut.visitEndOfFile();
        assertEquals("GTTC-AAATTG",
        		sut.getGappedReferenceFor(referenceId).toString());
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
        replay(referenceNucleotideDataStore, contigNameLookup);
        for(CasMatch match : createMatchesFor(alignment, alignment2)){
            sut.visitMatch(match);
        }
        sut.visitEndOfFile();
        assertEquals("GTTC-AAATTG",
                sut.getGappedReferenceFor(referenceId).toString());
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
        replay(referenceNucleotideDataStore, contigNameLookup);
        for(CasMatch match : createMatchesFor(alignment, alignment2)){
            sut.visitMatch(match);
        }
        sut.visitEndOfFile();
        assertEquals("GTTC-AAATTG",
        		sut.getGappedReferenceFor(referenceId).toString());
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
        replay(referenceNucleotideDataStore, contigNameLookup);
        for(CasMatch match : createMatchesFor(alignment, alignment2)){
            sut.visitMatch(match);
        }
        sut.visitEndOfFile();
        assertEquals("GTTC-AAA-TTG",
        		sut.getGappedReferenceFor(referenceId).toString());
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
        replay(referenceNucleotideDataStore, contigNameLookup);
        for(CasMatch match : createMatchesFor(alignment, alignment2)){
            sut.visitMatch(match);
        }
        sut.visitEndOfFile();
        assertEquals("GTTC--AAATTG",
        		sut.getGappedReferenceFor(referenceId).toString());
        verify(referenceNucleotideDataStore, contigNameLookup);        
    }
}
