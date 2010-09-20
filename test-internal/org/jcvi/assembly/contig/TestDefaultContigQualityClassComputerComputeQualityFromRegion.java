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
import java.util.Collections;
import java.util.List;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.qualClass.QualityClass;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class TestDefaultContigQualityClassComputerComputeQualityFromRegion {

    byte delta = (byte)1;
    int index = 1234;
    PhredQuality threshold = PhredQuality.valueOf((byte)30);
    PhredQuality lowQuality = threshold.decreaseBy(delta);
    PhredQuality highQuality = threshold.increaseBy(delta);
    
    
    QualityValueStrategy qualityValueStrategy;
    DefaultContigQualityClassComputer  sut;
    CoverageRegion<PlacedRead> coverageRegion;
    DataStore<EncodedGlyphs<PhredQuality>> qualityFastaMap;
    NucleotideGlyph consensusBase = NucleotideGlyph.Adenine;
    NucleotideGlyph notConsensusBase = NucleotideGlyph.Thymine;
    
    QualityClass.Builder builder;
    QualityClass expectedQuality = QualityClass.NO_CONFLICT_HIGH_QUAL_BOTH_DIRS;
    @Before
    public void setup(){
        qualityValueStrategy = createMock(QualityValueStrategy.class);
        coverageRegion = createMock(CoverageRegion.class);
        qualityFastaMap = createMock(DataStore.class);
        sut = new DefaultContigQualityClassComputer(qualityValueStrategy, threshold);
        builder = createMock(QualityClass.Builder.class);
        expect(builder.build()).andReturn(expectedQuality);
    }
    
    @Test
    public void zeroCoverageRegion() throws DataStoreException{
        expect(coverageRegion.getElements()).andReturn(Collections.<PlacedRead>emptyList());
        replay(qualityFastaMap,coverageRegion,builder);
        assertEquals(expectedQuality,
                sut.computeQualityClassFor(qualityFastaMap, index, coverageRegion, consensusBase, builder));
        verify(qualityFastaMap,coverageRegion,builder);
    }
    @Test
    public void oneReadAtThresholdQualShouldBeConsideredHighQuality() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createThresholdQualityAgreeingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    

    @Test
    public void oneReadHighQualForwardAgreement() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }

    @Test
    public void oneReadHighQualReverseAgreement() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", SequenceDirection.REVERSE,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void twoReadsQualReverseAgreement() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", SequenceDirection.REVERSE,reads));
        mocks.addAll(createHighQualityAgreeingRead("read2", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    
    @Test
    public void oneReadLowQualForwardAgreement() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createLowQualityAgreeingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }

    @Test
    public void oneReadLowQualReverseAgreement() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createLowQualityAgreeingRead("read1", SequenceDirection.REVERSE,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void oneReadLowQualReverseConflict() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createLowQualityConflictingRead("read1", SequenceDirection.REVERSE,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void oneReadThresholdQualForwardConflictShouldCountAsHighQuality() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createThresholdQualityConflictingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void oneReadHighQualForwardConflict() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityConflictingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    
    @Test
    public void manyReadsWithLowAndHighQualityAgreementsAndConflicts() throws DataStoreException{
        List<PlacedRead> reads = new ArrayList<PlacedRead>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", SequenceDirection.FORWARD,reads));
        mocks.addAll(createHighQualityAgreeingRead("read2", SequenceDirection.REVERSE,reads));
        mocks.addAll(createHighQualityConflictingRead("read3", SequenceDirection.FORWARD,reads));
        mocks.addAll(createHighQualityConflictingRead("read4", SequenceDirection.REVERSE,reads));

        mocks.addAll(createLowQualityAgreeingRead("read1", SequenceDirection.FORWARD,reads));
        mocks.addAll(createLowQualityAgreeingRead("read2", SequenceDirection.REVERSE,reads));
        mocks.addAll(createLowQualityConflictingRead("read3", SequenceDirection.FORWARD,reads));
        mocks.addAll(createLowQualityConflictingRead("read4", SequenceDirection.REVERSE,reads));
        
        
        
        assertQualityClassBuiltCorrectly(reads, mocks);
    }

    private void assertQualityClassBuiltCorrectly(List<PlacedRead> reads,
            List<Object> mocks) throws DataStoreException {
        expect(coverageRegion.getElements()).andReturn(reads);
        replay(mocks.toArray());
        replay(qualityFastaMap,coverageRegion,builder,qualityValueStrategy);
        assertEquals(expectedQuality,
                sut.computeQualityClassFor(qualityFastaMap, index, coverageRegion, consensusBase, builder));
        verify(qualityFastaMap,coverageRegion,builder,qualityValueStrategy);
        verify(mocks.toArray());
    }
    
    
    private List<Object> createHighQualityAgreeingRead(String id,SequenceDirection dir,List<PlacedRead> reads) throws DataStoreException {
        return createAgreeingRead(id, dir, reads, highQuality);
    }
    private List<Object> createLowQualityAgreeingRead(String id,SequenceDirection dir,List<PlacedRead> reads) throws DataStoreException {
        return createAgreeingRead(id, dir, reads, lowQuality);
    }
    private List<Object> createHighQualityConflictingRead(String id,SequenceDirection dir,List<PlacedRead> reads) throws DataStoreException {
        return createConflictingRead(id, dir, reads, highQuality);
    }
    private List<Object> createLowQualityConflictingRead(String id,SequenceDirection dir,List<PlacedRead> reads) throws DataStoreException {
        return createConflictingRead(id, dir, reads, lowQuality);
    }
    private List<Object> createThresholdQualityAgreeingRead(
            String id, SequenceDirection dir,
            List<PlacedRead> reads) throws DataStoreException {
        return createAgreeingRead(id, dir, reads, threshold);
    }
    private List<Object> createThresholdQualityConflictingRead(
            String id, SequenceDirection dir,
            List<PlacedRead> reads) throws DataStoreException {
        return createConflictingRead(id, dir, reads, threshold);
    }
    private List<Object> createAgreeingRead(String id, SequenceDirection dir,
            List<PlacedRead> reads, final PhredQuality returnedQuality) throws DataStoreException {
        PlacedRead realRead = createMock(PlacedRead.class);
        NucleotideEncodedGlyphs encodedBases = createMock(NucleotideEncodedGlyphs.class);
        EncodedGlyphs<PhredQuality> encodedQualities = createMock(EncodedGlyphs.class);
        expect(realRead.getId()).andReturn(id);
        expect(qualityFastaMap.get(id)).andReturn(encodedQualities);
        expect(realRead.getStart()).andReturn(0L);
        expect(realRead.getEncodedGlyphs()).andReturn(encodedBases);
        expect(encodedBases.get(index)).andReturn(consensusBase);  
        expect(qualityValueStrategy.getQualityFor(realRead, encodedQualities, index)).andReturn(returnedQuality);
        expect(realRead.getSequenceDirection()).andReturn(dir);
        if(returnedQuality == lowQuality){
            expect(builder.addLowQualityAgreement(dir)).andReturn(builder);
        }
        else{
            expect(builder.addHighQualityAgreement(dir)).andReturn(builder);
        }
        reads.add(realRead);
        return Arrays.asList(realRead, encodedBases, encodedQualities);
    }
    
    private List<Object> createConflictingRead(String id, SequenceDirection dir,
            List<PlacedRead> reads, final PhredQuality returnedQuality) throws DataStoreException {
        PlacedRead realRead = createMock(PlacedRead.class);
        NucleotideEncodedGlyphs encodedBases = createMock(NucleotideEncodedGlyphs.class);
        EncodedGlyphs<PhredQuality> encodedQualities = createMock(EncodedGlyphs.class);
        expect(realRead.getId()).andReturn(id);
        expect(qualityFastaMap.get(id)).andReturn(encodedQualities);
        expect(realRead.getStart()).andReturn(0L);
        expect(realRead.getEncodedGlyphs()).andReturn(encodedBases);
        expect(encodedBases.get(index)).andReturn(notConsensusBase);      
        expect(qualityValueStrategy.getQualityFor(realRead, encodedQualities, index)).andReturn(returnedQuality);
        expect(realRead.getSequenceDirection()).andReturn(dir);
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
