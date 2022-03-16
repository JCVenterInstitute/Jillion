package org.jcvi.jillion.core.residue.nt;

import java.util.stream.Collector;

/**
 * Utility class of different {@link Collector}s that can collect
 * items into a {@link NucleotideSequence}.
 * @author dkatzel
 * 
 * @since 6.0
 *
 */
public final class NucleotideSequenceCollectors {

	private NucleotideSequenceCollectors() {
		//can not instantiate
	}
	
	
	private static class NucleotideSequenceCombiner{
		private NucleotideSequenceBuilder builder;
		
		public NucleotideSequenceCombiner() {
			this.builder = new NucleotideSequenceBuilder()
								.turnOffDataCompression(true);
		}
		public NucleotideSequenceCombiner(int capacity) {
			this.builder = new NucleotideSequenceBuilder(capacity)
								.turnOffDataCompression(true);
		}
		
		public NucleotideSequenceCombiner(NucleotideSequence reference, int offset) {
			this.builder = new NucleotideSequenceBuilder()
								.setReferenceHint(reference, offset)
								.turnOffDataCompression(true);
		}
		
		public NucleotideSequence build() {
			return builder.build();
		}
		
		public ReferenceMappedNucleotideSequence buildReferenceEncodedNucleotideSequence() {
			return builder.buildReferenceEncodedNucleotideSequence();
		}
		
		public NucleotideSequenceCombiner append(NucleotideSequence s) {
			builder.append(s);
			return this;
		}
		public NucleotideSequenceCombiner append(String s) {
			builder.append(s);
			return this;
		}
		
		public NucleotideSequenceCombiner merge(NucleotideSequenceCombiner other) {
			builder.append(other.builder);
			return this;
		}
	}
	/**
	 * Collect a Stream of Strings into a combined {@link NucleotideSequence} where the Strings 
	 * are appended together into a giant sequence.
	 * @return a Collector that makes a {@link NucleotideSequence}.
	 */
	public static Collector<String, ?, NucleotideSequence>  toSequenceFromStrings() {
		return Collector.of(NucleotideSequenceCombiner::new, 
				NucleotideSequenceCombiner::append, 
				NucleotideSequenceCombiner::merge, 
				NucleotideSequenceCombiner::build
				
				);
	}
	/**
	 * Collect a Stream of Strings into a combined {@link NucleotideSequence} where the Strings 
	 * are appended together into a giant sequence.
	 * @param capacity the initial capacity for the internal {@link NucleotideSequenceBuilder}
	 * used by the Collector, this is used to initialize a backing array and is only to improve
	 * performance if the sequence is long.
	 * @return a Collector that makes a {@link NucleotideSequence}.
	 */
	public static Collector<String, ?, NucleotideSequence>  toSequenceFromStrings(int capacity) {
		return Collector.of(()-> new NucleotideSequenceCombiner(capacity), 
				NucleotideSequenceCombiner::append, 
				NucleotideSequenceCombiner::merge, 
				NucleotideSequenceCombiner::build
				
				);
	}
	/**
	 * Collect a Stream of {@link NucleotideSequence}s into a combined {@link NucleotideSequence} where the sequences 
	 * are appended together into a giant sequence.
	 * @return a Collector that makes a {@link NucleotideSequence}.
	 */
	public static Collector<NucleotideSequence, ?, NucleotideSequence>  toSequence() {
		return Collector.of(NucleotideSequenceCombiner::new, 
				NucleotideSequenceCombiner::append, 
				NucleotideSequenceCombiner::merge, 
				NucleotideSequenceCombiner::build
				
				);
	}
	
	/**
	 * Collect a Stream of {@link NucleotideSequence}s into a combined {@link NucleotideSequence} where the sequences 
	 * are appended together into a giant sequence.
	 * @param reference the {@link NucleotideSequence} to use as a reference; can not be null.
	 * @param gappedStartOffset the starting offset that this sequence will be if aligned to the provided reference
	 * performance if the sequence is long.
	 * @return a Collector that makes a {@link NucleotideSequence}.
	 * @throws NullPointerException if reference is null.
	 * @throws IllegalArgumentException if gappedStartOffset is &lt; 0.
	 */
	public static Collector<NucleotideSequence, ?, NucleotideSequence>  toReferenceMappedNucleotideSequence(NucleotideSequence reference, int gappedStartOffset) {
		return Collector.of(()->  new NucleotideSequenceCombiner(reference, gappedStartOffset), 
				NucleotideSequenceCombiner::append, 
				NucleotideSequenceCombiner::merge, 
				NucleotideSequenceCombiner::buildReferenceEncodedNucleotideSequence
				
				);
	}
	/**
	 * Collect a Stream of Strings into a combined {@link NucleotideSequence} where the sequences 
	 * are appended together into a giant sequence.
	 * @param reference the {@link NucleotideSequence} to use as a reference; can not be null.
	 * @param gappedStartOffset the starting offset that this sequence will be if aligned to the provided reference
	 * performance if the sequence is long.
	 * @return a Collector that makes a {@link NucleotideSequence}.
	 * @throws NullPointerException if reference is null.
	 * @throws IllegalArgumentException if gappedStartOffset is &lt; 0.
	 */
	public static Collector<String, ?, NucleotideSequence>  toReferenceMappedNucleotideSequenceFromStrings(NucleotideSequence reference, int gappedStartOffset) {
		return Collector.of(()->  new NucleotideSequenceCombiner(reference, gappedStartOffset), 
				NucleotideSequenceCombiner::append, 
				NucleotideSequenceCombiner::merge, 
				NucleotideSequenceCombiner::buildReferenceEncodedNucleotideSequence
				
				);
	}
	/**
	 * Collect a Stream of {@link NucleotideSequence}s into a combined {@link NucleotideSequence} where the sequences 
	 * are appended together into a giant sequence.
	 * @param capacity the initial capacity for the internal {@link NucleotideSequenceBuilder}
	 * used by the Collector, this is used to initialize a backing array and is only to improve
	 * performance if the sequence is long.
	 * @return a Collector that makes a {@link NucleotideSequence}.
	 */
	public static Collector<NucleotideSequence, ?, NucleotideSequence> toSequence(int capacity) {
		return Collector.of(()-> new NucleotideSequenceCombiner(capacity), 
				NucleotideSequenceCombiner::append, 
				NucleotideSequenceCombiner::merge, 
				NucleotideSequenceCombiner::build
				
				);
	}
}
