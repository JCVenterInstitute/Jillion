package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
public class TestWriteTrimmedFastq {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();
    
    ResourceHelper helper = new ResourceHelper(TestWriteTrimmedFastq.class);
    
    private FastqFileDataStore datastore;
    
    @Before
    public void setup() throws IOException{
        datastore = new FastqFileDataStoreBuilder(helper.getFile("files/sanger.fastq")).build();
    }
    @Test
    public void untrimmed() throws DataStoreException, IOException{
        File outputFile = tmpDir.newFile();
        try(StreamingIterator<FastqRecord> iter = datastore.iterator();
                FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                .qualityCodec(FastqQualityCodec.SANGER)
                                                .build();
                ){
            while(iter.hasNext()){
                writer.write(iter.next());
            }
        }
        
        try(FastqFileDataStore actual = new FastqFileDataStoreBuilder(outputFile)
                                                    .qualityCodec(FastqQualityCodec.SANGER)
                                                    .build();){
            assertDataStoresAreEqual(datastore, actual);
        }
                        
    }
    
    @Test
    public void untrimmedDifferentFastqRecordImpl() throws DataStoreException, IOException{
        File outputFile = tmpDir.newFile();
        try(StreamingIterator<FastqRecord> iter = datastore.iterator();
                FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                .qualityCodec(FastqQualityCodec.SANGER)
                                                .build();
                ){
            while(iter.hasNext()){
                writer.write(new OtherFastqRecord(iter.next()));
            }
        }
        
        try(FastqFileDataStore actual = new FastqFileDataStoreBuilder(outputFile)
                                                    .qualityCodec(FastqQualityCodec.SANGER)
                                                    .build();){
            assertDataStoresAreEqual(datastore, actual);
        }
                        
    }
    
    @Test
    public void trimmed() throws DataStoreException, IOException{
        File outputFile = tmpDir.newFile();
        File outputFile2 = tmpDir.newFile();
        try(StreamingIterator<FastqRecord> iter = datastore.iterator();
                FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                .qualityCodec(FastqQualityCodec.SANGER)
                                                .build();
                
                FastqWriter writer2 = new FastqWriterBuilder(outputFile2)
                                                .qualityCodec(FastqQualityCodec.SANGER)
                                                .build();
                ){
            Range range = Range.of(20,50);
            while(iter.hasNext()){
                FastqRecord next = iter.next();
                
                writer.write(next, range);
                
                writer2.write(next.getId(),
                        next.getNucleotideSequence().toBuilder().trim(range).build(),
                        next.getQualitySequence().toBuilder().trim(range).build(),
                        next.getComment());
            }
        }
        
        try(FastqFileDataStore actual = new FastqFileDataStoreBuilder(outputFile)
                                                    .qualityCodec(FastqQualityCodec.SANGER)
                                                    .build();
                FastqFileDataStore expected = new FastqFileDataStoreBuilder(outputFile2)
                .qualityCodec(FastqQualityCodec.SANGER)
                .build();){
            assertDataStoresAreEqual(expected, actual);
        }
                        
    }
    
    @Test
    public void trimmedDifferentFastqRecordImpl() throws DataStoreException, IOException{
        File outputFile = tmpDir.newFile();
        File outputFile2 = tmpDir.newFile();
        try(StreamingIterator<FastqRecord> iter = datastore.iterator();
                FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                .qualityCodec(FastqQualityCodec.SANGER)
                                                .build();
                
                FastqWriter writer2 = new FastqWriterBuilder(outputFile2)
                                                .qualityCodec(FastqQualityCodec.SANGER)
                                                .build();
                ){
            Range range = Range.of(20,50);
            while(iter.hasNext()){
                FastqRecord next = new OtherFastqRecord(iter.next());
                
                writer.write(next, range);
                
                writer2.write(next.getId(),
                        next.getNucleotideSequence().toBuilder().trim(range).build(),
                        next.getQualitySequence().toBuilder().trim(range).build(),
                        next.getComment());
            }
        }
        
        try(FastqFileDataStore actual = new FastqFileDataStoreBuilder(outputFile)
                                                    .qualityCodec(FastqQualityCodec.SANGER)
                                                    .build();
                FastqFileDataStore expected = new FastqFileDataStoreBuilder(outputFile2)
                .qualityCodec(FastqQualityCodec.SANGER)
                .build();){
            assertDataStoresAreEqual(expected, actual);
        }
                        
    }
    
    @Test
    public void trimmedAndReencoded() throws DataStoreException, IOException{
        File outputFile = tmpDir.newFile();
        File outputFile2 = tmpDir.newFile();
        try(StreamingIterator<FastqRecord> iter = datastore.iterator();
                FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                .qualityCodec(FastqQualityCodec.ILLUMINA)
                                                .build();
                
                FastqWriter writer2 = new FastqWriterBuilder(outputFile2)
                                                .qualityCodec(FastqQualityCodec.ILLUMINA)
                                                .build();
                ){
            Range range = Range.of(20,50);
            while(iter.hasNext()){
                FastqRecord next = iter.next();
                
                writer.write(next, range);
                
                writer2.write(next.getId(),
                        next.getNucleotideSequence().toBuilder().trim(range).build(),
                        next.getQualitySequence().toBuilder().trim(range).build(),
                        next.getComment());
            }
        }
        
        try(FastqFileDataStore actual = new FastqFileDataStoreBuilder(outputFile)
                                                    .qualityCodec(FastqQualityCodec.ILLUMINA)
                                                    .build();
                FastqFileDataStore expected = new FastqFileDataStoreBuilder(outputFile2)
                .qualityCodec(FastqQualityCodec.ILLUMINA)
                .build();){
            assertDataStoresAreEqual(expected, actual);
        }
                        
    }
    
    @Test
    public void untrimmedReencoded() throws DataStoreException, IOException{
        File outputFile = tmpDir.newFile();
        try(StreamingIterator<FastqRecord> iter = datastore.iterator();
                FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                .qualityCodec(FastqQualityCodec.ILLUMINA)
                                                .build();
                ){
            while(iter.hasNext()){
                writer.write(iter.next());
            }
        }
        
        try(FastqFileDataStore actual = new FastqFileDataStoreBuilder(outputFile)
                                                    .qualityCodec(FastqQualityCodec.ILLUMINA)
                                                    .build();){
            assertDataStoresAreEqual(datastore, actual);
        }
                        
    }
    private void assertDataStoresAreEqual(FastqFileDataStore expected, FastqFileDataStore actual) throws DataStoreException {
        try(
         StreamingIterator<FastqRecord> actualIter = actual.iterator();
            StreamingIterator<FastqRecord> expectedIter = expected.iterator();
            
         ){
        while(expectedIter.hasNext()){
            assertTrue(actualIter.hasNext());
            assertEquals(expectedIter.next(), actualIter.next());
        }
        assertFalse(actualIter.hasNext());
      }
    }
    
    
    private static class OtherFastqRecord implements FastqRecord{

        public OtherFastqRecord(FastqRecord delegate) {
            this.delegate = delegate;
        }

        private final FastqRecord delegate;
        
        @Override
        public String getId() {
            return delegate.getId();
        }

        @Override
        public NucleotideSequence getNucleotideSequence() {
            return delegate.getNucleotideSequence();
        }

        @Override
        public QualitySequence getQualitySequence() {
            return delegate.getQualitySequence();
        }

        @Override
        public String getComment() {
            return delegate.getComment();
        }
        
    }
}
