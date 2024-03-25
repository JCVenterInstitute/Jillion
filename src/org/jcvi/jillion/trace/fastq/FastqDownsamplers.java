package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.IntFunction;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.PairedFastqVisitor.PairedFastqVisitorCallback;

import lombok.Data;
/**
 * {@link FastqDownsampler} implementations.
 * 
 * @author dkatzel
 * @since 6.0.2
 */
public final class FastqDownsamplers {
	
	private static final double DEFAULT_S_LEAP_PROPORTION = 0.005D;
	/**
	 * Use reservoir sampling algorithm to create 
	 * a {@link FastqDownsampler} that will output fastq files
	 * of at most {@code reservoirSize} records.
	 * 
	 * @implNote this is an implementation of Knuth's Algorithm R in Vol 2 of 
	 * The Art Of Computer Programming, chapter {@code 3.4.2}.  
	 * 
	 * @param reservoirSize the maximum number of records to include in the output
	 * fastq file; must be positive.
	 * 
	 * @return a new {@link FastqDownsampler}; will never be null.
	 * 
	 * @throws IllegalArgumentException if reservoirSize is &le; 0.
	 */
	public static FastqDownsampler reservoir(int reservoirSize) {
		return reservoir(reservoirSize, new Random());
	}
	/**
	 * Use reservoir sampling algorithm to create 
	 * a {@link FastqDownsampler} that will output fastq files
	 * of at most {@code reservoirSize} records.
	 * 
	 * @implNote this is an implementation of Knuth's Algorithm R in Vol 2 of 
	 * The Art Of Computer Programming, chapter {@code 3.4.2}.  
	 * 
	 * @param reservoirSize the maximum number of records to include in the output
	 * fastq file; must be positive.
	 * @param random the {@link Random} instance to use; can not be null.
	 * 
	 * @return a new {@link FastqDownsampler}; will never be null.
	 * 
	 * @throws IllegalArgumentException if reservoirSize is &le; 0.
	 * @throws NullPointerException if random is null.
	 */
	public static FastqDownsampler reservoir(int reservoirSize, Random random) {
		return new ReservoirWriter(reservoirSize, random);
	}
	/**
	 * Use the "S-leaping" algorithm.
	 * @param reservoirSize the maximum number of records to include in the output
	 * fastq file; must be positive.
	 * @param leapSize
	 * @param random the {@link Random} instance to use; can not be null.
	 * 
	 * @return a new {@link FastqDownsampler}; will never be null.
	 * 
	 * @throws IllegalArgumentException if either reservoirSize or leapSize are &le; 0.
	 * @throws NullPointerException if random is null.
	 * 
	 * @see Hiroyuki Kuwahara, Xin Gao, S-leaping: an efficient downsampling method for large high-throughput sequencing data,
	 *  Bioinformatics, Volume 39, Issue 7, July 2023, btad399, <a href="https://doi.org/10.1093/bioinformatics/btad399"</a>
	 */
	public static FastqDownsampler sLeap(int reservoirSize) {
		return sLeap(reservoirSize, Math.max( 1, (int) DEFAULT_S_LEAP_PROPORTION *reservoirSize));
	}
	/**
	 * Use the "S-leaping" algorithm.
	 * @param reservoirSize the maximum number of records to include in the output
	 * fastq file; must be positive.
	 * @param leapSize
	 * @param random the {@link Random} instance to use; can not be null.
	 * 
	 * @return a new {@link FastqDownsampler}; will never be null.
	 * 
	 * @throws IllegalArgumentException if either reservoirSize or leapSize are &le; 0.
	 * @throws NullPointerException if random is null.
	 * 
	 * @see Hiroyuki Kuwahara, Xin Gao, S-leaping: an efficient downsampling method for large high-throughput sequencing data,
	 *  Bioinformatics, Volume 39, Issue 7, July 2023, btad399, <a href="https://doi.org/10.1093/bioinformatics/btad399"</a>
	 */
	public static FastqDownsampler sLeap(int reservoirSize, int leapSize) {
		return sLeap(reservoirSize, leapSize, new Random());
	}
	/**
	 * Use the "S-leaping" algorithm.
	 * @param reservoirSize the maximum number of records to include in the output
	 * fastq file; must be positive.
	 * @param leapSize 
	 * 
	 * @return a new {@link FastqDownsampler}; will never be null.
	 * 
	 * @throws IllegalArgumentException if either reservoirSize or leapSize are &le; 0.
	 * @throws NullPointerException if random is null.
	 * 
	 * @see Hiroyuki Kuwahara, Xin Gao, S-leaping: an efficient downsampling method for large high-throughput sequencing data,
	 *  Bioinformatics, Volume 39, Issue 7, July 2023, btad399, <a href="https://doi.org/10.1093/bioinformatics/btad399"</a>
	 */
	public static FastqDownsampler sLeap(int reservoirSize, int leapSize, Random random) {
		return new SLeapWriter(reservoirSize, leapSize, random);
	}
	
