package org.jcvi.jillion.fasta;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
/**
 * Utility class that creates {@link FastaWriter} instances
 * that split the {@link FastaRecord} objects being written out
 * over several different output files.
 * <p>
 * The rules for how the records are split are determined
 * by the writer implementation chosen in each factory method.
 * </p>
 * <p>
 * Construction of the individual fasta writers used by the Split Writer
 * is delegated to a user supplied lambda function that is given the int i
 * for the ith file to be created and must return a non-null FastaWriter of the correct type.
 * </p>
 * The returned FastaWriter implementations are not thread-safe.
 * 
 * 
 * @author dkatzel
 *
 * @since 5.0
 */
public final class SplitFastaWriter{
	
	/**
	 * Functional interface to create new FastaWriter
	 * instances.
	 * @author dkatzel
	 *
	 * @param <T> the type of {@link FastaRecord} that is to be created.
	 */
	@FunctionalInterface
	public interface FastaRecordWriterFactory<T>{
		/**
		 * Create a new FastaWriter for the ith
		 * fasta file.
		 * @param i the ith file to create, will start at value 1
		 * not zero.
		 * @return a new FastaWriter; can not be null.
		 * @throws IOException if there is a problem creating the writer.
		 */
		T create(int i) throws IOException;
	}
	
	/**
	 * Functional interface to create new FastaWriter
	 * instances.
	 * @author dkatzel
	 *
	 * @param <K> the type of object that will be used as a "key"
	 * to determine which of the output writers to write this record to.
	 * 
	 * @param <T> the type of {@link FastaRecord} that is to be created
	 */
	@FunctionalInterface
	public interface DeconvolveFastaRecordWriterFactory<K,T>{
		/**
		 * Create a new FastaWriter for the
		 * fasta file for the given deconvolution key.
		 * @param key the deconvolutionkey for the file to create, will never be null.
		 * @return a new FastaWriter; can not be null.
		 * @throws IOException if there is a problem creating the writer.
		 */
		T create(K key) throws IOException;
	}
	
