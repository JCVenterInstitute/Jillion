package org.jcvi.jillion.examples.fastq;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.trace.fastq.AbstractFastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.AbstractFastqVisitor;
import org.jcvi.jillion.trace.fastq.FastqFileDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqFileParser;
import org.jcvi.jillion.trace.fastq.FastqFileReader;
import org.jcvi.jillion.trace.fastq.FastqFileReader.Results;
import org.jcvi.jillion.trace.fastq.FastqParser;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqUtil;
import org.jcvi.jillion.trace.fastq.FastqVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.FastqWriter;
import org.jcvi.jillion.trace.fastq.FastqWriterBuilder;
import org.jcvi.jillion.trim.BwaQualityTrimmer;
import org.jcvi.jillion.trim.Trimmer;

public class TrimFastqVisitor {

    public static void main(String[] args) throws IOException{
        Trimmer<FastqRecord> bwaTrimmer = BwaQualityTrimmer.createFor(PhredQuality.valueOf(20));
        Set<String> ids = new HashSet<>();
        
        File fastqFile = new File("/path/to/fastq");
        File outputFile = new File("/path/to/new/fastq");
        
        
        //read first 100 records that are in the id list?
        int numberOfRecordstoWrite = 100;
        
        long minLength = 30; // or whatever size you want
        
        visitor(bwaTrimmer, ids, fastqFile, outputFile, numberOfRecordstoWrite, minLength);
    }

    public static void stream(Trimmer<FastqRecord> bwaTrimmer,
            Set<String> ids, File fastqFile, File outputFile,
            int numberOfRecordstoWrite, long minLength)
            throws IOException {
        
            try(Results results = FastqFileReader.read(fastqFile);
                    FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                        .qualityCodec(results.getCodec())
                                                        .sort(Comparator.comparing(FastqRecord::getId))
                                                        .build();
                    ){
                results.records()
                        .filter(record -> ids.contains(record.getId()) && record.getLength() >= minLength)
                        .flatMap(fastq ->{
                            Range trimRange = bwaTrimmer.trim(fastq);
                            if (trimRange.getLength() >= minLength) {
                                return Stream.of(fastq.toBuilder().trim(trimRange).build());
                            }
                            return Stream.empty();
                        })
                        .limit(numberOfRecordstoWrite)
                        .throwingForEach(fastq -> writer.write(fastq));
            }
            
            
    }
    
    public static void useDataStore(Trimmer<FastqRecord> bwaTrimmer, Set<String> ids, File fastqFile,
            File outputFile, int numRecordsToWriter, long minLength) throws IOException{
        
        try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
                                                    .hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)                                                    
                                                    .build();
                
                FastqWriter writer = new FastqWriterBuilder(outputFile)
                                                .qualityCodec(datastore.getQualityCodec())
                                                .build();
                ThrowingStream<String> idStream = datastore.idIterator().toThrowingStream()
                                                            .sorted()
                                                            .limit(100);
            ){
            idStream.throwingForEach(id -> writer.write(datastore.get(id)));
            
        }
    }
    
    private static void visitor(Trimmer<FastqRecord> bwaTrimmer,
            Set<String> ids, File fastqFile, File outputFile,
            int numberOfRecordstoWrite, long minLength)
            throws IOException {
        FastqParser parser = FastqFileParser.create(fastqFile);
        FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(parser);
        
        FastqWriter writer = new FastqWriterBuilder(outputFile)
                                    .qualityCodec(codec)
                                    .sort(Comparator.comparing(FastqRecord::getId))
                                    .build();
      
        
        
        parser.parse(new FastqVisitor(){

            int count=0;
            @Override
            public FastqRecordVisitor visitDefline(
                    FastqVisitorCallback callback, String id,
                    String optionalComment) {
                if(count > numberOfRecordstoWrite){
                    callback.haltParsing();
                }
                if(!ids.contains(id)){
                    return null;
                }
                
               
                return new AbstractFastqRecordVisitor(id, optionalComment, codec) {
                    
                    @Override
                    protected void visitRecord(FastqRecord fastq) {
                        Range trimRange = bwaTrimmer.trim(fastq);
                        
                        if (trimRange.getLength() >= minLength) {
                            try {
                                writer.write(fastq, trimRange);
                                count++;
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        } 
                        
                    }
                };
            }
            @Override
            public void visitEnd() {
                try{
                    writer.close();
                }catch(IOException e){
                   //ignore
                }
            }
            @Override
            public void halted() {
                visitEnd();
            }
            
        });
    }
    
    
   
}
