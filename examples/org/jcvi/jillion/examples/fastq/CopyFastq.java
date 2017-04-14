package org.jcvi.jillion.examples.fastq;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.jcvi.jillion.trace.fastq.FastqFileDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileParser;
import org.jcvi.jillion.trace.fastq.FastqWriter;

public class CopyFastq {

    public static void main(String[] args) throws IOException {
        File inputFastq = new File("/path/to/fastq");
        
        File output = new File("/path/to/output");
        
        copyById(inputFastq, output);
        
        copyByFilter(inputFastq, output);
        
        copyViaDataStore(inputFastq, output);

    }
    
    private static void copyViaDataStore(File inputFastq, File output) throws IOException{
        try(FastqFileDataStore datastore = FastqFileDataStore.fromFile(inputFastq)
                
                ){
            
            FastqWriter.write(datastore, output);
        }
        
    }

    private static void copyByFilter(File inputFastq, File output) throws IOException{
        try(OutputStream out = new BufferedOutputStream(new FileOutputStream(output))){
            
            FastqWriter.copy(FastqFileParser.create(inputFastq), out, fastq -> fastq.getLength() > 40);
        }
    }

    private static void copyById(File inputFastq, File output) throws IOException{
        Set<String> idsToKeep = new HashSet<>();
        
        try(OutputStream out = new BufferedOutputStream(new FileOutputStream(output))){
            
            FastqWriter.copyById(FastqFileParser.create(inputFastq), out, idsToKeep::contains);
        }
    }

}
