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
 * Created on Mar 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.slice.QualityValueStrategy;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.common.core.util.iter.EmptyIterator;
import org.jcvi.glyph.qualClass.QualityClass;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class TestDefaultContigQualityClassComputerComputeQualityFromRegion {

    byte delta = (byte)1;
    int index = 1234;
    PhredQuality threshold = PhredQuality.valueOf(30);
    PhredQuality lowQuality = PhredQuality.valueOf(29);
    PhredQuality highQuality = PhredQuality.valueOf(31);
    
    
    
    QualityValueStrategy qualityValueStrategy;
    DefaultContigQualityClassComputer  sut;
    CoverageRegion<AssembledRead> coverageRegion;
    QualityDataStore qualityFastaMap;
    Nucleotide consensusBase = Nucleotide.Adenine;
    Nucleotide notConsensusBase = Nucleotide.Thymine;
    
    QualityClass.Builder builder;
    QualityClass expectedQuality = QualityClass.NO_CONFLICT_HIGH_QUAL_BOTH_DIRS;
    @Before
    public void setup(){
        qualityValueStrategy = createMock(QualityValueStrategy.class);
        coverageRegion = createMock(CoverageRegion.class);
        qualityFastaMap = createMock(QualityDataStore.class);
        sut = new DefaultContigQualityClassComputer(qualityValueStrategy, threshold);
        builder = createMock(QualityClass.Builder.class);
        expect(builder.build()).andReturn(expectedQuality);
    }
    
    @Test
    public void zeroCoverageRegion() throws DataStoreException{
        expect(coverageRegion.iterator()).andReturn(EmptyIterator.<AssembledRead>createEmptyIterator());
        replay(qualityFastaMap,coverageRegion,builder);
        assertEquals(expectedQuality,
                sut.computeQualityClassFor(qualityFastaMap, index, coverageRegion, consensusBase, builder));
        verify(qualityFastaMap,coverageRegion,builder);
    }
    @Test
    public void oneReadAtThresholdQualShouldBeConsideredHighQuality() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createThresholdQualityAgreeingRead("read1", Direction.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    

    @Test
    public void oneReadHighQualForwardAgreement() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", Direction.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }

    @Test
    public void oneReadHighQualReverseAgreement() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", Direction.REVERSE,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void twoReadsQualReverseAgreement() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", Direction.REVERSE,reads));
        mocks.addAll(createHighQualityAgreeingRead("read2", Direction.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    
    @Test
    public void oneReadLowQualForwardAgreement() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createLowQualityAgreeingRead("read1", Direction.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }

    @Test
    public void oneReadLowQualReverseAgreement() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createLowQualityAgreeingRead("read1", Direction.REVERSE,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void oneReadLowQualReverseConflict() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createLowQualityConflictingRead("read1", Direction.REVERSE,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void oneReadThresholdQualForwardConflictShouldCountAsHighQuality() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createThresholdQualityConflictingRead("read1", Direction.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void oneReadHighQualForwardConflict() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityConflictingRead("read1", Direction.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    
    @Test
    public void manyReadsWithLowAndHighQualityAgreementsAndConflicts() throws DataStoreException{
        List<AssembledRead> reads = new ArrayList<AssembledRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", Direction.FORWARD,reads));
        mocks.addAll(createHighQualityAgreeingRead("read2", Direction.REVERSE,reads));
        mocks.addAll(createHighQualityConflictingRead("read3", Direction.FORWARD,reads));
        mocks.addAll(createHighQualityConflictingRead("read4", Direction.REVERSE,reads));

        mocks.addAll(createLowQualityAgreeingRead("read1", Direction.FORWARD,reads));
        mocks.addAll(createLowQualityAgreeingRead("read2", Direction.REVERSE,reads));
        mocks.addAll(createLowQualityConflictingRead("read3", Direction.FORWARD,reads));
        mocks.addAll(createLowQualityConflictingRead("read4", Direction.REVERSE,reads));
        
        
        
        assertQualityClassBuiltCorrectly(reads, mocks);
    }

    private void assertQualityClassBuiltCorrectly(List<AssembledRead> reads,
            List<Object> mocks) throws DataStoreException {
        expect(coverageRegion.iterator()).andReturn(reads.iterator());
        replay(mocks.toArray());
        replay(qualityFastaMap,coverageRegion,builder,qualityValueStrategy);
        assertEquals(expectedQuality,
                sut.computeQualityClassFor(qualityFastaMap, index, coverageRegion, consensusBase, builder));
        verify(qualityFastaMap,coverageRegion,builder,qualityValueStrategy);
        verify(mocks.toArray());
    }
    
    
    private List<Object> createHighQualityAgreeingRead(String id,Direction dir,List<AssembledRead> reads) throws DataStoreException {
        return createAgreeingRead(id, dir, reads, highQuality);
    }
    private List<Object> createLowQualityAgreeingRead(String id,Direction dir,List<AssembledRead> reads) throws DataStoreException {
        return createAgreeingRead(id, dir, reads, lowQuality);
    }
    private List<Object> createHighQualityConflictingRead(String id,Direction dir,List<AssembledRead> reads) throws DataStoreException {
        return createConflictingRead(id, dir, reads, highQuality);
    }
    private List<Object> createLowQualityConflictingRead(String id,Direction dir,List<AssembledRead> reads) throws DataStoreException {
        return createConflictingRead(id, dir, reads, lowQuality);
    }
    private List<Object> createThresholdQualityAgreeingRead(
            String id, Direction dir,
            List<AssembledRead> reads) throws DataStoreException {
        return createAgreeingRead(id, dir, reads, threshold);
    }
    private List<Object> createThresholdQualityConflictingRead(
            String id, Direction dir,
            List<AssembledRead> reads) throws DataStoreException {
        return createConflictingRead(id, dir, reads, threshold);
    }
    private List<Object> createAgreeingRead(String id, Direction dir,
            List<AssembledRead> reads, final PhredQuality returnedQuality) throws DataStoreException {
        AssembledRead realRead = createMock(AssembledRead.class);
        ReferenceMappedNucleotideSequence encodedBases = createMock(ReferenceMappedNucleotideSequence.class);
        QualitySequence encodedQualities = createMock(QualitySequence.class);
        expect(realRead.getId()).andReturn(id);
        expect(qualityFastaMap.get(id)).andReturn(encodedQualities);
        expect(realRead.getGappedStartOffset()).andReturn(0L);
        expect(realRead.getNucleotideSequence()).andReturn(encodedBases);
        expect(encodedBases.get(index)).andReturn(consensusBase);  
        expect(qualityValueStrategy.getQualityFor(realRead, encodedQualities, index)).andReturn(returnedQuality);
        expect(realRead.getDirection()).andReturn(dir);
        if(returnedQuality == lowQuality){
            expect(builder.addLowQualityAgreement(dir)).andReturn(builder);
        }
        else{
            expect(builder.addHighQualityAgreement(dir)).andReturn(builder);
        }
        reads.add(realRead);
        return Arrays.asList(realRead, encodedBases, encodedQualities);
    }
    
    private List<Object> createConflictingRead(String id, Direction dir,
            List<AssembledRead> reads, final PhredQuality returnedQuality) throws DataStoreException {
        AssembledRead realRead = createMock(AssembledRead.class);
        ReferenceMappedNucleotideSequence encodedBases = createMock(ReferenceMappedNucleotideSequence.class);
        QualitySequence encodedQualities = createMock(QualitySequence.class);
        expect(realRead.getId()).andReturn(id);
        expect(qualityFastaMap.get(id)).andReturn(encodedQualities);
        expect(realRead.getGappedStartOffset()).andReturn(0L);
        expect(realRead.getNucleotideSequence()).andReturn(encodedBases);
        expect(encodedBases.get(index)).andReturn(notConsensusBase);      
        expect(qualityValueStrategy.getQualityFor(realRead, encodedQualities, index)).andReturn(returnedQuality);
        expect(realRead.getDirection()).andReturn(dir);
        if(returnedQuality == lowQuality){
            expect(builder.addLowQualityConflict(dir)).andReturn(builder);
        }
        else{
            expect(builder.addHighQualityConflict(dir)).andReturn(builder);
        }
        reads.add(realRead);
        return Arrays.asList(realRead, encodedBases, encodedQualities);
    }
}
