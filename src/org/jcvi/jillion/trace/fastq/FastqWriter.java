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
package org.jcvi.jillion.trace.fastq;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.internal.core.util.Sneak;
import org.jcvi.jillion.trace.fastq.FastqFileReader.Results;
/**
 * {@code FastqWriter} is an interface
 * that handles writing out {@link FastqRecord}s.
 * @author dkatzel
 *
 */
public interface FastqWriter extends Closeable{
	/**
	 * Write the given {@link FastqRecord} out.
	 * @param record the {@link FastqRecord} to write;
	 * can not be null.
	 * @throws IOException if there is a problem writing out the
	 * {@link FastqRecord}.
	 * @throws NullPointerException if record is null.
	 */
	void write(FastqRecord record) throws IOException;

    /**
     * Write all the records in the given Collection
     * @param fastqs the fastqs to write; can not be {@code null}.
     * @throws IOException if there is a problem writing any records.
     * @throws NullPointerException if collection is null or any element in the map is null.
     *
     * @implNote by default this just does
     * <pre>
     * <code>
     * for(FastqRecord r : fastqs){
     *     write(r);
     * }
     * </code>
     * </pre>
     * implementations should override this to provide a more efficient version.
     * @since 5.3.2
     */
    default void write(Collection<FastqRecord> fastqs) throws IOException{
        for(FastqRecord r : fastqs){
            write(r);
        }
    }
    
    /**
     * Write all the records in the given array.
     * @param fastqs the fastqs to write; can not be {@code null}.
     * @throws IOException if there is a problem writing any records.
     * @throws NullPointerException if array is null or any element in the map is null.
     *
     * @implNote by default this just does
     * <pre>
     * <code>
     * for(FastqRecord r : fastqs){
     *     write(r);
     * }
     * </code>
     * </pre>
     * implementations should override this to provide a more efficient version.
     * @since 6.0.2
     */
    default void write(FastqRecord[] fastqs) throws IOException{
        for(FastqRecord r : fastqs){
            write(r);
        }
    }
    /**
     * Write all the records in the given array.
     * @param fastqs the fastqs to write; can not be {@code null}.
     * @param fromIndex the index of the first element, inclusive.
     * @param toIndex the index of the last element, exclusive.
     * 
     * @throws IOException if there is a problem writing any records.
     * @throws NullPointerException if array is null or any element between {@code fromIndex} through
     * {@code toIndex -1} is null.
     *
     * @implNote by default this just does
     * <pre>
     * <code>
     for(int i=fromIndex; i< toIndex; i++) {
            write(fastqs[i]);
     
     }
     </code>
     * </pre>
     * implementations should override this to provide a more efficient version.
     * @since 6.0.2
     */
    default void write(FastqRecord[] fastqs, int fromIndex, int toIndex ) throws IOException{
        for(int i=fromIndex; i< toIndex; i++) {
            write(fastqs[i]);
        }
    }
	
	/**
         * Write the given {@link FastqRecord} out.
         * 
         * @param record the {@link FastqRecord} to write;
         * can not be null.
         * 
         * @param trimRange the {@link Range} to use to trim the nucleotide
         * and quality sequences.  If the trimRange is null, then the whole
         * sequence is written. 
         * 
         * @throws IOException if there is a problem writing out the
         * {@link FastqRecord}.
         * 
         * @throws NullPointerException if record is null.
         * 
         * 
         * @implNote The default implementation is given below, but 
         *           implementations may override this method to provide
         *           a more efficient version:
         * 
         * <pre>
         * if(trimRange==null){
         *     write(record);
         *     return;
         * }
         * write(record.getId(), 
                    record.getNucleotideSequence()
                            .toBuilder()
                            .trim(trimRange)
                            .build(), 
                    record.getQualitySequence()
                            .toBuilder()
                            .trim(trimRange)
                            .build(),
                 record.getComment());
         * </pre>
         * 
         * @since 5.2
         */
        default void write(FastqRecord record, Range trimRange) throws IOException{
            if(trimRange==null){
                write(record);
                return;
            }
            
            write(record.getId(), 
                    record.getNucleotideSequence()
                            .toBuilder()
                            .trim(trimRange)
                            .turnOffDataCompression(true)
                            .build(), 
                    record.getQualitySequence()
                            .toBuilder()
                            .trim(trimRange)
                            .turnOffDataCompression(true)
                            .build(),
                 record.getComment());
        }
	/**
	 * Write the given id, {@link NucleotideSequence}
	 * and {@link QualitySequence}
	 * out as a {@link FastqRecord} without a comment.
	 * @param id the id of the record to be written.
	 * @param nucleotides the {@link NucleotideSequence} to be written.
	 * @param qualities the {@link QualitySequence} to be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id, nucleotides or qualities are null.
	 */
	default void write(String id, NucleotideSequence nucleotides, QualitySequence qualities) throws IOException{
	    write(id, nucleotides, qualities, null);
	}
	/**
	 * Write the given id and {{@link NucleotideSequence}
	 * and {@link QualitySequence}
	 * out as a {@link FastqRecord} along with the optional comment.
	 * @param id the id of the record to be written.
	 * @param sequence the {@link NucleotideSequence} to be written.
	 * @param qualities the {@link QualitySequence} to be written.
	 * @param optionalComment comment to write, if this value is null,
	 * then no comment will be written.
	 * @throws IOException if there is a problem writing out the record.
	 * @throws NullPointerException if either id, nucleotides or qualities are null.
	 */
	default void write(String id, NucleotideSequence sequence, QualitySequence qualities, String optionalComment) throws IOException{
	    write(FastqRecordBuilder.create(id, sequence, qualities, optionalComment).build());
	}
	