	/**
	 * Creates a new {@link FastaWriter} instance that will spread out
	 * the {@link FastaRecord}s to be written to create several fasta files.  The
	 * first fasta record written will be written to the first output fasta, the second
	 * record written will be written to the second output fasta etc.  After all the output files
	 * have written a record, the next {@link FastaRecord} to be written will write to the first
	 * output file again.  This will continue until the SplitWriter is closed.
	 * 
	 * @param interfaceClass The <strong>interface</strong> type the lambda function will be returning
	 * which is also going to be the return type for this Fasta Writer.
	 * 
	 * @param recordsPerFile the max number of {@link FastaRecord}s to be written to a file
	 * before it should be closed and the next file created. Must be >=1.
	 * 
	 * @param supplier a {@link FastqWriterFactory} instance that will create a new FastaWriter of type W for the
	 * ith file to be created.  The passed in value i will be in the range 1..N where N is the number of files
	 * created (will start at 1 not 0).  If no records are written, then supplier will never be called. Can not be null.
	 * 
	 * @return a new {@link FastaWriter} instance; will never be null. 
	 * The returned writer is not thread-safe.
	 * 
	 * @throws NullPointerException if supplier lambda is null.
	 * @throws IllegalArgumentException if max records per file is < 1.
	 * 
	 * @apiNote for example, to make a Split Fasta Writer that will write out to 10 different fasta files
	 *  named "1.fasta" to "10.fasta" in a round robin fashion would look like this:
	 * <pre>
	 * {@code 
	 * File outputDir = ...
	 * Iterator<NucleotideFastaRecord> iter = ...
	 * 
	 * try(NucleotideFastaWriter writer = SplitFastaWriter.roundRobin(NucleotideFastaWriter.class,
	 * 					10,
	 * 					i -> new NucleotideFastaWriterBuilder(new File(outputDir, i +".fasta"))
	 *										.build());
	*){
	*	while(iter.hasNext()){
	*		writer.write(iter.next());
	*	}
	*}
	 * </pre>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static <S, T extends Sequence<S>, F extends FastaRecord<S, T>, W extends FastaWriter<S,T,F>> W roundRobin(Class<W> interfaceClass, int numberOfFiles,
			FastaRecordWriterFactory<W> supplier){
		RoundRobinSplitFastaWriter<S, T, F, W> writer = new RoundRobinSplitFastaWriter<S, T, F, W>(numberOfFiles, supplier);
		
		return (W) Proxy.newProxyInstance(writer.getClass().getClassLoader(), new Class[]{interfaceClass}, new InvocationHandlerImpl<>(writer) );
	}
	
	
	/**
	 * Creates a new {@link FastaWriter} instance that will internally create several fasta files
	 * that will each contain only the given number of records.  Once the first fasta file written has reached
	 * the max number of records, a new output fasta file will be created to write out the next max number of records
	 * (the additional written records will be rolled over to the new writer).
	 * 
	 * 
	 * @param interfaceClass The <strong>interface</strong> type the lambda function will be returning
	 * which is also going to be the return type for this Fasta Writer.
	 * 
	 * @param recordsPerFile the max number of {@link FastaRecord}s to be written to a file
	 * before it should be closed and the next file created. Must be >=1.
	 * 
	 * @param supplier a {@link FastqWriterFactory} instance that will create a new FastaWriter of type W for the
	 * ith file to be created.  The passed in value i will be in the range 1..N where N is the number of files
	 * created (will start at 1 not 0).  If no records are written, then supplier will never be called.  Can not be null.
	 * 
	 * @return a new {@link FastaWriter} instance; will never be null.
	 * The returned writer is not thread-safe.
	 * 
	 * @throws NullPointerException if supplier lambda is null.
	 * @throws IllegalArgumentException if max records per file is < 1.
	 * 
	 *  @apiNote for example, to make a Split Fasta Writer that will write out to a new fasta file
	 *  every 1000 sequences
	 *  named "1.fasta", "2.fasta", etc where "1.fasta" will contain the first 1000 sequences
	 *  and "2.fasta" will contain the next 1000 sequences etc would look like this:
	 * <pre>
	 * {@code 
	 * File outputDir = ...
	 * Iterator<NucleotideFastaRecord> iter = ...
	 * 
	 * try(NucleotideFastaWriter writer = SplitFastaWriter.rollover(NucleotideFastaWriter.class,
	 * 					1000,
	 * 					i -> new NucleotideFastaWriterBuilder(new File(outputDir, i +".fasta"))
	 *										.build());
	*){
	*	while(iter.hasNext()){
	*		writer.write(iter.next());
	*	}
	*}
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public static <S, T extends Sequence<S>, F extends FastaRecord<S, T>, W extends FastaWriter<S,T,F>> W rollover(Class<W> interfaceClass, int maxRecordsPerFile,
			FastaRecordWriterFactory<W> supplier){
		RolloverSplitFastaWriter<S, T, F, W> writer = new RolloverSplitFastaWriter<S, T, F, W>(maxRecordsPerFile, supplier);
		
		return (W) Proxy.newProxyInstance(writer.getClass().getClassLoader(), new Class[]{interfaceClass}, new InvocationHandlerImpl<>(writer) );
	}
	
	
	/**
	 * Write out many fasta files where a "deconvolution" function determines which file
	 * each record is written to based on the contents of that record.  Deconvolution functions
	 * typically use the actual read sequence to find sequencing barcodes or bin by characteristics 
	 * in specially formatted record ids.  
	 * 
	 * @param interfaceClass The <strong>interface</strong> type the supplier function will be returning
	 * which is also going to be the return type for this Fasta Writer.
	 * 
	 * 
	 * @param deconvolutionFunction the lambda expression that given a FastaRecord will
	 * determine the "key" that will be used to map to the output fasta writer to write the record to.
	 * The returned key object must correctly implement equals and hashcode.  Think of the key
	 * as the key in a {@code Map<Key, FastaWriter>}.  If the returned Key
	 * is {@code null} then the record should be skipped and not written out to any of the
	 * output files.  The lambda function itself can not be null.
	 *  
	 * @param supplier given the key returned by the deconvolutionFunction create a new FastaWriter of the
	 * type W.  The supplier will only be called when a key from the deconvolutionFunction is seen for the 
	 * first time (as determined by the key's equals() and hashcode() implementation)
	 * 
	 * @return a new FastaWriter; will never be null.  The returned writer is not thread-safe.
	 * 
	 * @param <K> The deconvolution key type that is returned from the deconvolution function and passed 
	 * to the supplier function.
	 * @param <F> The {@link FastaRecord} type that is being written out.
	 * 
	 * @param <W> the {@link FastaWriter} interface  type returned by this method.
	 * 
	 * @throw NullPointerException if any parameter is null (but the deconvolution function
	 * may return null values).
	 * 
	 * @apiNote For example to if there exists a method that given the read id, returns a Direction object
	 * of that read then the following code:
	 * 
	 * <pre>
	 * {@code NucleotideFastaWriter writer = SplitFastaWriter.deconvolve(NucleotideFastaWriter.class, 
					record-> getSequenceDirectionFor(record.getId()),
					dir -> new NucleotideFastaWriterBuilder(new File(outputDir, dir + ".fasta"))
											.build());
		}
	 * </pre>
	 * 
	 * Will write out all the fastas to 2 files, "forward.fasta" and "reverse.fasta" where the reads
	 * that were determined to be forward reads were written to "forward.fasta" and the reads that were
	 * determined to be reversed where written to "reverse.fasta".
	 */
	@SuppressWarnings("unchecked")
	public static <S, T extends Sequence<S>, F extends FastaRecord<S, T>, W extends FastaWriter<S,T,F>, K> W deconvolve(Class<W> interfaceClass, 
			Function<FastaRecord<S, T>, K> deconvolutionFunction,
			DeconvolveFastaRecordWriterFactory<K, W> supplier){
		DeconvolutionFastaWriter<S, T, F, W, K> writer = new DeconvolutionFastaWriter<S, T, F, W, K>(deconvolutionFunction, supplier);
		
		return (W) Proxy.newProxyInstance(writer.getClass().getClassLoader(), new Class[]{interfaceClass}, new InvocationHandlerImpl<>(writer) );
	} 
	
