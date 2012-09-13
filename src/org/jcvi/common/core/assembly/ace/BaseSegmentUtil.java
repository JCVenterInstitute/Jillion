package org.jcvi.common.core.assembly.ace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.common.core.util.MapValueComparator;
import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.PeekableIterator;
import org.jcvi.common.core.util.iter.PeekableStreamingIterator;
/**
 * Utility class for working with {@link AceBaseSegment}s.
 * @author dkatzel
 *
 */
public final class BaseSegmentUtil {
	
	private BaseSegmentUtil(){
		//can not instantiate
	}
	/**
	 * Compute the {@link AceBaseSegment}s for the given contig
	 * @param contig
	 * @return
	 * @throws NoReadMatchesConsensusException if there no aligned
	 * read has an exact basecall match to the consensus.  This
	 * can often happen when the consensus has ambiguity values
	 * and the underlying sequences are the bases
	 * that make up the ambiguity. 
	 */
	public static List<AceBaseSegment> computeBestSegmentsFor(AceContig contig){
		List<AceBaseSegment> baseSegments = new ArrayList<AceBaseSegment>();
		NucleotideSequence consensus =contig.getConsensusSequence();
		PeekableIterator<Nucleotide> consensusIterator = IteratorUtil.createPeekableIterator(consensus.iterator());
		PeekableStreamingIterator<AceAssembledRead> readIter = IteratorUtil.createPeekableStreamingIterator(contig.getReadIterator());
		
		SortedMap<String,Range> sortedReadRanges = createSortedRangeMapFor(readIter,0);			
		Nucleotide consensusBase = consensusIterator.peek();
		CurrentMatchingRead currentMatchingRead = findFirstReadThatMatchesConsensus(contig, sortedReadRanges,consensusBase);

		long consensusOffsetToBeCovered = computeNextBaseSegment(baseSegments,
													0, 
													consensusIterator,
													currentMatchingRead);
		while(consensusIterator.hasNext()){
			consensusBase = consensusIterator.peek();
			sortedReadRanges = createSortedRangeMapFor(readIter,consensusOffsetToBeCovered,sortedReadRanges);
			currentMatchingRead = findReadThatMatchesConsensus(contig, sortedReadRanges,consensusBase,consensusOffsetToBeCovered);
			
			consensusOffsetToBeCovered = computeNextBaseSegment(baseSegments,
													consensusOffsetToBeCovered, 
													consensusIterator,
													currentMatchingRead);
		}
		return baseSegments;
	}
	private static long computeNextBaseSegment(
			List<AceBaseSegment> bestSegments, long consensusOffsetToBeCovered,
			PeekableIterator<Nucleotide> consensusIterator,
			CurrentMatchingRead currentMatchingRead) {
		Nucleotide consensusBase;
		boolean stillMatches=true;
		while(consensusIterator.hasNext() && currentMatchingRead.getBaseIterator().hasNext() && stillMatches){
			
			consensusBase = consensusIterator.peek();
			Nucleotide readBase = currentMatchingRead.getBaseIterator().next();
			stillMatches = consensusBase ==readBase;	
			if(stillMatches){
				consensusOffsetToBeCovered++;
				consensusIterator.next();
			}
		}
		//here we have a current matching read that no longer matches
		bestSegments.add(new DefaultAceBaseSegment(currentMatchingRead.getRead().getId(), 
				Range.create(currentMatchingRead.getStartMatchOffset(), consensusOffsetToBeCovered-1)));
		return consensusOffsetToBeCovered;
	}

	private static SortedMap<String, Range> createSortedRangeMapFor(
			PeekableStreamingIterator<AceAssembledRead> readIter, long offset,
			SortedMap<String, Range> sortedReadRanges) {
		//remove any reads that no longer provide coverage
		//this map should be sorted by end offset
		//so if we see a read that still covers
		//then the rest of the reads do too.
		//this is an optimization over having to compute
		//and then iterate over a coverage map and slice map for each slice
		Iterator<Entry<String, Range>> iter = sortedReadRanges.entrySet().iterator();
		//our new map
		Map<String, Range> map = new LinkedHashMap<String, Range>();
		while(iter.hasNext()){
			Entry<String, Range> entry =iter.next();
			if(entry.getValue().getEnd()<offset){
				iter.remove();
			}else{
				map.put(entry.getKey(),entry.getValue());
			}
		}
		boolean done=false;
		
		while(readIter.hasNext() && !done){
			//we peek incase we get to a read that starts beyond
			//our current consensus offset
			AceAssembledRead currentRead = readIter.peek();
			if(currentRead.getGappedStartOffset()<=offset){
				readIter.next();
				map.put(currentRead.getId(), currentRead.asRange());
			}else{
				done=true;
			}
		}
		
		return MapValueComparator.sortAscending(map, Range.Comparators.DEPARTURE);
	}

	private static CurrentMatchingRead findFirstReadThatMatchesConsensus(AceContig contig,
			SortedMap<String, Range> sortedReadRanges,
			Nucleotide consensusBase) {
		return findReadThatMatchesConsensus(contig, sortedReadRanges, consensusBase, 0);
	}
	private static CurrentMatchingRead findReadThatMatchesConsensus(AceContig contig,
			SortedMap<String, Range> sortedReadRanges,
			Nucleotide consensusBase, long consensusOffset) {
		Iterator<String> idIterator = sortedReadRanges.keySet().iterator();
		boolean foundMatch=false;
		AceAssembledRead currentBestRead=null;
		while(!foundMatch && idIterator.hasNext()){
			String id = idIterator.next();
			AceAssembledRead read = contig.getRead(id);
			ReferenceMappedNucleotideSequence readSequence =read.getNucleotideSequence();
			long gappedStartOffset = read.getGappedStartOffset();
			long readOffset = consensusOffset-gappedStartOffset;
			if(readSequence.getLength()>readOffset){
				Nucleotide base =readSequence.get(readOffset);
				if(base ==consensusBase){
					foundMatch=true;
					currentBestRead = read;
				}
			}
		}
		if(currentBestRead==null){
			throw new NoReadMatchesConsensusException(consensusBase,consensusOffset);
		}
		Range readRange = Range.create(consensusOffset- currentBestRead.getGappedStartOffset(), currentBestRead.getGappedEndOffset()- currentBestRead.getGappedStartOffset());
		return new CurrentMatchingRead(currentBestRead, 
				currentBestRead.getNucleotideSequence().iterator(readRange),
				consensusOffset);
	}
	private static SortedMap<String, Range> createSortedRangeMapFor(
			PeekableStreamingIterator<AceAssembledRead> readIter, long offset) {
		return createSortedRangeMapFor(readIter, offset, new TreeMap<String, Range>());
	}
	
	public static class NoReadMatchesConsensusException extends IllegalStateException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2738190645917425718L;

		NoReadMatchesConsensusException(Nucleotide consensusBase, long offset){
			super(String.format("consensus ='%s' at offset %d",consensusBase, offset));
		}
	}
	
	private static class CurrentMatchingRead{
		private final AceAssembledRead read;
		private final Iterator<Nucleotide> baseIterator;
		private final long startMatchOffset;
		
		public CurrentMatchingRead(AceAssembledRead read,
				Iterator<Nucleotide> baseIterator,
				long startMatchOffset) {
			this.read = read;
			this.baseIterator = baseIterator;
			this.startMatchOffset = startMatchOffset;
		}
		protected final AceAssembledRead getRead() {
			return read;
		}
		protected final Iterator<Nucleotide> getBaseIterator() {
			return baseIterator;
		}
		protected final long getStartMatchOffset() {
			return startMatchOffset;
		}
		
		
	}
}
