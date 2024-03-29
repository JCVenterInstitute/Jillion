/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestSliceMap {

    protected abstract SliceMap createSliceMapFor(Contig<AssembledRead> contig, QualitySequenceDataStore qualityDatastore, GapQualityValueStrategy qualityValueStrategy);
    private QualitySequenceDataStore qualityDataStore;
    @Before
    public void setup(){
        Map<String, QualitySequence> qualities = new HashMap<String, QualitySequence>();
        qualities.put("read_0", new QualitySequenceBuilder(new byte[]{10,12,14,16,18,20,22,24}).build());
        qualities.put("read_1", new QualitySequenceBuilder(new byte[]{1,2,3,4,5,6,7,8}).build());
        qualities.put("read_2", new QualitySequenceBuilder(new byte[]{15,16,17,18}).build());
        qualityDataStore = DataStore.of(qualities, QualitySequenceDataStore.class);
    }
    @Test
    public void allSlicesSameDepth(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "ACGTACGT")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategy.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("A","AA", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C","CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("G","GG", 14,3),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("T","TT", 16,4),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("A","AA", 18,5),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C","CC", 20,6),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("G","GG", 22,7),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("T","TT", 24,8),
                sut.getSlice(7));
    }
    @Test
    public void multipleDepthSlices(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "ACGTACGT")
                                    .addRead("read_2", 2,   "GTAC")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategy.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("A","AA", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C","CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("G","GGG", 14,3,15),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("T","TTT", 16,4,16),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("A","AAA", 18,5,17),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C","CCC", 20,6,18),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("G","GG", 22,7),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("T","TT", 24,8),
                sut.getSlice(7));
    }
    
    @Test
    public void multipleBasecallsPerSlice(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "RCGTACGT")
                                    .addRead("read_2", 2,   "GWAC")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategy.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("A","AR", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C","CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("G","GGG", 14,3,15),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("T","TTW", 16,4,16),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("A","AAA", 18,5,17),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C","CCC", 20,6,18),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("G","GG", 22,7),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("T","TT", 24,8),
                sut.getSlice(7));
    }
    
    @Test
    public void gapsInSliceShouldUseLowestFlankingQualityValues(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "RCGTA-GT")
                                    .addRead("read_2", 2,   "G-AC")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategy.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("A","AR", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C","CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("G","GGG", 14,3,15),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("T","TT-", 16,4,15),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("A","AAA", 18,5,16),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C","C-C", 20,5,17),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("G","GG", 22,6),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("T","TT", 24,7),
                sut.getSlice(7));
    }
    
    @Test
    public void sameSliceMapIsEqualToItself(){
    	Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
        .addRead("read_0", 0, "ACGTACGT")
        .addRead("read_1", 0, "RCGTA-GT")
        .addRead("read_2", 2,   "G-AC")
        .build();
    	
    	SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategy.LOWEST_FLANKING);
        
    	assertTrue(sut.equals(sut));
    }
    
    @Test
    public void sameSliceMapIsEqualToSameValues(){
    	Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
        .addRead("read_0", 0, "ACGTACGT")
        .addRead("read_1", 0, "RCGTA-GT")
        .addRead("read_2", 2,   "G-AC")
        .build();
    	
    	SliceMap sut1 = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategy.LOWEST_FLANKING);
    	SliceMap sut2 = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategy.LOWEST_FLANKING);
        
    	TestUtil.assertEqualAndHashcodeSame(sut1, sut2);
    	
    }
    
    @Test
    public void differentSliceMapsNotEqual(){
    	Contig<AssembledRead> contig1 = new DefaultContig.Builder("contigId", "ACGTACGT")
							        .addRead("read_0", 0, "ACGTACGT")
							        .addRead("read_1", 0, "RCGTA-GT")
							        .addRead("read_2", 2,   "G-AC")
							        .build();
    	
    	Contig<AssembledRead> contig2 = new DefaultContig.Builder("contigId", "ACGTACGT")
							        .addRead("read_0", 0, "ACGTACGT")
							        .addRead("read_1", 0, "RCGTA-GT")
							        .build();
    	
    	SliceMap sut1 = createSliceMapFor(contig1, qualityDataStore,GapQualityValueStrategy.LOWEST_FLANKING);
    	SliceMap sut2 = createSliceMapFor(contig2, qualityDataStore,GapQualityValueStrategy.LOWEST_FLANKING);
        
    	TestUtil.assertNotEqualAndHashcodeDifferent(sut1, sut2);
    	
    }
}
