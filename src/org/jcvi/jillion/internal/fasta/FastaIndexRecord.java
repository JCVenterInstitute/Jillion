package org.jcvi.jillion.internal.fasta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
/**
 * Object representation of a single
 * record in a {@code fai} index file
 * that is used by Samtools.
 * 
 * @author dkatzel
 *
 * @since 5.1
 */
public final class FastaIndexRecord {

	private final long seqLength;
	private final long firstBaseOffset;
	private final int basesPerLine;
	private final int bytesPerLineIncludingEol;
	/**
	 * Constructor that takes the fields of a record
	 * in the order that they are listed in an fai file.
	 * 
	 * @param seqLength the length of a sequence in bases.
	 * 
	 * @param firstBaseOffset the offset (in bytes) to the first
	 * base of the sequence.  This is not to be confused with 
	 * the first byte offset of the defline.
	 * 
	 * @param basesPerLine the number of bases per line on a full line 
	 * (the final line may be shorter).
	 * 
	 * @param bytesPerLineIncludingEol the number of bytes per line including
	 * any line terminators.
	 * 
	 * @throws IllegalArgumentException if any fields are negative, or firstBaseOffset
	 * is determined to be before the end of a defline or
	 * if bytesPerLineIncludingEol < basesPerLine
	 */
	public FastaIndexRecord(long seqLength, long firstBaseOffset, int basesPerLine,
			int bytesPerLineIncludingEol) {
		
		
		if(seqLength <0){
			throw new IllegalArgumentException("seq length can not be negative");
		}
		//technically has to be a defline too so we can't start the sequence anywhere
		//less than 2 bytes into the file
		if(firstBaseOffset <2){
			throw new IllegalArgumentException("first base offset can not be negative");
		}
		if(basesPerLine <0){
			throw new IllegalArgumentException("bases per line can not be negative");
		}
		//even if there are all single line records these 2 will be equal
		if(bytesPerLineIncludingEol <basesPerLine){
			throw new IllegalArgumentException("bytes per line  can not be less than bases per line");
		}
		this.seqLength = seqLength;
		this.firstBaseOffset = firstBaseOffset;
		this.basesPerLine = basesPerLine;
		this.bytesPerLineIncludingEol = bytesPerLineIncludingEol;
	}
	
	public long computeFileOffset(long sequenceOffset){
		if(sequenceOffset <0){
			throw new IllegalArgumentException("seq offset must be >=0");
		}
		if(sequenceOffset >= seqLength){
			throw new IllegalArgumentException("seq offset " + sequenceOffset + "must be < sequence length " + seqLength);
		}
		//the offset is computed by home many lines we skip + how much into the last line we are
		//parenthesis aren't needed but makes it more clear
		return firstBaseOffset + (sequenceOffset/basesPerLine * bytesPerLineIncludingEol) + sequenceOffset % basesPerLine;
	}
	
	public Range computeFileOffsetRange(Range sequenceOffsetRange){
		return Range.of(computeFileOffset(sequenceOffsetRange.getBegin()), 
						computeFileOffset(sequenceOffsetRange.getEnd()));
	}

	public InputStream newInputStream(File fastaFile) throws IOException{
		Range range = computeFileOffsetRange(Range.ofLength(seqLength));
		return new RandomAccessFileInputStream(fastaFile, firstBaseOffset, range.getLength());
	}
	
	public InputStream newInputStream(File fastaFile, Range sequenceRange) throws IOException{
		Range range = computeFileOffsetRange(sequenceRange);
		return new RandomAccessFileInputStream(fastaFile, range.getBegin(), range.getLength());
	}
	
	public InputStream newInputStream(File fastaFile, long sequenceStartOffset) throws IOException{
		Range range = new Range.Builder(seqLength)
								.contractBegin(sequenceStartOffset)
								.build();
		return newInputStream(fastaFile, range);
	}

	public long getSeqLength() {
		return seqLength;
	}
	
	
	
}
