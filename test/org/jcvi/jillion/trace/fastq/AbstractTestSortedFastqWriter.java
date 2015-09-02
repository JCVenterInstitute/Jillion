package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
public abstract class AbstractTestSortedFastqWriter {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();
    
    private FastqWriter sut;
    private  File outputFile;
    
    Comparator<FastqRecord> comparator = (a,b)-> a.getId().compareTo(b.getId());
    @Before
    public void setup() throws IOException{
        outputFile = tmpDir.newFile("output.fastq");
        FastqWriterBuilder builder = new FastqWriterBuilder(outputFile)
                                        .qualityCodec(FastqQualityCodec.SANGER);
        addSortStrategy(builder, comparator);
        sut = builder.build();
    }
    protected abstract void addSortStrategy(FastqWriterBuilder builder, Comparator<FastqRecord> comparator) throws IOException;
    
    
    FastqRecord a = new FastqRecordBuilder("AA", NucleotideSequenceTestUtil.create("ACGTACGT"),
                                    new QualitySequenceBuilder(new byte[]{20,30,40,50,20,30,40,50}).build())
                        .build();
    
    FastqRecord bb = new FastqRecordBuilder("BB", NucleotideSequenceTestUtil.create("TTTTTTTT"),
            new QualitySequenceBuilder(new byte[]{20,20,20,20,20,20,20,20}).build())
                        .build();
    
    @Test
    public void noRecordsToWrite() throws IOException{
        sut.close();
        assertEquals(0, outputFile.length());
    }
    
    @Test
    public void oneRecord() throws IOException, DataStoreException{
        sut.write(a);
        sut.close();
        assertRecordOrder(a);
    }
    
    @Test
    public void twoRecordsAlreadySorted() throws IOException, DataStoreException{
        sut.write(a);
        sut.write(bb);
        sut.close();
        assertRecordOrder(a, bb);
    }
    
    @Test
    public void twoRecordsReverseSorted() throws IOException, DataStoreException{
        
        sut.write(bb);
        sut.write(a);
        sut.close();
        assertRecordOrder(a, bb);
    }
    
    @Test
    public void hundredRecords() throws IOException, DataStoreException{
        List<FastqRecord> expectedOrder = new ArrayList<>(100);
        for(int i=0; i< 50; i++){
            expectedOrder.add( new FastqRecordBuilder(
                    String.format("%s_%02d", a.getId(), +i), a.getNucleotideSequence(), a.getQualitySequence()).build());
        }
        for(int i=0; i< 50; i++){
            expectedOrder.add( new FastqRecordBuilder(
                    String.format("%s_%02d", bb.getId(), +i), bb.getNucleotideSequence(), bb.getQualitySequence()).build());
            }
        
        List<FastqRecord> shuffled = new ArrayList<>(expectedOrder);
        Collections.shuffle(shuffled);
        
        for(FastqRecord e : shuffled){
            sut.write(e);
        }
        sut.close();
        assertRecordOrder(expectedOrder);
        
    }
    
    private void assertRecordOrder(FastqRecord... expectedOrder) throws IOException, DataStoreException{
        List<FastqRecord> list = new ArrayList<FastqRecord>();
        for(FastqRecord e: expectedOrder){
            list.add(e);
        }
        assertRecordOrder(list);
    }
    
    private void assertRecordOrder(List<FastqRecord> expectedOrder) throws IOException, DataStoreException {
        try(FastqDataStore datastore = new FastqFileDataStoreBuilder(outputFile)
                                                .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                .qualityCodec(FastqQualityCodec.SANGER)
                                                .build();
                
            StreamingIterator<FastqRecord> actualIter = datastore.iterator();    
            ){
            Iterator<FastqRecord> expectedIter = expectedOrder.iterator();
            while(expectedIter.hasNext()){
                FastqRecord expected = expectedIter.next();
                if(!actualIter.hasNext()){
                   fail("actual missing records starting with " + expected.getId()); 
                }
                assertEquals(expected, actualIter.next());
            }
            if(actualIter.hasNext()){
                fail("actual has extra records starting with " + actualIter.next().getId());
            }
        }
        
    }
}
