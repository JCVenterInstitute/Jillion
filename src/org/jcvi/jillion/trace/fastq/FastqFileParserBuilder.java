package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

import org.jcvi.jillion.core.io.IOUtil;

public class FastqFileParserBuilder {

    private final File file;
    private final Function<File, InputStream> toInputStreamFunction;
    private final InputStream in;
    
    private boolean hasComments;
    private boolean multiline;
    
    public FastqFileParserBuilder(InputStream in){
        Objects.requireNonNull(in, "Inputstream can not be null");
        
        this.in = in;
        this.file = null;
        this.toInputStreamFunction = null;
    }
    public FastqFileParserBuilder(File file) throws IOException{
        this(file, null);
    }
    public FastqFileParserBuilder(File file,
            Function<File, InputStream> toInputStreamFunction) throws IOException {
        IOUtil.verifyIsReadable(file);
        
        this.file = file;
        this.toInputStreamFunction = toInputStreamFunction;
        this.in = null;
    }
    
    public FastqFileParserBuilder hasComments(boolean hasComments){
        this.hasComments = hasComments;
        return this;
    }
    
    public FastqFileParserBuilder hasMultilineSequences(boolean multiline){
        this.multiline = multiline;
        return this;
    }
    
    public FastqParser build() throws IOException{
        if(in ==null){
            return FastqFileParser.create(file, toInputStreamFunction, hasComments, multiline);
        }
        return FastqFileParser.create(in, hasComments, multiline);
    }
    
    
}
