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
package org.jcvi.jillion.sam.transform;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.assembly.AssemblyTransformationService;
import org.jcvi.jillion.assembly.AssemblyTransformer;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.sam.SamParser;
import org.jcvi.jillion.sam.SamParser.SamParserOptions;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamRecordFilter;
import org.jcvi.jillion.sam.SamRecordFlag;
import org.jcvi.jillion.sam.SamRecordFlags;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.Cigar.ClipType;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamReferenceSequence;
/**
 * {@code SamTransformationService}
 * is a class that can parse a SAM file
 * and call the appropriate methods
 * on a given {@link AssemblyTransformer}
 * so the transformer can get assembly and alignment 
 * information from the SAM file without knowing
 * anything about how SAM files are formatted.
 * 
 * @author dkatzel
 *
 */
public final class SamTransformationService implements AssemblyTransformationService{

	private final NucleotideSequenceDataStore referenceDataStore;
	private final SamParser parser;
	private final SamRecordFilter filter;
	
	/**
	 * Create a new {@link SamTransformationService} using
	 * the given SAM encoded file and a fasta file of the ungapped
	 * references sequences referred to in the SAM.  The ids in the fasta file
	 * must match the reference sequence names in the SAM file (@SQ SN:$ID) in the SAM header.
	 * @param samFile the SAM file to parse and transform; can not be null
	 * and must exist.
	 * @param referenceFasta the reference fasta file; can not be null and must exist.
	 * @throws IOException if there is a problem parsing the input files.
	 * @throws NullPointerException if either parameter is null.
	 */
	public SamTransformationService(File samFile, File referenceFasta) throws IOException {
		
		this(samFile, referenceFasta, (SamRecordFilter) null, SamParserFactory.Parameters.builder().build());
	}
	/**
	 * Create a new {@link SamTransformationService} using
	 * the given SAM encoded file and a fasta file of the ungapped
	 * references sequences referred to in the SAM.  The ids in the fasta file
	 * must match the reference sequence names in the SAM file (@SQ SN:$ID) in the SAM header.
	 * @param samFile the SAM file to parse and transform; can not be null
	 * and must exist.
	 * @param referenceFasta the reference fasta file; can not be null and must exist.
	 * @param a filter of reads to include- if null, then no filter is applied.
	 * @throws IOException if there is a problem parsing the input files.
	 * @throws NullPointerException if either parameter is null.
	 * 
	 * @since 6.0
	 */
	public SamTransformationService(File samFile, File referenceFasta, Predicate<SamRecord> filter, SamParserFactory.Parameters parameters) throws IOException {
		
		parser = SamParserFactory.create(samFile, parameters);
		NucleotideFastaDataStore ungappedReferenceDataStore = new NucleotideFastaFileDataStoreBuilder(referenceFasta)
																	.build();
		this.filter = filter==null? null : SamRecordFilter.wrap(filter);
		referenceDataStore = SamGappedReferenceBuilderVisitor.createGappedReferencesFrom(parser, ungappedReferenceDataStore, this.filter);
	}
	/**
	 * Create a new {@link SamTransformationService} using
	 * the given SAM encoded file and a fasta file of the ungapped
	 * references sequences referred to in the SAM.  The ids in the fasta file
	 * must match the reference sequence names in the SAM file (@SQ SN:$ID) in the SAM header.
	 * @param samFile the SAM file to parse and transform; can not be null
	 * and must exist.
	 * @param referenceFasta the reference fasta file; can not be null and must exist.
	 * @param a filter of reads to include- if null, then no filter is applied.
	 * @throws IOException if there is a problem parsing the input files.
	 * @throws NullPointerException if either parameter is null.
	 * 
	 * @since 6.0
	 */
	public SamTransformationService(File samFile, File referenceFasta, Predicate<SamRecord> filter,
			Map<String, List<Range>> ungappedReferenceRanges,
			SamParserFactory.Parameters parameters) throws IOException {
		
		parser = SamParserFactory.create(samFile, parameters);
		NucleotideFastaDataStore ungappedReferenceDataStore = new NucleotideFastaFileDataStoreBuilder(referenceFasta)
																	.build();
		this.filter = filter==null? null : SamRecordFilter.wrap(filter);
		referenceDataStore = SamGappedReferenceBuilderVisitor.createGappedReferencesFrom(parser, ungappedReferenceDataStore, this.filter);
	}
	/**
	 * Create a new {@link SamTransformationService} using
	 * the given SAM encoded file and a fasta file of the ungapped
	 * references sequences referred to in the SAM.  The ids in the fasta file
	 * must match the reference sequence names in the SAM file (@SQ SN:$ID) in the SAM header.
	 * @param samFile the SAM file to parse and transform; can not be null
	 * and must exist.
	 * @param referenceFasta the reference fasta file; can not be null and must exist.
	 * @param a filter of reads to include- if null, then no filter is applied.
	 * @throws IOException if there is a problem parsing the input files.
	 * @throws NullPointerException if either parameter is null.
	 * 
	 * @since 6.0
	 */
	public SamTransformationService(File samFile, File referenceFasta,SamRecordFilter filter,
			SamParserFactory.Parameters parameters) throws IOException {
		
		parser = SamParserFactory.create(samFile, parameters);
		NucleotideFastaDataStore ungappedReferenceDataStore = new NucleotideFastaFileDataStoreBuilder(referenceFasta)
																	.build();
		this.filter = filter;
		if(filter!=null) {
			filter.ungappedReferenceDataStore(ungappedReferenceDataStore);
		}
		referenceDataStore = SamGappedReferenceBuilderVisitor.createGappedReferencesFrom(parser, ungappedReferenceDataStore, filter);
	}
	/**
	 * Create a new {@link SamTransformationService} using
	 * the given SAM encoded file and a fasta file of the ungapped
	 * references sequences referred to in the SAM.  The ids in the fasta file
	 * must match the reference sequence names in the SAM file (@SQ SN:$ID) in the SAM header.
	 * @param samFile the SAM file to parse and transform; can not be null
	 * and must exist.
	 * @param referenceFasta the reference fasta file; can not be null and must exist.
	 * @param a filter of reads to include- if null, then no filter is applied.
	 * @throws IOException if there is a problem parsing the input files.
	 * @throws NullPointerException if either parameter is null.
	 * 
	 * @since 6.0
	 */
	public SamTransformationService(File samFile, File referenceFasta,SamRecordFilter filter,  Map<String, List<Range>> ungappedReferenceRanges, SamParserFactory.Parameters parameters) throws IOException {
		
		parser = SamParserFactory.create(samFile, parameters);
		NucleotideFastaDataStore ungappedReferenceDataStore = new NucleotideFastaFileDataStoreBuilder(referenceFasta)
																	.build();
		this.filter = filter;
		if(filter!=null) {
			filter.ungappedReferenceDataStore(ungappedReferenceDataStore);
		}
		referenceDataStore = SamGappedReferenceBuilderVisitor.createGappedReferencesFrom(parser, ungappedReferenceDataStore, filter, ungappedReferenceRanges);
	}
	/**
	 * Parse the SAM file and call the appropriate methods on the given
	 * {@link AssemblyTransformer}.
	 * @param transformer the {@link AssemblyTransformer} instance to call
	 * the methods on; can not be null.
	 * @throws NullPointerException if transformer is null.
	 */
	@Override
	public void transform(final AssemblyTransformer transformer) throws IOException{
		if(transformer ==null){
			throw new NullPointerException("transformer can not be null");
		}
		try {
			SamTransformerVisitor visitor = new SamTransformerVisitor(referenceDataStore, transformer);
			parser.parse(SamParserOptions.builder().filter(filter).build(), visitor);
			
		} catch (Exception e) {
			throw new IOException("error parsing sam file", e);
		}
	}
	
