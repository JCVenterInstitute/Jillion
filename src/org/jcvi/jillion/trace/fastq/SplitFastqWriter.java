package org.jcvi.jillion.trace.fastq;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * Utility class that creates {@link FastqWriter} instances
 * that split the {@link FastqRecord} objects being written out
 * over several different output files.
 * <p>
 * The rules for how the records are split are determined
 * by the writer implementation chosen in each factory method.
 * </p>
 * <p>
 * Construction of the individual fastq writers used by the Split Writer
 * is delegated to a user supplied lambda function must return a non-null {@link FastqWriter} of the correct type.
 * </p>
 * 
 * 
 * @author dkatzel
 *
 * @since 5.0
 */
public final class SplitFastqWriter{
	
	/**
	 * Functional interface to create new {@link FastqWriter}
	 * instances.
	 * @author dkatzel
	 *
	 * 
	 */
	@FunctionalInterface
	public interface FastqWriterFactory{
		/**
		 * Create a new {@link FastqWriter} for the ith
		 * fastq file.
		 * @param i the ith file to create, will start at value 1
		 * not zero.
		 * @return a new FastqWriter; can not be null.
		 * @throws IOException if there is a problem creating the writer.
		 */
		FastqWriter create(int i) throws IOException;
	}
	
	/**
	 * Functional interface to create new {@link FastqWriter}
	 * instances.
	 * @author dkatzel
	 *
	 * @param <K> the type of object that will be used as a "key"
	 * to determine which of the output writers to write this record to.
	 * 
	 * 
	 */
	@FunctionalInterface
	public interface DeconvolveFastqRecordWriterFactory<K>{
		/**
		 * Create a new {@link FastqWriter} for the
		 * fastq file for the given deconvolution key.
		 * @param key the deconvolution key for the file to create;should never be null.
		 * @return a new {@link FastqWriter}; can not be null.
		 * @throws IOException if there is a problem creating the writer.
		 */
		FastqWriter create(K key) throws IOException;
	}
	
