package org.jcvi.jillion.core.qual;

import org.jcvi.jillion.core.Range;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.DoubleSummaryStatistics;
public class TestQualityStatisticsSummary {

    private static final double DELTA = 0.00001D;
    @Test
    public void emptySequenceReturnsEmptyOptional(){
        assertFalse( new QualitySequenceBuilder().build().getSummaryStats().isPresent());
    }
    
    @Test
    public void oneQuality(){
        DoubleSummaryStatistics stats = new QualitySequenceBuilder().append(40).build().getSummaryStats().get();
        
        assertEquals(40, stats.getAverage(), DELTA);
        assertEquals(40, stats.getMax(), DELTA);
        assertEquals(40, stats.getMin(), DELTA);
    }
    
    @Test
    public void lotsOfQualities(){
        DoubleSummaryStatistics stats = new QualitySequenceBuilder(new byte[]{10,20,30,40,50,60,70,80})
                                                .build().getSummaryStats().get();
        
        assertEquals(45, stats.getAverage(), DELTA);
        assertEquals(80, stats.getMax(), DELTA);
        assertEquals(10, stats.getMin(), DELTA);
    }
    
    @Test
    public void subRange(){
        DoubleSummaryStatistics stats = new QualitySequenceBuilder(new byte[]{10,10,20,30,40,50,60,70,80,0})
                                                .build()
                                                .getSummaryStats(Range.of(1,8)).get();
        
        
        assertEquals(45, stats.getAverage(), DELTA);
        assertEquals(80, stats.getMax(), DELTA);
        assertEquals(10, stats.getMin(), DELTA);
    }
}
