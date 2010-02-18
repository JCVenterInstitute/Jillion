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
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.QualityFastaRecord;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.qualClass.QualityClass;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

public class TestDefaultContigQualityClassComputerComputeQualityFromRegion {

    byte delta = (byte)1;
    int index = 1234;
    PhredQuality threshold = PhredQuality.valueOf((byte)30);
    PhredQuality lowQuality = threshold.decreaseBy(delta);
    PhredQuality highQuality = threshold.increaseBy(delta);
    
    
    QualityValueStrategy qualityValueStrategy;
    DefaultContigQualityClassComputer  sut;
    CoverageRegion<VirtualPlacedRead<PlacedRead>> coverageRegion;
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
        expect(coverageRegion.getElements()).andReturn(Collections.<VirtualPlacedRead<PlacedRead>>emptyList());
        replay(qualityFastaMap,coverageRegion,builder);
        assertEquals(expectedQuality,
                sut.computeQualityClassFor(qualityFastaMap, index, coverageRegion, consensusBase, builder));
        verify(qualityFastaMap,coverageRegion,builder);
    }
    @Test
    public void oneReadAtThresholdQualShouldBeConsideredHighQuality() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createThresholdQualityAgreeingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    

    @Test
    public void oneReadHighQualForwardAgreement() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }

    @Test
    public void oneReadHighQualReverseAgreement() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", SequenceDirection.REVERSE,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void twoReadsQualReverseAgreement() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityAgreeingRead("read1", SequenceDirection.REVERSE,reads));
        mocks.addAll(createHighQualityAgreeingRead("read2", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    
    @Test
    public void oneReadLowQualForwardAgreement() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createLowQualityAgreeingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }

    @Test
    public void oneReadLowQualReverseAgreement() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createLowQualityAgreeingRead("read1", SequenceDirection.REVERSE,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void oneReadLowQualReverseConflict() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createLowQualityConflictingRead("read1", SequenceDirection.REVERSE,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void oneReadThresholdQualForwardConflictShouldCountAsHighQuality() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createThresholdQualityConflictingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    @Test
    public void oneReadHighQualForwardConflict() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
        List<Object> mocks = new ArrayList<Object>();
        mocks.addAll(createHighQualityConflictingRead("read1", SequenceDirection.FORWARD,reads));
        assertQualityClassBuiltCorrectly(reads, mocks);
    }
    
    @Test
    public void manyReadsWithLowAndHighQualityAgreementsAndConflicts() throws DataStoreException{
        List<VirtualPlacedRead<PlacedRead>> reads = new ArrayList<VirtualPlacedRead<PlacedRead>>();
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

    private void assertQualityClassBuiltCorrectly(List<VirtualPlacedRead<PlacedRead>> reads,
            List<Object> mocks) throws DataStoreException {
        expect(coverageRegion.getElements()).andReturn(reads);
        replay(mocks.toArray());
        replay(qualityFastaMap,coverageRegion,builder,qualityValueStrategy);
        assertEquals(expectedQuality,
                sut.computeQualityClassFor(qualityFastaMap, index, coverageRegion, consensusBase, builder));
        verify(qualityFastaMap,coverageRegion,builder,qualityValueStrategy);
        verify(mocks.toArray());
    }
    
    
    private List<Object> createHighQualityAgreeingRead(String id,SequenceDirection dir,List<VirtualPlacedRead<PlacedRead>> reads) throws DataStoreException {
        return createAgreeingRead(id, dir, reads, highQuality);
    }
    private List<Object> createLowQualityAgreeingRead(String id,SequenceDirection dir,List<VirtualPlacedRead<PlacedRead>> reads) throws DataStoreException {
        return createAgreeingRead(id, dir, reads, lowQuality);
    }
    private List<Object> createHighQualityConflictingRead(String id,SequenceDirection dir,List<VirtualPlacedRead<PlacedRead>> reads) throws DataStoreException {
        return createConflictingRead(id, dir, reads, highQuality);
    }
    private List<Object> createLowQualityConflictingRead(String id,SequenceDirection dir,List<VirtualPlacedRead<PlacedRead>> reads) throws DataStoreException {
        return createConflictingRead(id, dir, reads, lowQuality);
    }
    private List<Object> createThresholdQualityAgreeingRead(
            String id, SequenceDirection dir,
            List<VirtualPlacedRead<PlacedRead>> reads) throws DataStoreException {
        return createAgreeingRead(id, dir, reads, threshold);
    }
    private List<Object> createThresholdQualityConflictingRead(
            String id, SequenceDirection dir,
            List<VirtualPlacedRead<PlacedRead>> reads) throws DataStoreException {
        return createConflictingRead(id, dir, reads, threshold);
    }
    private List<Object> createAgreeingRead(String id, SequenceDirection dir,
            List<VirtualPlacedRead<PlacedRead>> reads, final PhredQuality returnedQuality) throws DataStoreException {
        VirtualPlacedRead<PlacedRead> virtualRead = createMock(VirtualPlacedRead.class);
        PlacedRead realRead = createMock(PlacedRead.class);
        NucleotideEncodedGlyphs encodedBases = createMock(NucleotideEncodedGlyphs.class);
        EncodedGlyphs<PhredQuality> encodedQualities = createMock(EncodedGlyphs.class);
        expect(realRead.getId()).andReturn(id);
        expect(virtualRead.getRealPlacedRead()).andReturn(realRead);
        expect(qualityFastaMap.get(id)).andReturn(encodedQualities);
        expect(virtualRead.getStart()).andReturn(0L);
        expect(virtualRead.getEncodedGlyphs()).andReturn(encodedBases);
        final int realIndex = index+15;
        expect(virtualRead.getRealIndexOf(index)).andReturn(realIndex);
        expect(encodedBases.get(index)).andReturn(consensusBase);  
        expect(qualityValueStrategy.getQualityFor(realRead, encodedQualities, realIndex)).andReturn(returnedQuality);
        expect(virtualRead.getSequenceDirection()).andReturn(dir);
        if(returnedQuality == lowQuality){
            expect(builder.addLowQualityAgreement(dir)).andReturn(builder);
        }
        else{
            expect(builder.addHighQualityAgreement(dir)).andReturn(builder);
        }
        reads.add(virtualRead);
        return Arrays.asList(virtualRead, encodedBases, encodedQualities,realRead);
    }
    
    private List<Object> createConflictingRead(String id, SequenceDirection dir,
            List<VirtualPlacedRead<PlacedRead>> reads, final PhredQuality returnedQuality) throws DataStoreException {
        VirtualPlacedRead<PlacedRead> virtualRead = createMock(VirtualPlacedRead.class);
        PlacedRead realRead = createMock(PlacedRead.class);
        expect(virtualRead.getRealPlacedRead()).andReturn(realRead);
        NucleotideEncodedGlyphs encodedBases = createMock(NucleotideEncodedGlyphs.class);
        EncodedGlyphs<PhredQuality> encodedQualities = createMock(EncodedGlyphs.class);
        expect(realRead.getId()).andReturn(id);
        expect(qualityFastaMap.get(id)).andReturn(encodedQualities);
        expect(virtualRead.getStart()).andReturn(0L);
        expect(virtualRead.getEncodedGlyphs()).andReturn(encodedBases);
        final int realIndex = index+15;
        expect(virtualRead.getRealIndexOf(index)).andReturn(realIndex);
        expect(encodedBases.get(index)).andReturn(notConsensusBase);      
        expect(qualityValueStrategy.getQualityFor(realRead, encodedQualities, realIndex)).andReturn(returnedQuality);
        expect(virtualRead.getSequenceDirection()).andReturn(dir);
        if(returnedQuality == lowQuality){
            expect(builder.addLowQualityConflict(dir)).andReturn(builder);
        }
        else{
            expect(builder.addHighQualityConflict(dir)).andReturn(builder);
        }
        reads.add(virtualRead);
        return Arrays.asList(virtualRead, encodedBases, encodedQualities,realRead);
    }
}