	public void transform(String referenceId, Range range, AssemblyTransformer transformer) throws IOException {
		Objects.requireNonNull(referenceId);
		Objects.requireNonNull(range);
		Objects.requireNonNull(transformer);
		
		if(!referenceDataStore.contains(referenceId)) {
			throw new IllegalArgumentException(referenceId+ " does not exist");
		}
		SamTransformerVisitor visitor = new SamTransformerVisitor(referenceDataStore, transformer);
		parser.parse(SamParserOptions.builder().filter(filter).reference(referenceId, range).build(), visitor);
	}
	
	public void transform(String referenceId, List<Range> ranges, AssemblyTransformer transformer) throws IOException {
		Objects.requireNonNull(referenceId);
		Objects.requireNonNull(ranges);
		Objects.requireNonNull(transformer);
		ranges.forEach(Objects::requireNonNull);
		
		if(!referenceDataStore.contains(referenceId)) {
			throw new IllegalArgumentException(referenceId+ " does not exist");
		}
		SamTransformerVisitor visitor = new SamTransformerVisitor(referenceDataStore, transformer);
		parser.parse(SamParserOptions.builder().filter(filter).reference(referenceId, ranges).build(), visitor);
	}
	
	
	private static final class SamTransformerVisitor implements SamVisitor{

