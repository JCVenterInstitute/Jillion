/*
 * Created on Apr 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.slice.ContigSlice;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.assembly.slice.SliceLocation;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultBasecallCount {

    Slice mockSlice;
    Map<NucleotideGlyph, Integer> expectedMap;
    @Before
    public void setup(){
        mockSlice = createMock(Slice.class);
        expectedMap = createZeroedMap();
    }
    
    private SliceElement createLocationWithBase(NucleotideGlyph base){
        SliceElement location = createMock(SliceElement.class);
        expect(location.getBase()).andReturn(base);
        replay(location);
        return location;
    }
    
    private List<SliceElement> createLocations(Map<NucleotideGlyph, Integer> map){
        List<SliceElement> locations = new ArrayList<SliceElement>();
        for(Entry<NucleotideGlyph, Integer> entry : map.entrySet()){
            int count = entry.getValue();
            for(int i=0; i<count; i++){
                locations.add(createLocationWithBase(entry.getKey()));
            }
        }
        
        return locations;
    }
    
    @Test
    public void emptySliceShouldMapWithAllZeroes(){
        expect(mockSlice.iterator()).andReturn(Collections.<SliceElement>emptyList().iterator());
        replay(mockSlice);
        BasecallCountHistogram sut = new DefaultBasecallCountHistogram(mockSlice);
        assertEquals(expectedMap,sut.getHistogram());
        
    }

    private Map<NucleotideGlyph, Integer> createZeroedMap() {
        Map<NucleotideGlyph, Integer> map = new EnumMap<NucleotideGlyph, Integer>(NucleotideGlyph.class);
        for(NucleotideGlyph glyph : NucleotideGlyph.values()){
            map.put(glyph, Integer.valueOf(0));
        }
        return map;
    }
    
    @Test
    public void oneRead(){
        expectedMap.put(NucleotideGlyph.Adenine, 1);
        assertCorrectHistogramCreated();
    }
    
    @Test
    public void manyReadsSameBase(){
        expectedMap.put(NucleotideGlyph.Adenine, 5);
        assertCorrectHistogramCreated();
    }
    
    @Test
    public void twoDifferentBases(){
        
        expectedMap.put(NucleotideGlyph.Adenine, 5);
        expectedMap.put(NucleotideGlyph.Guanine, 4);
        assertCorrectHistogramCreated();
    }
    
    @Test
    public void someOfEverything(){
        Random rand = new Random();
        for(NucleotideGlyph glyph : NucleotideGlyph.values()){
            expectedMap.put(glyph, rand.nextInt(10)+1);
        }
        assertCorrectHistogramCreated();
    }

    private void assertCorrectHistogramCreated() {
        List<SliceElement> slices =createLocations(expectedMap);
        expect(mockSlice.iterator()).andReturn(slices.iterator());
        replay(mockSlice);
        
        BasecallCountHistogram sut = new DefaultBasecallCountHistogram(mockSlice);
        assertEquals(mockSlice, sut.getContigSlice());
        assertEquals(expectedMap,sut.getHistogram());
        verify(mockSlice);
    }
}
