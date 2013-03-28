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
/*
 * Created on Jan 14, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor.CasVisitorCallback;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestCasGappedReferenceDataStoreBuilderVisitor {

    CasGappedReferenceDataStoreBuilderVisitor sut;
    long referenceId= 0;
    String referenceName = "refName";
    NucleotideSequence referenceCalls = new NucleotideSequenceBuilder("GTTCAAATTG").build();
    
    CasMatchVisitor matchVisitor;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void setup() throws DataStoreException, IOException{
    	File refFasta = folder.newFile("ref.fasta");
    	NucleotideSequenceFastaRecordWriter fastaWriter = new NucleotideSequenceFastaRecordWriterBuilder(refFasta)
    															.build();
    	fastaWriter.write(referenceName, new NucleotideSequenceBuilder("GTTCAAATTG").build());
       fastaWriter.close();
        sut = new CasGappedReferenceDataStoreBuilderVisitor(folder.getRoot());
        CasFileInfo refInfo = createMock(CasFileInfo.class);
        expect(refInfo.getFileNames()).andReturn(Arrays.asList(refFasta.getName()));
        replay(refInfo);
        sut.visitReferenceFileInfo(refInfo);
        matchVisitor = sut.visitMatches(createMock(CasVisitorCallback.class));
    }
    
    private List<CasMatch> createMatchesFor(CasAlignment... alignments){
        List<CasMatch> matches = new ArrayList<CasMatch>();
        for(CasAlignment alignment: alignments){
            matches.add( new DefaultCasMatch(true, 1, 1L,false, alignment,0));
        }
        return matches;
    }
    @Test
    public void oneReadNoInsertsShouldNotAddAnyGaps() throws DataStoreException{
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .build();
        for(CasMatch match : createMatchesFor(alignment)){
        	matchVisitor.visitMatch(match);
        }
        assertBuiltGappedReferenceEquals(referenceCalls);       
    }

	private void assertBuiltGappedReferenceEquals(
			NucleotideSequence expectedSequence) throws DataStoreException {
		matchVisitor.visitEnd();
        sut.visitEnd();
        CasGappedReferenceDataStore actual = sut.build();
        assertEquals(expectedSequence,actual.get(referenceName));
        assertEquals(expectedSequence,actual.getReferenceByIndex(referenceId));
	}
    
    @Test(expected = IllegalStateException.class)
    public void dataStoreExceptionShouldThrowIllegalStateException() throws DataStoreException{
    	long invalidReferenceId = referenceId +1;
        CasAlignment alignment = new DefaultCasAlignment.Builder(invalidReferenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .build();
        for(CasMatch match : createMatchesFor(alignment)){
        	matchVisitor.visitMatch(match);
        }
             
    }
    @Test
    public void ifLastAlignmentIsInsertShouldBeIgnored() throws DataStoreException{
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .addRegion(CasAlignmentRegionType.INSERT, 10)
                                                .build();
        for(CasMatch match : createMatchesFor(alignment)){
        	matchVisitor.visitMatch(match);
        }
        assertBuiltGappedReferenceEquals(referenceCalls);       
    }
    @Test
    public void twoReadsNoInsertsShouldNotAddAnyGaps() throws DataStoreException{
        CasAlignment alignment1 = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 10)
                                                .build();
        CasAlignment alignment2 = new DefaultCasAlignment.Builder(referenceId,0,false)
                                            .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 5)
                                            .build();
        for(CasMatch match : createMatchesFor(alignment1, alignment2)){
            matchVisitor.visitMatch(match);
        }
        assertBuiltGappedReferenceEquals(referenceCalls);     
    }
    @Test
    public void oneReadOneInsertShouldAddOneGap() throws DataStoreException{
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
                                                .build();
        for(CasMatch match : createMatchesFor(alignment)){
            matchVisitor.visitMatch(match);
        }
        NucleotideSequence expected = new NucleotideSequenceBuilder("GTTC-AAATTG").build();
        assertBuiltGappedReferenceEquals(expected);      
    }
    @Test
    public void twoReadsOneHasInsertShouldAddOneGap() throws DataStoreException{
        CasAlignment alignment = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
                                                .addRegion(CasAlignmentRegionType.INSERT, 1)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
                                                .build();
        CasAlignment alignment2 = new DefaultCasAlignment.Builder(referenceId,0,false)
                                                .addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 5)
                                                .build();

        for(CasMatch match : createMatchesFor(alignment, alignment2)){
            matchVisitor.visitMatch(match);
        }
        NucleotideSequence expected = new NucleotideSequenceBuilder("GTTC-AAATTG").build();
        assertBuiltGappedReferenceEquals(expected);      
    }
    @Test
    public void twoReadsBothHaveSameInsertShouldAddOneGap() throws DataStoreException{
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
        for(CasMatch match : createMatchesFor(alignment, alignment2)){
            matchVisitor.visitMatch(match);
        }
        NucleotideSequence expected = new NucleotideSequenceBuilder("GTTC-AAATTG").build();
        assertBuiltGappedReferenceEquals(expected);          
    }
    @Test
    public void twoReadsBothHavedifferentInsertShouldAddTwoGaps() throws DataStoreException{
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
        for(CasMatch match : createMatchesFor(alignment, alignment2)){
            matchVisitor.visitMatch(match);
        }
        NucleotideSequence expected = new NucleotideSequenceBuilder("GTTC-AAA-TTG").build();
        assertBuiltGappedReferenceEquals(expected);        
    }
    @Test
    public void twoReadsBothHaveDifferentLengthInsertAtSameLocationShouldAddLongestGap() throws DataStoreException{
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

        for(CasMatch match : createMatchesFor(alignment, alignment2)){
            matchVisitor.visitMatch(match);
        }
        NucleotideSequence expected = new NucleotideSequenceBuilder("GTTC--AAATTG").build();
        assertBuiltGappedReferenceEquals(expected);        
    }
}
