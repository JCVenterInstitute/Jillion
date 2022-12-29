/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.sam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.sam.SamUtil;
import org.jcvi.jillion.internal.sam.index.IndexUtil;
import org.jcvi.jillion.sam.SamParser.SamParserOptions;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.index.BamIndex;
import org.jcvi.jillion.sam.index.ReferenceIndex;

class IndexedBamFileParser extends BamFileParser{
	private final BamIndex index;
	private static VirtualFileOffset BEGINING_OF_FILE = new VirtualFileOffset(0L);
	
	public IndexedBamFileParser(File bamFile, File baiFile, SamAttributeValidator validator) throws IOException {
		super(bamFile, validator);
		try(InputStream in = new BufferedInputStream(new FileInputStream(baiFile))){
			index = IndexUtil.parseIndex(in, this.getHeader());
		}
	}

   

    @Override
	public void parse(SamParserOptions options, SamVisitor visitor) throws IOException {
		if(options.getReferenceName().isEmpty()) {
			 super.parse(options,visitor);
			 return;
		}
		String referenceName = options.getReferenceName().get();
		ReferenceIndex refIndex =index.getReferenceIndex(referenceName);
		if(refIndex ==null){
			throw new IllegalArgumentException("no reference with name '"+ referenceName +"'");
		}
		
		if(options.getReferenceRange().isPresent()) {
			
			Range alignmentRange = options.getReferenceRange().get();
			//alignment range
			int[] overlappingBins = SamUtil.getCandidateOverlappingBins(alignmentRange);
			
			//TODO can probably do better filtering by specific bins...
			VirtualFileOffset start = refIndex.getLowestStartOffset();
			VirtualFileOffset end = refIndex.getHighestEndOffset();
			Predicate<SamRecord> recordBinFilter = (record) -> {
				if(!referenceName.equals(record.getReferenceName())){
					return false;
				}
			
				Range readAlignmentRange = record.getAlignmentRange();
				if(readAlignmentRange ==null) {
					return false;
				}
				int bin = SamUtil.computeBinFor(readAlignmentRange);
				if(Arrays.binarySearch(overlappingBins, bin) <0){
					return false;
				}
				return readAlignmentRange.isSubRangeOf(alignmentRange);
			};
			if(options.getFilter().isPresent()) {
				recordBinFilter = recordBinFilter.and(options.getFilter().get().asPredicate());
			}
			try(BgzfInputStream in = BgzfInputStream.create(bamFile, start)){
				//assume anything in this interval matches?
				AtomicBoolean keepParsing = new AtomicBoolean(true);
				options.getFilter().ifPresent(f->f.begin());
				this.parseBamRecords(visitor, 
						recordBinFilter,
						(vfs)-> vfs.compareTo(end) <=0,
						in,
						keepParsing,
						options.shouldCreateMementos() ? new BamCallback(keepParsing) :new MementoLessBamCallback(keepParsing));
				options.getFilter().ifPresent(f->f.end());
			}
		}else {
			VirtualFileOffset start = refIndex.getLowestStartOffset();
			
			VirtualFileOffset end = refIndex.getHighestEndOffset();
			
			Predicate<SamRecord> recordMatchPredicate =(record) ->referenceName.equals(record.getReferenceName());
			if(options.getFilter().isPresent()) {
				recordMatchPredicate = recordMatchPredicate.and(options.getFilter().get().asPredicate());
			}
			Predicate<VirtualFileOffset> endPredicate =(vfs) ->vfs.compareTo(end) <0;
			try(BgzfInputStream in = BgzfInputStream.create(bamFile, start)){
				options.getFilter().ifPresent(f->f.begin());
				if(BEGINING_OF_FILE.equals(start)){
					this.parseBamFromBeginning(visitor, 
					        options.shouldCreateMementos(),
							recordMatchPredicate,
							endPredicate, in);
				}else{
					//assume anything in this interval matches?
					AtomicBoolean keepParsing = new AtomicBoolean(true);
					this.parseBamRecords(visitor, 
							recordMatchPredicate,
							endPredicate,
							in,
							keepParsing, options.shouldCreateMementos() ? new BamCallback(keepParsing) :new MementoLessBamCallback(keepParsing));
				}
				options.getFilter().ifPresent(f->f.end());
			}
		}
	}



	

	

}
