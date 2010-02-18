/*
 * Created on Jul 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestFakeTigrSeqnameMatedTraceIdGeneratorcomputeTigrSeqnamePrefix {

    private static class TestDouble extends FakeTigrSeqnameMatedTraceIdGenerator{
        private final int wellCounterOffset;
        public TestDouble(String libraryPrefix, List<String> excludedLibraries, int wellCounterOffset) {
            super(libraryPrefix, excludedLibraries);
            this.wellCounterOffset = wellCounterOffset;
        }
        @Override
        public int getWellCounter() {
            return wellCounterOffset + super.getWellCounter();
        }
    }
    private String libraryPrefix = "M2";
    
    @Test
    public void computeTigrPrefixFirstRead(){
        TestDouble sut = new TestDouble(libraryPrefix+"00", Collections.<String>emptyList(), 0);
        String expectedPrefix = libraryPrefix+"00"+"A00";
        assertEquals(expectedPrefix, sut.computeTigrSeqnamePrefix());
    }
    @Test
    public void computeTigrPrefixDoNotStartAt00(){
        TestDouble sut = new TestDouble(libraryPrefix+"DK", Collections.<String>emptyList(), 0);
        String expectedPrefix = libraryPrefix+"DK"+"A00";
        assertEquals(expectedPrefix, sut.computeTigrSeqnamePrefix());
    }
    @Test
    public void computeTigrPrefixMiddleOfFirstRow(){
        TestDouble sut = new TestDouble(libraryPrefix+"00", Collections.<String>emptyList(), 50);
        String expectedPrefix = libraryPrefix+"00"+"A50";
        assertEquals(expectedPrefix, sut.computeTigrSeqnamePrefix());
    }

    @Test
    public void computeTigrPrefixMiddleOfColumnRow(){
        TestDouble sut = new TestDouble(libraryPrefix+"00", Collections.<String>emptyList(), 550);
        String expectedPrefix = libraryPrefix+"00"+"F50";
        assertEquals(expectedPrefix, sut.computeTigrSeqnamePrefix());
    }
    @Test
    public void libraryExcludedShouldIncrementCounterby100(){
        TestDouble sut = new TestDouble(libraryPrefix+"00", Arrays.asList("00"), 0);
        assertEquals(libraryPrefix+"01"+"A00", sut.computeTigrSeqnamePrefix());
    }
    
    @Test
    public void generateIds(){
        TestDouble sut = new TestDouble(libraryPrefix+"00", Collections.<String>emptyList(), 550);
        String expectedPrefix = libraryPrefix+"00"+"F50";
        assertEquals(Arrays.asList(expectedPrefix+"TF"), sut.generateIdsAndIncrementCounter(false));
    }
    @Test
    public void generateIdsWithReverse(){
        TestDouble sut = new TestDouble(libraryPrefix+"00", Collections.<String>emptyList(), 550);
        String expectedPrefix = libraryPrefix+"00"+"F50";
        final List<String> expectedIds = Arrays.asList(expectedPrefix+"TF",
                                                        expectedPrefix+"TR");
        assertEquals(expectedIds, sut.generateIdsAndIncrementCounter(true));
    }
    @Test
    public void generateIdsMultiple(){
        TestDouble sut = new TestDouble(libraryPrefix+"00", Collections.<String>emptyList(), 550);
        assertEquals(Arrays.asList(libraryPrefix+"00"+"F50"+"TF"), sut.generateIdsAndIncrementCounter(false));
        assertEquals(Arrays.asList(libraryPrefix+"00"+"F51"+"TF"), sut.generateIdsAndIncrementCounter(false));
        assertEquals(Arrays.asList(libraryPrefix+"00"+"F52"+"TF"), sut.generateIdsAndIncrementCounter(false));
    }
    @Test
    public void generateIdsMultipleExclude(){
        TestDouble sut = new TestDouble(libraryPrefix+"00", Arrays.asList("00"), 550);
        assertEquals(Arrays.asList(libraryPrefix+"01"+"F50"+"TF"), sut.generateIdsAndIncrementCounter(false));
        assertEquals(Arrays.asList(libraryPrefix+"01"+"F51"+"TF",
                                libraryPrefix+"01"+"F51"+"TR"), sut.generateIdsAndIncrementCounter(true));
        assertEquals(Arrays.asList(libraryPrefix+"01"+"F52"+"TF"), sut.generateIdsAndIncrementCounter(false));
    }
}