	/**
	 * Creates a new {@link FastqWriter} instance that will spread out
	 * the {@link FastqRecord}s to be written to create several fastq files.  The
	 * first fastq record written will be written to the first output fastq, the second
	 * record written will be written to the second output fastq etc.  After all the output files
	 * have written a record, the next {@link FastqRecord} to be written will write to the first
	 * output file again.  This will continue until the writer is closed.
	 * 
	 * @param recordsPerFile the max number of {@link FastqRecord}s to be written to a file
	 * before it should be closed and the next file created. Must be >=1.
	 * 
	 * @param supplier a {@link FastqWriterFactory} instance that will create a new {@link FastqWriter} for the
	 * ith file to be created.  The passed in value i will be in the range 1..N where N is the number of files
	 * created (will start at 1 not 0).  If no records are written, then supplier will never be called. Can not be null.
	 * 
	 * @return a new {@link FastqWriter} instance; should never be null.
	 * 
	 * @throws NullPointerException if supplier lambda is null.
	 * @throws IllegalArgumentException if max records per file is < 1.
	 * 
	 * @apiNote for example, to make a Split FastqWriter that will write out to 10 different fastq files
	 *  named "1.fastq" to "10.fastq" in a round robin fashion would look like this:
	 * <pre>
	 * {@code 
	 * File outputDir = ...
	 * Iterator<FastqRecord> iter = ...
	 * 
	 * try(FastqWriter writer = SplitFastqWriter.roundRobin(
	 * 					10,
	 * 					i -> new FastqWriterBuilder(new File(outputDir, i +".fastq"))
	 *										.build());
	*){
	*	while(iter.hasNext()){
	*		writer.write(iter.next());
	*	}
	*}
	 * </pre>
	 * 
	 */
	public static FastqWriter roundRobin(int numberOfFiles, FastqWriterFactory supplier){
		return new RoundRobinSplitFastqWriter(numberOfFiles, supplier);
	}
	
	
	/**
	 * Creates a new {@link FastqWriter} instance that will internally create several fastq files
	 * that will each contain only the given number of records.  Once the first fastq file written has reached
	 * the max number of records, a new output fastq file will be created to write out the next max number of records
	 * (the additional written records will be rolled over to the new writer).
	 * 
	 * @param recordsPerFile the max number of {@link FastqRecord}s to be written to a file
	 * before it should be closed and the next file created. Must be >=1.
	 * 
	 * @param supplier a {@link FastqWriterFactory} instance that will create a new FastqWriter for the
	 * ith file to be created.  The passed in value i will be in the range 1..N where N is the number of files
	 * created (will start at 1 not 0).  If no records are written, then supplier will never be called.  Can not be null.
	 * 
	 * @return a new {@link FastqWriter} instance; will never be null.
	 * @throws NullPointerException if supplier lambda is null.
	 * @throws IllegalArgumentException if max records per file is < 1.
	 * 
	 *  @apiNote for example, to make a Split Fastq Writer that will write out to a new fastq file
	 *  every 1000 sequences
	 *  named "1.fastq", "2.fastq", etc where "1.fastq" will contain the first 1000 sequences
	 *  and "2.fastq" will contain the next 1000 sequences etc would look like this:
	 * <pre>
	 * {@code 
	 * File outputDir = ...
	 * Iterator<FastqRecord> iter = ...
	 * 
	 * try(FastqWriter writer = SplitFastqWriter.rollover(
	 * 					1000,
	 * 					i -> new FastqWriterBuilder(new File(outputDir, i +".fastq"))
	 *										.build());
	*){
	*	while(iter.hasNext()){
	*		writer.write(iter.next());
	*	}
	*}
	 * </pre>
	 */
	public static FastqWriter rollover(int maxRecordsPerFile,
			FastqWriterFactory supplier){
		return new RolloverSplitFastqWriter(maxRecordsPerFile, supplier);
	}
	
	
	/**
	 * Write out many fastq files where a "deconvolution" function determines which file
	 * each record is written to based on the contents of that record.  Deconvolution functions
	 * typically use the actual read sequence to find sequencing barcodes or bin by characteristics 
	 * in specially formatted record ids.  
	 * 
	 * @param deconvolutionFunction the lambda function that given a {@link FastqRecord} will
	 * determine the "key" that will be used to map to the output fastq writer to write the record to.
	 * The returned key object must correctly implement equals and hashcode.  Think of the key
	 * as the key in a {@code Map<Key, FastqWriter>}.  If the returned Key can not be null.is {@code null}
	 * then the record will not be written out to any of the output files.
	 *  
	 * @param supplier  a lambda function  that given the key returned
	 *  by the deconvolutionFunction, create a new {@link FastqWriter} for
	 *  those records with that key.
	 *   The supplier will only be called when a key from the deconvolutionFunction is seen for the 
	 * first time (as determined by the key's equals() and hashcode() implementation).
	 * 
	 * @return a new {@link FastqWriter}.
	 * 
	 * @param <K> The deconvolution key type that is returned from the deconvolution function and passed 
	 * to the supplier function.
	 * 
	 * @throws NullPointerException if any parameter is null.
	 * 
	 * @apiNote For example to if there exists a method that given the read, figures
	 * out which "barcode" was used in that read, then to the code to bin the reads
	 * by barcode will look like this:
	 * 
	 * <pre>
	 * {@code 
	 * 
	 * File outputDir = ...
	 * Iterator<FastqRecord> iter = ...
	 * try(FastqWriter writer = SplitFastqWriter.deconvolve(
	 * 					record-> findBarocdeFor(record),
	 * 					barcode -> new FastqWriterBuilder(new File(outputDir, barcode + ".fastq"))
	 * 								.build()
	 * 					);
	 * ){
	 * 		while(iter.hasNext()){
	 * 			writer.write(iter.next());
	 * 		}
	 * }
	 * </pre>
	 * 
	 * Will write out all the fastq records to multiples files, named "$barcode.fasta" where each
	 * file only contains reads from that particular barcode.
	 */
	public static <K> FastqWriter deconvolve(Function<FastqRecord, K> deconvolutionFunction,
			DeconvolveFastqRecordWriterFactory<K> supplier){
		return  new DeconvolutionFastqWriter<K>(deconvolutionFunction, supplier);
	}
	
	private SplitFastqWriter(){
		//can not instantiate
	}
	
	
	
	
	
	private static final class 	DeconvolutionFastqWriter<K> implements FastqWriter {

		private volatile boolean closed = false;
		
		private final Map<K, FastqWriter> writers = new HashMap<>();
		
		private final Function<FastqRecord, K> deconvolutionFunction;
		private final DeconvolveFastqRecordWriterFactory<K> supplier;
		
		
		public DeconvolutionFastqWriter(Function<FastqRecord, K> deconvolutionFunction, DeconvolveFastqRecordWriterFactory<K> supplier) {
			Objects.requireNonNull(deconvolutionFunction);
			Objects.requireNonNull(supplier);
			
			this.deconvolutionFunction = deconvolutionFunction;
			this.supplier = supplier;
		}

		@Override
		public void close() throws IOException {
			if(closed){
				return;
			}
			closed=true;
			for(FastqWriter writer : writers.values()){
				IOUtil.closeAndIgnoreErrors(writer);
			}
					
			
		}
		private void checkNotClosed() throws IOException {
			if (closed) {
				throw new IOException("already closed");
			}
		}
		
