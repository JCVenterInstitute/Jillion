package org.jcvi.jillion.trace.fastq;

import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.util.SingleThreadAdder;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.FastqFileReader.Results;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestFastqReader {

    private final ResourceHelper helper = new ResourceHelper(this);
    
    @Test
    public void readFile() throws IOException{
        try(Results results = FastqFileReader.read(helper.getFile("files/giv_XX_15050.fastq"));
                ThrowingStream<FastqRecord> stream = results.records();
                ){
            
            assertEquals(FastqQualityCodec.SANGER, results.getCodec());
            assertEquals(282, stream.count());
        }
    }
    
    @Test
    public void readFileCodec() throws IOException{
        try(Results results = FastqFileReader.read(helper.getFile("files/giv_XX_15050.fastq"), FastqQualityCodec.SANGER);
                ThrowingStream<FastqRecord> stream = results.records();
                ){
            
            assertEquals(FastqQualityCodec.SANGER, results.getCodec());
            assertEquals(282, stream.count());
        }
    }
    
    @Test
    public void readParserCodec() throws IOException{
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                .hasComments(true)
                .build();
        try(Results results = FastqFileReader.read(parser, FastqQualityCodec.SANGER);
                ThrowingStream<FastqRecord> stream = results.records();
                ){
            
            assertEquals(FastqQualityCodec.SANGER, results.getCodec());
            assertEquals(282, stream.count());
        }
    }
    
    @Test
    public void readParser() throws IOException{
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                .hasComments(true)
                .build();
        try(Results results = FastqFileReader.read(parser);
                ThrowingStream<FastqRecord> stream = results.records();
                ){
            
            assertEquals(FastqQualityCodec.SANGER, results.getCodec());
            assertEquals(282, stream.count());
        }
    }
    
    @Test
    public void forEachFile() throws IOException, RuntimeException{
        SingleThreadAdder counter= new SingleThreadAdder();
        
        FastqFileReader.forEach(helper.getFile("files/giv_XX_15050.fastq"), 
                (id, record) -> counter.increment());
   
        assertEquals(282, counter.intValue());
    }
    
    @Test
    public void forEachFileCodec() throws IOException, RuntimeException{
        SingleThreadAdder counter= new SingleThreadAdder();
        
        FastqFileReader.forEach(helper.getFile("files/giv_XX_15050.fastq"), FastqQualityCodec.SANGER, 
                (id, record) -> counter.increment());
   
        assertEquals(282, counter.intValue());
    }
    @Test
    public void forEach() throws IOException, RuntimeException{
        DirCounter counter= new DirCounter();
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                                            .hasComments(true)
                                            .build();
        FastqFileReader.forEach(parser, (id, record) -> counter.analyze(id));
   
        assertEquals(282, counter.getTotalCount());
        assertEquals(143, counter.getFwdCount());
        assertEquals(282-143, counter.getRevCount());
    }
    
    private static class MyCheckedException extends Exception{
        public MyCheckedException(String message){
            super(message);
        }
    }
    @Test(expected = MyCheckedException.class)
    public void forEachThrowsCheckedException() throws IOException, RuntimeException, MyCheckedException{
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                                            .hasComments(true)
                                            .build();
        FastqFileReader.forEach(parser, (id, record) -> {throw new MyCheckedException("foo");});
   
    }
    
    @Test
    public void forEachWithCodec() throws IOException, RuntimeException{
        DirCounter counter= new DirCounter();
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                                            .hasComments(true)
                                            .build();
        FastqFileReader.forEach(parser, FastqQualityCodec.SANGER, (id, record) -> counter.analyze(id));
   
        assertEquals(282, counter.getTotalCount());
        assertEquals(143, counter.getFwdCount());
        assertEquals(282-143, counter.getRevCount());
    }
    
    @Test
    public void forEachWithCodecFilterLength() throws IOException, RuntimeException{
        DirCounter counter= new DirCounter();
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                                            .hasComments(true)
                                            .build();
        FastqFileReader.forEach(parser, FastqQualityCodec.SANGER, 
                record-> record.getLength() > 500, 
                (id, record) -> counter.analyze(id));
   
        assertEquals(230, counter.getTotalCount());
        assertEquals(116, counter.getFwdCount());
        assertEquals(230-116, counter.getRevCount());
    }
    @Test
    public void forEachFilterLength() throws IOException, RuntimeException{
        DirCounter counter= new DirCounter();
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                                            .hasComments(true)
                                            .build();
        FastqFileReader.forEach(parser, 
                record-> record.getLength() > 500, 
                (id, record) -> counter.analyze(id));
   
        assertEquals(230, counter.getTotalCount());
        assertEquals(116, counter.getFwdCount());
        assertEquals(230-116, counter.getRevCount());
    }
    
    @Test
    public void forEachNullFilter() throws IOException, RuntimeException{
        DirCounter counter= new DirCounter();
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                                            .hasComments(true)
                                            .build();
        FastqFileReader.forEach(parser, 
                (Predicate<FastqRecord>)null, 
                (id, record) -> counter.analyze(id));
   
        assertEquals(282, counter.getTotalCount());
        assertEquals(143, counter.getFwdCount());
        assertEquals(282-143, counter.getRevCount());
    }
    @Test
    public void forEachFilterLengthAndDir() throws IOException, RuntimeException{
        DirCounter counter= new DirCounter();
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                                            .hasComments(true)
                                            .build();
        FastqFileReader.forEach(parser,
                id-> id.endsWith("F") || id.endsWith("FB"),
                record-> record.getLength() > 500, 
                (id, record) -> counter.analyze(id));
   
        assertEquals(116, counter.getTotalCount());
        assertEquals(116, counter.getFwdCount());
        assertEquals(0, counter.getRevCount());
    }
    
    @Test
    public void forEachFilterCodecLengthAndDir() throws IOException, RuntimeException{
        DirCounter counter= new DirCounter();
        FastqParser parser = new FastqFileParserBuilder(helper.getFile("files/giv_XX_15050.fastq"))
                                            .hasComments(true)
                                            .build();
        FastqFileReader.forEach(parser, FastqQualityCodec.SANGER,
                id-> id.endsWith("R") || id.endsWith("RB"),
                record-> record.getLength() > 500, 
                (id, record) -> counter.analyze(id));
   
        assertEquals(114, counter.getTotalCount());
        assertEquals(0, counter.getFwdCount());
        assertEquals(114, counter.getRevCount());
    }
    
    private static class DirCounter{
        int fwd, rev;
        public void analyze(String id){
            if(id.endsWith("R") || id.endsWith("RB")){
                rev++;
            }else{
                fwd++;
            }
        }
        public int getTotalCount(){
            return fwd + rev;
        }
        public int getFwdCount(){
            return fwd;
        }
        public int getRevCount(){
            return rev;
        }
    }
}
