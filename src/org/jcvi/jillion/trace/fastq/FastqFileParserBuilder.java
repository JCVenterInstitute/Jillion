package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

import org.jcvi.jillion.core.io.IOUtil;
/**
 * Creates a {@link FastqParser}
 * that parses the given fastq encoded file
 * along with additional configuration options.
 * 
 * 
 * @author dkatzel
 * 
 * @since 5.0
 */
public final class FastqFileParserBuilder {

    private final File file;
    private final Function<File, InputStream> toInputStreamFunction;
    private final InputStream in;
    
    private boolean hasComments;
    private boolean multiline;
    /**
	 * Create a new Builder instance
	 * that will parse the given fastq encoded
	 * {@link InputStream}.  Please Note that inputStream implementations
	 * of the FastqFileParser can only be parsed once 
	 * and will not not create {@link FastqVisitorMemento}s
	 * or use {@link #accept(FastqVisitor, FastqVisitorMemento)}
	 * method.
	 * 
	 * @apiNote if you have a compressed file that you want to be
	 * able to parse multiple times, use {@link #FastqFileParserBuilder(File, Function)}
	 * 
	 * @param in the fastq encoded {@link InputStream} to parse; can not be null.
	 * 
	 * @throws NullPointerException if inputstream is null.
	 * 
	 * @return a new {@link FastqParser} instance; will never be null.
	 * 
	 * @see #FastqFileParserBuilder(File, Function)
	 */
    public FastqFileParserBuilder(InputStream in){
        Objects.requireNonNull(in, "Inputstream can not be null");
        
        this.in = in;
        this.file = null;
        this.toInputStreamFunction = null;
    }
    /**
     * Create a Builder that will parse the given
     * fastq encoded File.
     * @param fastqFile the fastq file to be parsed; can not be null.
     * 
     * @throws IOException if the file is not readable
     * @throws NullPointerException if file is null.
     */
    public FastqFileParserBuilder(File fastqFile) throws IOException{
    	 IOUtil.verifyIsReadable(fastqFile);
         
         this.file = fastqFile;
         this.toInputStreamFunction = null;
         this.in = null;
    }
    /**
	 * Create a new Builder instance
	 * that will parse the given a compressed fastq file.
	 * Each time the file should be parsed, the provided Function will be 
	 * called to get the appropriate {@link InputStream} for it.
	 * This constructor should be used in preference
	 * to {@link #FastqFileParserBuilder(InputStream)} if the file needs to be parsed
	 * multiple times.  
	 * 
	 * @param fastqFile the file to parse; can not be null.
	 * @param toInputStream {@link Function} to convert the given {@link File}
	 * into a <strong>new</strong> raw {@link InputStream}.  This allows the parser to handle compressed
	 * files.  A new InputStream should be created each time the function is called.  Can not be null.
	 * 
	 * @apiNote 
	 * For example if you wanted to parse a gzipped fastq file:
	 * <pre>
	 * {@code
	 * Function &lt;File, InputStream&gt; toGzipInputStream = f -&gt; {
	 * 	try {
	 * 		return new GZIPInputStream(new FileInputStream(f));
	 * 	} catch (IOException e) {
	 * 		throw new UncheckedIOException(e);
	 * 	}
	 * };
	 * 
	 * FastqParser parser = new FastqFileParserBuilder(gzippedFfastqFile, toGzipInputStream)
	 *                           .build();
	 * </pre>
	 * 
	 * @implNote The performance of random accessing records in this fastq file
	 * is dependent on {@link InputStream#skip(long)} implementation returned by the function.
	 * 
	 * @throws IOException  if the file does not exist or can not be read.
	 * @throws NullPointerException if any parameters are null or if the function returns null.
	 * 
	 */
    public FastqFileParserBuilder(File fastqFile,
            Function<File, InputStream> toInputStreamFunction) throws IOException {
        IOUtil.verifyIsReadable(fastqFile);
        Objects.requireNonNull(toInputStreamFunction);
        
        this.file = fastqFile;
        this.toInputStreamFunction = toInputStreamFunction;
        this.in = null;
    }

	/**
	 * Does this fastq file contain comments on the deflines.
	 * If not called, then by default this builder uses hasComments = false.
	 * 
	 * @param hasComments
	 *            do the deflines of the sequences contain comments. If set to
	 *            {@code true} then a more computationally intensive parsing is
	 *            performed to try to distinguish the id from the comment.
	 *            Remember some Fastq id's can have spaces which makes comment
	 *            detection difficult and complex.
	 * @return this
	 */
    public FastqFileParserBuilder hasComments(boolean hasComments){
        this.hasComments = hasComments;
        return this;
    }
    /**
     * Does this fastq file contain records that may have multiple lines for the sequence
     * and qualities sequences.
     * Most fastq files define each record in 4 lines:
     * <ol>
     * <li>The defline</li>
     * <li>nucleotide sequence</li>
     * <li>The quality defline</li>
     * <li>quality sequence</li>
     * </ol>
     * 
     * However, some fastq files split the nucleotide and quality sequences
     * over multiple lines.  For performance reasons,
     * checking for multi-lines has been turned off by default
     * and must be explicitly turned on by calling this method.
     * 
     * @param multiline {@code true} if this file may contain multilines;
     * {@code false} otherwise (defaults to {@code false}.
     * 
     * @return this
     */
    public FastqFileParserBuilder hasMultilineSequences(boolean multiline){
        this.multiline = multiline;
        return this;
    }
    /**
     * Creates a new {@link FastqParser} object
     * which will parse the given Fastq encoded file
     * using the provided options.
     * 
     * @return a new {@link FastqParser}; will never be null.
     * @throws IOException if there is a problem creating the parser object.
     */
    public FastqParser build() throws IOException{
        if(in ==null){
            return FastqFileParser.create(file, toInputStreamFunction, hasComments, multiline);
        }
        return FastqFileParser.create(in, hasComments, multiline);
    }
    
    
}