		private final AssemblyTransformer transformer;
		private final NucleotideSequenceDataStore referenceDataStore;
		private final Map<String,SamAlignmentGapInserter> gapOffsetMap;
		

		VirtualFileOffset lowestSeen , highestSeen;
		public SamTransformerVisitor(NucleotideSequenceDataStore referenceDataStore, AssemblyTransformer transformer) throws DataStoreException {
			this.referenceDataStore = referenceDataStore;
			this.transformer = transformer;
			gapOffsetMap = new HashMap<>(MapUtil.computeMinHashMapSizeWithoutRehashing(referenceDataStore.getNumberOfRecords()));
			
		}

		@Override
		public void visitHeader(SamVisitorCallback callback, SamHeader header) {
			
			for(SamReferenceSequence refSeq : header.getReferenceSequences()){
				String id = refSeq.getName();
				try {
					NucleotideSequence ref = referenceDataStore.get(id);
					if(ref ==null){
						throw new IllegalStateException("error could not find reference sequence in fasta file with id " + id);
					}
					gapOffsetMap.put(id,  new SamAlignmentGapInserter(ref));
					
					transformer.referenceOrConsensus(id, ref, callback::haltParsing);
				} catch (DataStoreException e) {
					throw new IllegalStateException("error getting reference sequence from fasta file", e);
				}
				
			}
		}
	

		@Override
		public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start,
				VirtualFileOffset end) {
			if(record.isPrimary()){
				
				if(record.mapped()){
					String refName = record.getReferenceName();
						
					Direction dir = record.getDirection();
					Cigar cigar = record.getCigar();
					int rawLength = cigar.getUnpaddedReadLength(ClipType.RAW);
					Range validRange;
					
					
					//extra insertions have been added to the reference
					//from other reads that we don't know about
					//modify the cigar accordingly
					NucleotideSequence rawSequence;
					QualitySequence quals;
					if(dir == Direction.FORWARD){
						rawSequence = record.getSequence();
						quals = record.getQualities();
						validRange = cigar.getValidRange();
					}else{
						rawSequence = record.getSequence().reverseComplement();
						if(record.getQualities() ==null){
							quals = null;
						}else{
							quals = new QualitySequenceBuilder(record.getQualities())
											.reverse()
											.turnOffDataCompression(true)
											.build();
						}
						validRange = AssemblyUtil.reverseComplementValidRange(cigar.getValidRange(), rawLength);
					}
					SamAlignmentGapInserter samAlignmentGapInserter = gapOffsetMap.get(refName);
					if(samAlignmentGapInserter==null) {
						throw new IllegalStateException("unknown reference " + refName);
					}
					SamAlignmentGapInserter.Result insertedResult = samAlignmentGapInserter.computeExtraInsertions(cigar, rawSequence, record.getStartPosition()-1, dir);
					
					
				
					//if the read is mated SAM doesn't put the /1 or /2 ?
					//what happens in CASAVA 1.8 reads?
					String readName = record.getQueryName();
					SamRecordFlags flags = record.getFlags();
					//from the SAMv1 spec
					//The leftmost segment has a plus sign and the rightmost has a
					//minus sign. The sign of segments in the middle is undefined. 
					//It is set as 0 for single-segment
					//template or when the information is unavailable.
					if(flags.contains(SamRecordFlag.HAS_MATE_PAIR)){
						if(record.getObservedTemplateLength() >=0){
							//first read
							readName +="/1";
						}else{
							readName +="/2";
						}
						
					}
					if(lowestSeen ==null || start.compareTo(lowestSeen) <0) {
						lowestSeen = start;
					}
					if(highestSeen ==null || highestSeen.compareTo(end) <0) {
						highestSeen = end;
					}
					//update valid range?
					transformer.aligned(readName, rawSequence, quals, null, null, 
							refName, 
							insertedResult.getGappedStartOffset(), 
							dir, 
							insertedResult.getGappedSequence().build(), 
							
							new ReadInfo(validRange, rawLength), record);
					
				}else{
					transformer.notAligned(record.getQueryName(), record.getSequence(), record.getQualities(), null, null, record);
					
				}
			}
				
		}

		@Override
		public void visitEnd() {
			transformer.endAssembly();			
		}
		
		@Override
		public void halted() {
			transformer.endAssembly();		
		}

	
	
}
}