	/**
	 * Create a new FastqWriter that will wrap the given fastqWriter and intercept any calls
	 * to write() to allow the record to be transformed in some way.  For example,
	 * to change the id or modify or trim the sequences; or even skip the record entirely.
	 * @param delegate the writer to delegate the actual writing to this writer will be called
	 * after each record is adapted.
	 * 
	 * @param adapter a Function that is given the input FastqRecord to be written
	 * and will return a possibly new FastqRecord to actually write.  If the function
	 * returns {@code null} then the record is skipped.
	 * @return a new FastqWriter; will never be null.
	 * @throws NullPointerException if any parameter is null.
	 * 
	 * @since 5.3
	 */
	public static FastqWriter adapt(FastqWriter delegate, Function<FastqRecord, FastqRecord> adapter){
	    return new FastqWriterAdapter(delegate, adapter);
	}
	/**
	 * Write the contents of the given datastore to the given output file.  
	 * The output fastq file written will use the same {@link FastqQualityCodec}
	 * as was used in the input fastq of the datastore.
	 * 
	 * @apiNote this is the same as
	 * {@link #write(FastqFileDataStore, File, FastqQualityCodec) write(datastore, outputFile, datastore.getQualityCodec())}.
	 * 
	 * @param datastore the {@link FastqFileDataStore} to write out; can not be null.
	 * @param outputFile the output file to write the resulting Fastq file to.
	 * 
	 * @throws IOException if there are any problems writing out the file.
	 * @throws NullPointerException if any parameter is null.
	 * 
	 * @since 5.3
	 */
	public static void write(FastqFileDataStore datastore, File outputFile) throws IOException{
	    write(datastore, outputFile, datastore.getQualityCodec());
	}
	
	/**
         * Write the contents of the given datastore to the given output file.  
         * The output fastq file written will use the same {@link FastqQualityCodec}
         * as was used in the input fastq of the datastore.
         * 
         * 
         * 
         * @param datastore the {@link FastqDataStore} to write out; can not be null.
         * @param outputFile the output file to write the resulting Fastq file to.
         * @param coec the {@link FastqQualityCodec} to use when writing out the output fastq file.
         * 
         * @throws IOException if there are any problems writing out the file.
         * @throws NullPointerException if any parameter is null.
         * 
         * @since 5.3
         */
	public static void write(FastqDataStore datastore, File outputFile, FastqQualityCodec codec) throws IOException{
	    Objects.requireNonNull(datastore);
	    
	    try(FastqWriter writer = new FastqWriterBuilder(outputFile)
	                                    .qualityCodec(codec)
	                                    .build();
	            
	        ThrowingStream<FastqRecord> stream = datastore.records();
	            ){
	        stream.throwingForEach(writer::write);
	    }
	}
	
	public static void copy(FastqParser in, OutputStream out) throws IOException{
	    
            try(Results results = FastqFileReader.read(in);
                    
                    FastqWriter writer = new FastqWriterBuilder(out)
                                            .qualityCodec(results.getCodec())
                                            .build();
                    
                ThrowingStream<FastqRecord> stream = results.records();
                    ){
                stream.throwingForEach(writer::write);
            }
	}
	