	private SplitFastaWriter(){
		//can not instantiate
	}
	
	private static final class FastaRecordImpl<S, T extends Sequence<S>> implements FastaRecord<S,T>{
		private final String id, comment;
		private final T sequence;
		
		
		public FastaRecordImpl(String id, String comment, T sequence) {
			this.id = id;
			this.comment = comment;
			this.sequence = sequence;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getComment() {
			return comment;
		}

		@Override
		public T getSequence() {
			return sequence;
		}
		
	}
	
	
	private static final class InvocationHandlerImpl<S, T extends Sequence<S>, F extends FastaRecord<S, T>, W extends FastaWriter<S,T,F>> implements InvocationHandler{

		private final FastaWriter<S,T,F> delegate;
		
		@SuppressWarnings("unchecked")
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			
			String name = method.getName();
			if("write".equals(name)){
				//special handling of write method calls
				//subclasses override with more specific subtypes
				//which causes reflection problems so need to do explicit casting
				if(args.length ==1){
					//write full record
					delegate.write((F) args[0]);
					return null;
				}else if(args.length ==2){
					delegate.write((String) args[0], (T) args[1]);
					return null;
				}else if(args.length ==3){
					delegate.write((String) args[0], (T) args[1], (String) args[2]);
					return null;
				}
			}
			
			return method.invoke(delegate, args);
			
		}

		public InvocationHandlerImpl(FastaWriter<S,T,F> delegate) {
			this.delegate = delegate;
		}
		
	}
	