		@Override
		public void write(FastqRecord record) throws IOException {
			checkNotClosed();
			Objects.requireNonNull(record);
			K key = deconvolutionFunction.apply(record);
			
			if(key ==null){
			    //skip
			    return;
			}
			writers.computeIfAbsent(key, k-> {
				try{
						return supplier.create(k);
				}catch(IOException e){
					throw new UncheckedIOException("error creating deconvolution writer for " + k, e);
				}})
				.write(record);
		}
		
	

		@Override
		public void write(String id, NucleotideSequence sequence, QualitySequence qualities) throws IOException {
			write(id, sequence, qualities, null);
			
		}

		@Override
		public void write(String id, NucleotideSequence sequence, QualitySequence qualities, String optionalComment)
				throws IOException {
			Objects.requireNonNull(id, "id can not be null");
			Objects.requireNonNull(sequence, "sequence can not be null");
			Objects.requireNonNull(qualities, "qualities can not be null");
			
			
			write(new FastqRecordBuilder(id, sequence, qualities).comment(optionalComment).build());
		}
		
	}
	private static final class RolloverSplitFastqWriter implements FastqWriter {

		private final FastqWriterFactory supplier;

		private final int recordsPerFile;
		//initialized to 0 because we will increment it to 1
		//the first time something is written
		private int splitFileNumber = 0;
		private int currentRecordCount;
		private FastqWriter currentWriter;

		private volatile boolean closed = false;

		private RolloverSplitFastqWriter(int recordsPerFile,
				FastqWriterFactory supplier) {

			Objects.requireNonNull(supplier);
			if (recordsPerFile < 1) {
				throw new IllegalArgumentException(
						"records per File must be >=1");
			}
			this.supplier = supplier;
			this.recordsPerFile = recordsPerFile;
			//initialized to max records per file so initial write creates a new writer
			this.currentRecordCount = recordsPerFile;
		}

		private void checkNotClosed() throws IOException {
			if (closed) {
				throw new IOException("already closed");
			}
		}

		@Override
		public void close() throws IOException {
			if (!closed && currentWriter !=null) {
				currentWriter.close();
			}
			closed = true;
		}

		@Override
		public void write(FastqRecord record) throws IOException {
			updateCurrentWriterIfNeeded();
			currentWriter.write(record);
			currentRecordCount++;
		}

		private void updateCurrentWriterIfNeeded() throws IOException {
			checkNotClosed();
			if (currentRecordCount == recordsPerFile) {
				if(currentWriter !=null){
					currentWriter.close();
				}
				splitFileNumber++;
				currentRecordCount = 0;
				currentWriter = supplier.create(splitFileNumber);
			}
		}

		@Override
		public void write(String id, NucleotideSequence sequence, QualitySequence qualities) throws IOException {
			updateCurrentWriterIfNeeded();
			currentWriter.write(id, sequence,qualities);
			currentRecordCount++;

		}

		@Override
		public void write(String id, NucleotideSequence sequence, QualitySequence qualities, String optionalComment) throws IOException {
			updateCurrentWriterIfNeeded();
			currentWriter.write(id, sequence,qualities, optionalComment);
			currentRecordCount++;

		}
	}
	
	private static final class RoundRobinSplitFastqWriter implements FastqWriter{

		private final FastqWriterFactory supplier;

		private int currentIndex=0;
		

		private volatile boolean closed = false;

		
		private final FastqWriter[] writers;
		
		private RoundRobinSplitFastqWriter(int numberOfFiles,
				FastqWriterFactory supplier) {

			Objects.requireNonNull(supplier);
			if (numberOfFiles < 1) {
				throw new IllegalArgumentException(
						"records per File must be >=1");
			}
			writers = new FastqWriter[numberOfFiles];
			
			this.supplier = supplier;
			
		}

		private void checkNotClosed() throws IOException {
			if (closed) {
				throw new IOException("already closed");
			}
		}
		
		private FastqWriter getCurrentWriter() throws IOException{
			checkNotClosed();
			FastqWriter writer=  writers[currentIndex];
			if(writer ==null){
				// supplied index always goes from 1..N not 0..n-1
				writer = supplier.create(currentIndex +1);
				writers[currentIndex] = writer;
			}
			currentIndex = (currentIndex +1) % writers.length;
			return writer;
			
		}

		@Override
		public void close() throws IOException {
			if (!closed) {
				for(FastqWriter writer : writers){
					IOUtil.closeAndIgnoreErrors(writer);
				}
			}
			closed = true;
		}

		@Override
		public void write(FastqRecord record) throws IOException {
			getCurrentWriter().write(record);
		}

		

		@Override
		public void write(String id, NucleotideSequence sequence, QualitySequence qualities) throws IOException {
			getCurrentWriter().write(id, sequence, qualities);

		}

		@Override
		public void write(String id, NucleotideSequence sequence, QualitySequence qualities, String optionalComment) throws IOException {
			getCurrentWriter().write(id, sequence, qualities, optionalComment);

		}
	}
}
