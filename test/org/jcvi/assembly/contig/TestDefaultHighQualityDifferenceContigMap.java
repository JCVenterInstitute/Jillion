/*
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.Range;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultLocation;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultHighQualityDifferenceContigMap {

    Contig contig;
    NucleotideEncodedGlyphs contigConsensus ;
    QualityDataStore qualityFastaMap;
    PlacedRead read1;
    String read1_id = "read1_id";
    EncodedGlyphs<PhredQuality> read1_Qualities ;
    NucleotideEncodedGlyphs read1_EncodedGlyphs;
    PhredQuality qualityThreshold = PhredQuality.valueOf((byte)30);
    PhredQuality highQuality = PhredQuality.valueOf((byte)(31));
    PhredQuality lowQuality = PhredQuality.valueOf((byte)(29));
    QualityValueStrategy qualValueStrategy;
    Range validRange = Range.buildRange(0, 100);
    int read1_startIndex=0;
    Map<Integer, NucleotideGlyph> snpMap;
    @Before
    public void setup(){
        contig = createMock(Contig.class);
        contigConsensus = createMock(NucleotideEncodedGlyphs.class);
        expect(contig.getConsensus()).andStubReturn(contigConsensus);
        qualityFastaMap = createMock(QualityDataStore.class);
        read1 = createMock(PlacedRead.class);
        expect(read1.getId()).andStubReturn(read1_id);
        expect(read1.getValidRange()).andStubReturn(validRange);
        snpMap = new HashMap<Integer, NucleotideGlyph>();
        read1_Qualities = createMock(EncodedGlyphs.class);
        read1_EncodedGlyphs = createMock(NucleotideEncodedGlyphs.class);
        qualValueStrategy = createMock(QualityValueStrategy.class);
        expect(read1.getEncodedGlyphs()).andReturn(read1_EncodedGlyphs).anyTimes();
        expect(read1_EncodedGlyphs.getGapIndexes()).andReturn(Collections.<Integer>emptyList()).anyTimes();
        expect(read1.getStart()).andStubReturn((long)read1_startIndex);
        expect(read1.getSequenceDirection()).andStubReturn(SequenceDirection.FORWARD);
    }
    
    @Test
    public void emptyContigShouldCreateEmptyMap() throws DataStoreException{
        expect(contig.getPlacedReads()).andReturn(Collections.<PlacedRead>emptySet());
        replay(contig,qualityFastaMap);
        assertHighQualityDifferenceContigMapIsEmpty();
        verify(contig,qualityFastaMap);
    }
    @Test
    public void oneReadNoSnpsNoHighQualityDiffs() throws DataStoreException{
        Set<PlacedRead> readsInContig = new HashSet<PlacedRead>();
        readsInContig.add(read1);
        expect(contig.getPlacedReads()).andReturn(readsInContig);
        expect(qualityFastaMap.get(read1_id)).andReturn(read1_Qualities);
        expect(read1.getSnps()).andReturn(Collections.<Integer, NucleotideGlyph>emptyMap());
        
        replay(contig,qualityFastaMap,read1,qualValueStrategy);
        assertHighQualityDifferenceContigMapIsEmpty();
        verify(contig,qualityFastaMap,read1,qualValueStrategy);
    }

    private void assertHighQualityDifferenceContigMapIsEmpty() throws DataStoreException {
        DefaultHighQualityDifferencesContigMap map=new DefaultHighQualityDifferencesContigMap(contig, qualityFastaMap,qualValueStrategy,qualityThreshold);
        assertEquals(0, map.getNumberOfReadsWithHighQualityDifferences());
        assertEquals(qualityThreshold, map.getQualityThreshold());
        assertTrue(map.entrySet().isEmpty());
        assertFalse(map.iterator().hasNext());
        assertTrue(map.getHighQualityDifferencesFor(read1).isEmpty());
    }
    
    @Test
    public void oneReadOneSnpNoHighQualityDiff() throws DataStoreException{
        
        Set<PlacedRead> readsInContig = new HashSet<PlacedRead>();
        readsInContig.add(read1);
        expect(contig.getPlacedReads()).andReturn(readsInContig);
        expect(qualityFastaMap.get(read1_id)).andReturn(read1_Qualities);
        
        expect(read1.getSnps()).andReturn(snpMap);
        createLowQualitySnpAt(30, read1_Qualities, read1);
        
        
        replay(contig,qualityFastaMap,read1,read1_EncodedGlyphs,read1_Qualities,qualValueStrategy);
        assertHighQualityDifferenceContigMapIsEmpty();
        verify(contig,qualityFastaMap,read1,read1_EncodedGlyphs,read1_Qualities,qualValueStrategy);
    }

    private void createLowQualitySnpAt(int index,
            EncodedGlyphs<PhredQuality> mockQualities,
            PlacedRead placedRead) {
        snpMap.put(Integer.valueOf(index), NucleotideGlyph.Adenine);
        expect(qualValueStrategy.getQualityFor(placedRead, mockQualities, index)).andReturn(lowQuality);
    }
    
    @Test
    public void oneReadOneSnpOneHighQualityDiff() throws DataStoreException{
        
        Set<PlacedRead> readsInContig = new HashSet<PlacedRead>();
        readsInContig.add(read1);
        expect(contig.getPlacedReads()).andReturn(readsInContig);
        expect(qualityFastaMap.get(read1_id)).andReturn(read1_Qualities);
        
        expect(read1.getSnps()).andReturn(snpMap);
        
        DefaultQualityDifference qualityDifference =createHighQualitySnpAt(30, read1_startIndex,read1,read1_Qualities, snpMap);
        
        
        replay(contig,qualityFastaMap,read1,read1_EncodedGlyphs,read1_Qualities,qualValueStrategy);
        Map<PlacedRead, List<DefaultQualityDifference>> expectedMap = new HashMap<PlacedRead, List<DefaultQualityDifference>>();
        expectedMap.put(read1, Arrays.asList(qualityDifference));
        assertHighQualityDifferenceContigMapMatches(expectedMap);
        verify(contig,qualityFastaMap,read1,read1_EncodedGlyphs,read1_Qualities,qualValueStrategy);
    }
    @Test
    public void oneReadTwoSnpsOneLowOneHighQualityDiff() throws DataStoreException{
        
        Set<PlacedRead> readsInContig = new HashSet<PlacedRead>();
        readsInContig.add(read1);
        expect(contig.getPlacedReads()).andReturn(readsInContig);
        expect(qualityFastaMap.get(read1_id)).andReturn(read1_Qualities);
        
        expect(read1.getSnps()).andReturn(snpMap);
        createLowQualitySnpAt(20, read1_Qualities, read1);
        DefaultQualityDifference qualityDifference =createHighQualitySnpAt(30, read1_startIndex,read1,read1_Qualities, snpMap);
        
        
        replay(contig,qualityFastaMap,read1,read1_EncodedGlyphs,read1_Qualities,qualValueStrategy);
        Map<PlacedRead, List<DefaultQualityDifference>> expectedMap = new HashMap<PlacedRead, List<DefaultQualityDifference>>();
        expectedMap.put(read1, Arrays.asList(qualityDifference));
        assertHighQualityDifferenceContigMapMatches(expectedMap);
        verify(contig,qualityFastaMap,read1,read1_EncodedGlyphs,read1_Qualities,qualValueStrategy);
    }
    @Test
    public void oneReadTwoSnpsBothHighQualityDiff() throws DataStoreException{
        
        Set<PlacedRead> readsInContig = new HashSet<PlacedRead>();
        readsInContig.add(read1);
        expect(contig.getPlacedReads()).andReturn(readsInContig);
        expect(qualityFastaMap.get(read1_id)).andReturn(read1_Qualities);
        
        expect(read1.getSnps()).andReturn(snpMap);
        DefaultQualityDifference qualityDifference1 =createHighQualitySnpAt(30, read1_startIndex,read1,read1_Qualities, snpMap);
        DefaultQualityDifference qualityDifference2 =createHighQualitySnpAt(32, read1_startIndex,read1,read1_Qualities, snpMap);
        
        
        replay(contig,qualityFastaMap,read1,read1_EncodedGlyphs,read1_Qualities,qualValueStrategy);
        Map<PlacedRead, List<DefaultQualityDifference>> expectedMap = new HashMap<PlacedRead, List<DefaultQualityDifference>>();
        expectedMap.put(read1, Arrays.asList(qualityDifference2,qualityDifference1));
        assertHighQualityDifferenceContigMapMatches(expectedMap);
        verify(contig,qualityFastaMap,read1,read1_EncodedGlyphs,read1_Qualities,qualValueStrategy);
    }
    
    private void assertHighQualityDifferenceContigMapMatches(Map<PlacedRead, List<DefaultQualityDifference>> expectedMap) throws DataStoreException{
        DefaultHighQualityDifferencesContigMap actualMap=new DefaultHighQualityDifferencesContigMap(contig, qualityFastaMap,qualValueStrategy,qualityThreshold);
        assertEquals(expectedMap.size(), actualMap.getNumberOfReadsWithHighQualityDifferences());
        assertEquals(qualityThreshold, actualMap.getQualityThreshold());
        for(Entry<PlacedRead, List<DefaultQualityDifference>> expectedEntrySet : expectedMap.entrySet()){
            PlacedRead read = expectedEntrySet.getKey();
            List<DefaultQualityDifference> expectedList = expectedEntrySet.getValue();
            List<DefaultQualityDifference> actualList =actualMap.getHighQualityDifferencesFor(expectedEntrySet.getKey());
            assertEquals("high quality differences do not match for " +read,
                        expectedList, actualList);            
        }
    }
    
    private DefaultQualityDifference createHighQualitySnpAt(int readIndex,int readStartIndex,PlacedRead read,
            EncodedGlyphs<PhredQuality> mockQualities, Map<Integer, NucleotideGlyph> snpMap) {
        snpMap.put(Integer.valueOf(readIndex), NucleotideGlyph.Adenine);
        expect(qualValueStrategy.getQualityFor(read, mockQualities, readIndex)).andReturn(highQuality);
        
        return new DefaultQualityDifference( 
                new DefaultLocation<EncodedGlyphs<NucleotideGlyph>>(contigConsensus, readIndex+readStartIndex),
                new DefaultLocation<PlacedRead>( read, readIndex),
                highQuality);
    }
    
}