	private static interface State{
		
		HandleResult handleNext();
		
	}
	
	@Data
	public static class HandleResult{
		private final State nextState;
		private final OptionalInt reservoirOffset;
	}
	
	
	private static class AlgorithmR implements DownsampleImpl{
		private final Random random;
		
		private State currentState;
		private final int reservoirSize;
		
		public AlgorithmR(int reservoirSize, Random random) {
			this.random = random;
			this.reservoirSize = reservoirSize;
			this.currentState = new InitialState();
			
		}
		
		
		
		
		
		@Override
		public OptionalInt getNextReservoir() {
			HandleResult result = currentState.handleNext();
			currentState = result.getNextState();
			
			return result.getReservoirOffset();
		}





		public class RandomDownSampleState implements State{
			private int readsSeenSoFar;
			
			public RandomDownSampleState() {
				this.readsSeenSoFar = reservoirSize-1;
			}
			
			public HandleResult handleNext() {
				int r = random.nextInt(++readsSeenSoFar);
				if(r < reservoirSize) {
					return new HandleResult(
					this,
					OptionalInt.of(r));
				}
				//skip
				return new HandleResult(this, OptionalInt.empty());
			}
			
			
		}
		
		public class InitialState implements State{
			
			private int arraySizePopulated=0;
			
			public HandleResult handleNext() {
				
				if(arraySizePopulated< reservoirSize) {
					int r = arraySizePopulated++;
					return new HandleResult(this, OptionalInt.of(r));
				}
				
				return new RandomDownSampleState().handleNext();
			}
			 
			
		}
		
		
	}
	private static class DownsampleVisitor implements FastqVisitor{

		private final FastqWriter delegate;

		private int arraySizePopulated=0;
		
		private final FastqRecord[] reservoir;
		
		private DownsampleImpl downsampler;
		private FastqQualityCodec codec;
		
		public DownsampleVisitor(int reservoirSize, FastqQualityCodec codec,FastqWriter delegate,IntFunction<DownsampleImpl> downSamplerFactory) {

			this.codec= codec;
			this.delegate = delegate;
			this.reservoir = new FastqRecord[reservoirSize];
			this.downsampler = downSamplerFactory.apply(reservoirSize);
			
		}
	
		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback callback, String id, String optionalComment) {
			
			FastqRecordVisitor visitor[] = new FastqRecordVisitor[1];
			downsampler.getNextReservoir().ifPresent(r->{
				visitor[0] = new AbstractFastqRecordVisitor(id, optionalComment, codec) {
					
					@Override
					protected void visitRecord(FastqRecord record) {
						reservoir[r] = record;
						arraySizePopulated++;
					}
				};
			});
			return visitor[0];
			
		}

		@Override
		public void visitEnd() {
			try {
				delegate.write(reservoir, 0, Math.min(reservoir.length, arraySizePopulated));
			} catch (IOException e) {
				//no-op
			}
			Arrays.fill(reservoir, null);
		}