    public static void copy(FastqParser in, FastqQualityCodec codec,
            OutputStream out) throws IOException {

        try (FastqWriter writer = new FastqWriterBuilder(out)
                                        .qualityCodec(codec).build()) {
            FastqFileReader.forEach(in, codec,  (id, fastq) -> writer.write(fastq));

        }
    }
    
    public static void copy(FastqParser in, FastqQualityCodec codec,
            File out) throws IOException {

        try (FastqWriter writer = new FastqWriterBuilder(out)
                                        .qualityCodec(codec).build()) {
            FastqFileReader.forEach(in, codec,  (id, fastq) -> writer.write(fastq));

        }
    }
	public static void copy(FastqParser in, File out) throws IOException{
            
            try(Results results = FastqFileReader.read(in);
                    
                    FastqWriter writer = new FastqWriterBuilder(out)
                                            .qualityCodec(results.getCodec())
                                            .build();
                    
                ThrowingStream<FastqRecord> stream = results.records();
                    ){
                stream.throwingForEach(writer::write);
            }
        }
	
	public static void copy(FastqParser in, OutputStream out, Predicate<FastqRecord> filter) throws IOException{
            
            try(Results results = FastqFileReader.read(in);
                    
                    FastqWriter writer = new FastqWriterBuilder(out)
                                            .qualityCodec(results.getCodec())
                                            .build();
                    
                ThrowingStream<FastqRecord> stream = results.records();
                    ){
                stream.filter(filter)
                        .throwingForEach(writer::write);
            }
        }

	public static void copy(FastqParser in, File out, Predicate<FastqRecord> filter) throws IOException{
            
            try(Results results = FastqFileReader.read(in);
                    
                    FastqWriter writer = new FastqWriterBuilder(out)
                                            .qualityCodec(results.getCodec())
                                            .build();
                    
                ThrowingStream<FastqRecord> stream = results.records();
                    ){
                stream.filter(filter)
                        .throwingForEach(writer::write);
            }
        }
	
    public static void copy(FastqParser in, FastqQualityCodec codec,
            OutputStream out, Predicate<FastqRecord> filter)
            throws IOException {

        try (FastqWriter writer = new FastqWriterBuilder(out)
                                        .qualityCodec(codec).build()) {
            FastqFileReader.forEach(in, codec, filter,  (id, record) -> writer.write(record));

        }
    }

    public static void copy(FastqParser in, FastqQualityCodec codec,
            File out, Predicate<FastqRecord> filter)
            throws IOException {

        try (FastqWriter writer = new FastqWriterBuilder(out)
                                        .qualityCodec(codec).build()) {
            FastqFileReader.forEach(in, codec, filter,  (id, record) -> writer.write(record));

        }
    }
	
	public static void copyById(FastqParser in, OutputStream out, Predicate<String> idFilter) throws IOException{
	    
	    try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))){
	        in.parse(new AbstractFastqVisitor() {
               
	            StringBuilder builder = new StringBuilder(2000);
                    
                    @Override
                    public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
                            String id, String optionalComment) {
                        if(idFilter.test(id)){
                            builder.setLength(0);
                            builder.append('@').append(id);
                            if(optionalComment !=null){
                                builder.append(' ').append(optionalComment);
                            }
                            builder.append('\n');
                            return new FastqRecordVisitor() {
                                
                                @Override
                                public void visitQualities(QualitySequence qualities) {
                                    visitEncodedQualities(FastqQualityCodec.getDefault().encode(qualities));
                                    
                                }
                                
                                @Override
                                public void visitNucleotides(String nucleotides) {
                                    builder.append(nucleotides).append('\n');
                                    
                                }
                                
                                @Override
                                public void visitEnd() {
                                    try {
                                        writer.write(builder.toString());
                                    } catch (IOException e) {
                                        Sneak.sneakyThrow(e);
                                    }
                                    
                                }
                                
                                @Override
                                public void visitEncodedQualities(String encodedQualities) {
                                    builder.append("+\n").append(encodedQualities).append('\n');
                                    
                                }
                                
                                @Override
                                public void halted() {
                                    // TODO Auto-generated method stub
                                    
                                }
                            };
                        }
                        return null;
                    }
                    
                  
                });
	    }
	}
}
