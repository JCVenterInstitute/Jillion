package org.jcvi.jillion.profile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code ProfileWriterBuilder}
 * is a builder object that is used to configure
 * and create a {@link ProfileWriter}.
 * @author dkatzel
 *
 */
public class ProfileWriterBuilder {

	private final File outputFile;
	private final OutputStream outStream;
	
	private DisplayCountStrategy displayPercentages =DisplayCountStrategy.COUNTS;
	private final NucleotideSequence referenceOrConsensus;
	
	private MostFrequentTieBreakerRule tieBreakerRule = MostFrequentTieBreakerRule.LOWEST_ASCII;
	
	private boolean include0xEdges=true;
	private boolean ignoreGappedConsensusPositions = false;
	/**
	 * Create a new ProfileWriterBuilder that will use
	 * the given reference or consensus sequence as the profile
	 * reference (and length as the profile length) and write the
	 * resulting profile to the provided file.
	 * @param outputFile the output file to write the profile to;
	 * can not be null.
	 * @param referenceOrConsensus the reference sequence to use.
	 * the sequence's length is also used to determine the profile length.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the reference length is > {@link Integer#MAX_VALUE}.
	 */
	public ProfileWriterBuilder(File outputFile, NucleotideSequence referenceOrConsensus) {
		Objects.requireNonNull(outputFile);
		Objects.requireNonNull(referenceOrConsensus);
		
		verifySequenceLength(referenceOrConsensus);
		
		this.outputFile = outputFile;
		this.outStream = null;
		this.referenceOrConsensus = referenceOrConsensus;
	}
	/**
	 * Create a new ProfileWriterBuilder that will use
	 * the given reference or consensus sequence as the profile
	 * reference (and length as the profile length) and write the
	 * resulting profile to the provided {@link OutputStream}.
	 * @param out the {@link OutputStream} to write the profile to;
	 * can not be null.
	 * @param referenceOrConsensus the reference sequence to use.
	 * the sequence's length is also used to determine the profile length.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the reference length is > {@link Integer#MAX_VALUE}.
	 */
	public ProfileWriterBuilder(OutputStream out, NucleotideSequence referenceOrConsensus) {
		
		Objects.requireNonNull(out);
		Objects.requireNonNull(referenceOrConsensus);
		verifySequenceLength(referenceOrConsensus);
		this.outputFile = null;
		this.outStream = out;
		this.referenceOrConsensus = referenceOrConsensus;
	}
	
	private void verifySequenceLength(NucleotideSequence seq){
		if(seq.getLength() >Integer.MAX_VALUE){
			throw new IllegalArgumentException(
					String.format("reference sequence too long, max length is 2^31-1 but this is %d", seq.getLength()));
		}
	}

	/**
	 * Sets the {@link MostFrequentTieBreakerRule} instance to use
	 * to govern the "most frequent" column of the profile. If this
	 * method is not called, then by default {@link MostFrequentTieBreakerRule#LOWEST_ASCII}
	 * is used.
	 * @param tieBreakerRule the {@link MostFrequentTieBreakerRule} instance to use;
	 * can not be null.
	 * @return this.
	 * @throws NullPointerException if tieBreakerRule is null.
	 */
	public ProfileWriterBuilder setMostFrequentTieBreakerRule(MostFrequentTieBreakerRule tieBreakerRule){
		Objects.requireNonNull(tieBreakerRule);
		this.tieBreakerRule = tieBreakerRule;
		return this;
	}
	/**
	 * Should 0x regions at the edge of the profile be included in the output.
	 * If not called, by default this value is set to {@code true}.
	 * When comparing several profiles against the same reference
	 * it is important to include the edges even if 0x
	 * in order to more easily compare them.
	 * @param include0xEdges {@code true} if should be included;
	 * {@code false} otherwise.
	 * @return this.
	 */
	public ProfileWriterBuilder include0xEdges(boolean include0xEdges){
		this.include0xEdges = include0xEdges;
		return this;
	}
	/**
	 * Set the {@link DisplayCountStrategy} to use in the profile.
	 * If this method is not called, then by default {@link DisplayCountStrategy#COUNTS}
	 * is used.
	 * @param displayPercentages the {@link DisplayCountStrategy} to use; can not be null.
	 * @return this.
	 * @throws NullPointerException if parameter is null.
	 */
	public ProfileWriterBuilder displayPercentages(DisplayCountStrategy displayPercentages){
		this.displayPercentages = displayPercentages;
		return this;
	}
	/**
	 * Use the given configuration data provided so far
	 * to create a {@link ProfileWriter}.
	 * @return a new {@link ProfileWriter}.
	 * @throws IOException if the output file option is used
	 * and the file is unable to be created.
	 */
	public ProfileWriter build() throws IOException{
		if(outputFile ==null){
			//use stream
			return new SimpleProfileWriter(outStream, displayPercentages, tieBreakerRule, referenceOrConsensus, include0xEdges, ignoreGappedConsensusPositions);
		}
		return new SimpleProfileWriter(outputFile, displayPercentages, tieBreakerRule, referenceOrConsensus, include0xEdges, ignoreGappedConsensusPositions);
	}
	
	public ProfileWriterBuilder ignoreGappedConsensusPositions(boolean ignoreGappedConsensusPositions) {
		this.ignoreGappedConsensusPositions = ignoreGappedConsensusPositions;
		return this;
	}
}