		@Override
		public void halted() {
			//no-op ? should we close?  
			
		}
	}
	
	private static class PairedDownsampleVisitor implements PairedFastqVisitor{

		private final FastqWriter delegate1, delegate2;

		private int arraySizePopulated=0;
		
		private final FastqRecord[][] reservoir;
		
		private DownsampleImpl downsampler;
		private FastqQualityCodec codec;
		
		public PairedDownsampleVisitor(int reservoirSize, FastqQualityCodec codec,
				FastqWriter delegate1, FastqWriter delegate2,IntFunction<DownsampleImpl> downSamplerFactory) {

			this.codec= codec;
			this.delegate1 = delegate1;
			this.delegate2 = delegate2;
			
			this.reservoir = new FastqRecord[2][reservoirSize];
			this.downsampler = downSamplerFactory.apply(reservoirSize);
			
		}
	
		@Override
		public PairedFastqRecordVisitor visitDefline(PairedFastqVisitorCallback callback, String id,
				String optionalComment) {
			PairedFastqRecordVisitor visitor[] = new PairedFastqRecordVisitor[1];
			downsampler.getNextReservoir().ifPresent(r->{
				visitor[0] = new AbstractPairedFastqRecordVisitor(id, optionalComment, codec, true){

					@Override
					protected void visitRecordPair(FastqRecord read1Record, FastqRecord read2Record) {
						reservoir[0][r]=read1Record;
						reservoir[1][r]=read2Record;
						
					}
				};
			}
				
				
			);
			return visitor[0];
		}

		
		@Override
		public void visitEnd() {
			int length = Math.min(reservoir.length, arraySizePopulated);
			try {
				delegate1.write(reservoir[0], 0, length);
			} catch (IOException e) {
				//no-op
			}
			try {
				delegate2.write(reservoir[1], 0, length);
			} catch (IOException e) {
				//no-op
			}
			Arrays.fill(reservoir[0], null);
			Arrays.fill(reservoir[1], null);
		}

		@Override
		public void halted() {
			//no-op ? should we close?  
			
		}
	}
	
	
	interface DownsampleImpl{
		OptionalInt getNextReservoir();
	}
	private static class SLeap implements DownsampleImpl {

		
		private int arraySizePopulated=0;
		
		private Random random;
		
		private State currentState;
		private final int leapSize;
		private final int reservoirSize;
		
		public SLeap(int reservoirSize, int leapSize, Random random) {
			this.random = random;
			this.leapSize = leapSize;
			this.currentState = new InitialState();
			this.reservoirSize = reservoirSize;
			
		}
		
		public OptionalInt getNextReservoir() {
			HandleResult result = currentState.handleNext();
			currentState = result.getNextState();
			return result.getReservoirOffset();
		}
		
		
		
		
		
		public class InitialState implements State{
			
		
			
			public HandleResult handleNext() {
				
				if(arraySizePopulated< reservoirSize) {
					int r = arraySizePopulated++;
					return new HandleResult(this, OptionalInt.of(r));
				}
				
				return new SecondPassOfReservoirState().handleNext();
			}
			 
			
		}
		public class SecondPassOfReservoirState implements State{
			
			private int secondPassCount=0;
			
			public HandleResult handleNext() {
				
				if(secondPassCount< reservoirSize) {
					int r = random.nextInt(reservoirSize + (++secondPassCount));
					if(r < reservoirSize) {
						return new HandleResult(this, OptionalInt.of(r));
					}
					//skip
					return new HandleResult( this, OptionalInt.empty());
				}
				
				return new SLeapState().handleNext();
			}
			 
			
		}
		
		public class SLeapState implements State{
			
			private long recordsVisitedSoFar;
			private int offset=0;
			private double p;
			private double lambda;
			private int nextSteps;
			private int skipCount;
			private boolean inSkip=false;
			private boolean includeNextRecord=false;
			private boolean reComputeLambda=false;
			public SLeapState() {
				recordsVisitedSoFar= 2*reservoirSize;
				reComputeLambda=true;
			}
			
			
			public HandleResult handleNext() {
				if(inSkip && skipCount>0) {
					skipCount--;
					recordsVisitedSoFar++;
					return new HandleResult(this, OptionalInt.empty());
				}
				inSkip=false;
				if(includeNextRecord) {
					int r = random.nextInt(reservoirSize);
					includeNextRecord=false;
					return new HandleResult(this, OptionalInt.of(r));
					
				}
				
				if(reComputeLambda) {
					p = reservoirSize/ (double)(recordsVisitedSoFar+(leapSize*2)+1);
					lambda = Math.log(1.0 - p);
					reComputeLambda=false;
				}
				nextSteps = (int)(Math.log(random.nextDouble()) /lambda) + 1;
				offset+=nextSteps;
				skipCount=0;
				inSkip=true;
				if(offset > leapSize -1) {
					
					skipCount = leapSize+nextSteps-offset;
					includeNextRecord=false;
					offset=0;
				}else {
					skipCount=nextSteps;
					includeNextRecord=true;
				}
				return handleNext();
				
			}
			 
			
		}
		
		
	}
	
	
	private static class ReservoirWriter implements FastqDownsampler{
		
		
		private final int reserviorSize;
		private Random random;
		
		ReservoirWriter( int reserviorSize, Random random){
			this.random = Objects.requireNonNull(random);
			
			if(reserviorSize <1) {
				throw new IllegalArgumentException("reservoir size must be >=0");
			}
			this.reserviorSize = reserviorSize;
		}

		
		
		
		@Override
		public void downsample(FastqParser parser, FastqQualityCodec codec, FastqWriter delegate) throws IOException {
			Objects.requireNonNull(delegate);
			parser.parse(new DownsampleVisitor(reserviorSize, Objects.requireNonNull(codec), delegate, 
					size -> new AlgorithmR(size, random)));
			
		}




		@Override
		public void downsamplePair(FastqParser read1FastqParser, FastqParser read2FastqParser, FastqQualityCodec codec,
				FastqWriter read1OutputWriter, FastqWriter read2OutputWriter) throws IOException {
			new PairedFastqFileParser((FastqFileParser)read1FastqParser, (FastqFileParser) read2FastqParser)
				.parse(new PairedDownsampleVisitor(reserviorSize, Objects.requireNonNull(codec), 
						read1OutputWriter, read2OutputWriter, size -> new AlgorithmR(size, random)));
			
		}
		
		
	}
	
	private static class SLeapWriter implements FastqDownsampler{
		
		
		private final int reserviorSize;
		private final int skipSize;
		private Random random;
		
		SLeapWriter( int reserviorSize, int skipSize, Random random){
			this.random = Objects.requireNonNull(random);
			
			if(reserviorSize <1) {
				throw new IllegalArgumentException("reservoir size must be >=0");
			}
			if(skipSize <1) {
				throw new IllegalArgumentException("skip size must be >=0");
			}
			this.reserviorSize = reserviorSize;
			this.skipSize = skipSize;
		}

		
		@Override
		public void downsample(FastqParser parser, FastqQualityCodec codec, FastqWriter delegate) throws IOException {
			Objects.requireNonNull(delegate);
			parser.parse(new DownsampleVisitor(reserviorSize, Objects.requireNonNull(codec), delegate, size -> new SLeap(size, skipSize, random) ));
			
		}
		@Override
		public void downsamplePair(FastqParser read1FastqParser, FastqParser read2FastqParser, FastqQualityCodec codec,
				FastqWriter read1OutputWriter, FastqWriter read2OutputWriter) throws IOException {
			new PairedFastqFileParser((FastqFileParser)read1FastqParser, (FastqFileParser) read2FastqParser)
				.parse(new PairedDownsampleVisitor(reserviorSize, Objects.requireNonNull(codec), 
						read1OutputWriter, read2OutputWriter, size -> new SLeap(size, skipSize, random)));
			
		}

		
		
	}
}