	private static final class 	DeconvolutionFastaWriter<S, T extends Sequence<S>, F extends FastaRecord<S, T>, W extends FastaWriter<S, T, F>, K>
	implements FastaWriter<S, T, F> {

		private volatile boolean closed = false;
		
		private final Map<K, W> writers = new HashMap<>();
		
		private final Function<FastaRecord<S, T>, K> deconvolutionFunction;
		private final DeconvolveFastaRecordWriterFactory<K, W> supplier;
		
		
		public DeconvolutionFastaWriter(Function<FastaRecord<S, T>, K> deconvolutionFunction, DeconvolveFastaRecordWriterFactory<K, W> supplier) {
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
			for(W writer : writers.values()){
				IOUtil.closeAndIgnoreErrors(writer);
			}
					
			
		}
		private void checkNotClosed() throws IOException {
			if (closed) {
				throw new IOException("already closed");
			}
		}
		
		@Override
		public void write(F record) throws IOException {
			privateWrite(record);
		}

		private void privateWrite(FastaRecord<S,T> record) throws IOException {
			checkNotClosed();
			Objects.requireNonNull(record);
			K key = deconvolutionFunction.apply(record);
			if(key ==null){
			    //skip record
			    return;
			}
			//since we might throw an IOException
			//if we call the supplier
			//we do a check then act
			//which is not threadsafe
			//but this writer isn't threadsafe anyway
			//so I don't think it matters.
			//if we used computIfAbsent(key, supplier)
			//we would have to wrap any exception thrown by supplier
			//in a runtime exception
			W writer = writers.get(key);
			if(writer==null){
			    writer = supplier.create(key);
			    writers.put(key, writer);
			}
			//use this version of write()
			//so we don't have to create a mock
			//implementation of fastaRecord type F
			//which adds a lot of  by requiring
			//Proxy classes and reflection calls
			writer.write(record.getId(), record.getSequence(), record.getComment());
		}
		
	

		@Override
		public void write(String id, T sequence) throws IOException {
			write(id, sequence, null);
			
		}

		@Override
		public void write(String id, T sequence, String optionalComment)
				throws IOException {
			Objects.requireNonNull(id, "id can not be null");
			Objects.requireNonNull(sequence, "sequence can not be null");
			
			FastaRecordImpl<S, T> record = new FastaRecordImpl<>(id, optionalComment, sequence);
			
			privateWrite(record);
		}
		
	}
	private static final class RolloverSplitFastaWriter<S, T extends Sequence<S>, F extends FastaRecord<S, T>, W extends FastaWriter<S, T, F>>
			implements FastaWriter<S, T, F> {

		private final FastaRecordWriterFactory<W> supplier;

		private final int recordsPerFile;
		//initialized to 0 because we will increment it to 1
		//the first time something is written
		private int splitFileNumber = 0;
		private int currentRecordCount;
		private W currentWriter;

		private volatile boolean closed = false;

		private RolloverSplitFastaWriter(int recordsPerFile,
				FastaRecordWriterFactory<W> supplier) {

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
		public void write(F record) throws IOException {
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
		public void write(String id, T sequence) throws IOException {
			updateCurrentWriterIfNeeded();
			currentWriter.write(id, sequence);
			currentRecordCount++;

		}

		@Override
		public void write(String id, T sequence, String optionalComment)
				throws IOException {
			updateCurrentWriterIfNeeded();
			currentWriter.write(id, sequence, optionalComment);
			currentRecordCount++;
		}
	}
	
	private static final class RoundRobinSplitFastaWriter<S, T extends Sequence<S>, F extends FastaRecord<S, T>, W extends FastaWriter<S, T, F>>
			implements FastaWriter<S, T, F> {

		private final FastaRecordWriterFactory<W> supplier;

		private int currentIndex=0;
		

		private volatile boolean closed = false;

		
		private final FastaWriter<S, T, F>[] writers;
		
		@SuppressWarnings("unchecked")
		private RoundRobinSplitFastaWriter(int numberOfFiles,
				FastaRecordWriterFactory<W> supplier) {

			Objects.requireNonNull(supplier);
			if (numberOfFiles < 1) {
				throw new IllegalArgumentException(
						"records per File must be >=1");
			}
			writers = new FastaWriter[numberOfFiles];
			
			this.supplier = supplier;
			
		}

		private void checkNotClosed() throws IOException {
			if (closed) {
				throw new IOException("already closed");
			}
		}
		
		private W getCurrentWriter() throws IOException{
			checkNotClosed();
			@SuppressWarnings("unchecked")
			W writer=  (W)writers[currentIndex];
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
				for(FastaWriter<S, T, F> writer : writers){
					IOUtil.closeAndIgnoreErrors(writer);
				}
			}
			closed = true;
		}

		@Override
		public void write(F record) throws IOException {
			getCurrentWriter().write(record);
		}

		

		@Override
		public void write(String id, T sequence) throws IOException {
			getCurrentWriter().write(id, sequence);

		}

		@Override
		public void write(String id, T sequence, String optionalComment)
				throws IOException {
			getCurrentWriter().write(id, sequence, optionalComment);
		}
	}
}
