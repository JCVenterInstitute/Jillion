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
package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.clc.cas.ReAlignReads.ReAlignResult;
import org.jcvi.jillion.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.jillion.assembly.clc.cas.read.DefaultCasPlacedRead;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.TraceDataStore;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordBuilder;
import org.jcvi.jillion.trace.sff.SffFileIterator;
import org.jcvi.jillion.trace.sff.SffFlowgram;
import org.jcvi.jillion.trace.sff.SffUtil;
/**
 * {@code AbstractAlignedReadCasVisitor} is a {@link CasFileVisitor}
 * that handles iterating over the aligned reads in the cas file.
 * Cas files don't actually store any read information, just pointers
 * to where the input read files exist on the file system.
 * This means that all the read alignments in the cas must be matched up 
 * exactly with the input read files.  This can get very complicated
 * so this class handles all of that for you.
 * <p/>
 * This abstract class finds all the read files to be parsed
 * and maps them to the alignment information inside the cas file.
 * For each read that aligned (matched), the {@link #aligned(Trace, String, CasPlacedRead)}
 * method is called; for each non-aligned read, the {@link #notAligned(Trace)}
 * is called.
 * 
 * @author dkatzel
 *
 */
public abstract class AbstractAlignedReadCasVisitor extends AbstractCasFileVisitor{

	private final CasGappedReferenceDataStore gappedReferenceDataStore;

	private final File workingDir;
	
	private final List<StreamingIterator<? extends Trace>> iterators = new ArrayList<StreamingIterator<? extends Trace>>();
	
	private FastqQualityCodec qualityCodec=null;
	
	private Map<String, ReAlignReads> reAligners = new HashMap<String, ReAlignReads>();
	/**
	 * Create a new AbstractAlignedReadCasVisitor instance that will
	 * modify the Cas Match alignments to add extra insertions into the reads (gaps)
	 * that will make it correctly align to the gapped reference in the provided
	 * {@link CasGappedReferenceDataStore}.  The gapped datastore must have
	 * indexes for all the references in the cas file being visited
	 * but can only have some gapped reference sequences available by ID.
	 * If the {@link CasGappedReferenceDataStore#contains(String)} returns {@code false}
	 * for any cas match, then that match will be skipped.
	 * 
	 * @param workingDir the working directory of the cas file;
	 * may be null (means current directory).
	 * 
	 * @param gappedReferenceDataStore {@link CasGappedReferenceDataStore}
	 * which contains the precomputed gapped referenced by 
	 * in this cas file for these alignments; can not be null.
	 * 
	 * @thorws NullPointerException if gappedReferenceDataStore is null.
	 */
	public AbstractAlignedReadCasVisitor(File workingDir,
			CasGappedReferenceDataStore gappedReferenceDataStore) {
		if(gappedReferenceDataStore ==null){
			throw new NullPointerException("gapped Reference DataStore can not be null");
		}
		
		this.workingDir = workingDir;
		this.gappedReferenceDataStore = gappedReferenceDataStore;
	}

	
	
	public FastqQualityCodec getQualityCodec() {
		return qualityCodec;
	}



	public void setQualityCodec(FastqQualityCodec qualityCodec) {
		this.qualityCodec = qualityCodec;
	}



	public File getWorkingDir() {
		return workingDir;
	}

	public final CasGappedReferenceDataStore getGappedReferenceDataStore() {
		return gappedReferenceDataStore;
	}

	@Override
	public void visitReadFileInfo(CasFileInfo readFileInfo) {
		List<String> fileNames = readFileInfo.getFileNames();
		if(fileNames.isEmpty()){
			//nothing to do... 
			return;
		}
		if(fileNames.size()==1){
			try {
				File file = CasUtil.getFileFor(workingDir, fileNames.get(0));
				
				iterators.add(createIteratorFor(file));
			} catch (Exception e) {
				for(StreamingIterator<? extends Trace> iter : iterators){
					IOUtil.closeAndIgnoreErrors(iter);
				}
				throw new IllegalStateException("error getting input read data", e);
			}      
		}else{
			List<StreamingIterator<? extends Trace>> iteratorLists = new ArrayList<StreamingIterator<? extends Trace>>(fileNames.size());
			try {
				for(String filePath :fileNames){					
						File file = CasUtil.getFileFor(workingDir, filePath);						
						iteratorLists.add(createIteratorFor(file));					
		        }
				iterators.add(new InterleavedStreamingIterators(iteratorLists));
			} catch (Exception e) {
				for(StreamingIterator<? extends Trace> iter : iteratorLists){
					IOUtil.closeAndIgnoreErrors(iter);
				}
				for(StreamingIterator<? extends Trace> iter : iterators){
					IOUtil.closeAndIgnoreErrors(iter);
				}
				throw new IllegalStateException("error getting input read data", e);
			}   
		}
		
	}
	
	private StreamingIterator<? extends Trace> createIteratorFor(File file) throws DataStoreException, IOException{
        ReadFileType readType = ReadFileType.getTypeFromFile(file);
           switch(readType){
	            case FASTQ: 
	            	return createFastqIterator(file);
	            case SFF:
	            	return createSffIterator(file);
	            case FASTA:
                       return createFastaIterator(file);
	            default: 
	            	throw new IllegalArgumentException("unsupported type "+ file.getName());
	            }
        
   }


    protected StreamingIterator<? extends Trace> createFastqIterator(File illuminaFile) throws DataStoreException {
		try {
			FastqFileDataStoreBuilder builder = new FastqFileDataStoreBuilder(illuminaFile)
											.hint(DataStoreProviderHint.ITERATION_ONLY);
			FastqQualityCodec codecToUse = getQualityCodec();
			if(codecToUse !=null){
				builder.qualityCodec(codecToUse);
			}
			FastqDataStore datastore = builder.build();
			return new RemoveWhitespaceFromIdAdapter(datastore.iterator());
		} catch (IOException e) {
			throw new IllegalStateException("fastq file no longer exists! : "+ illuminaFile.getAbsolutePath(), e);
		}
		
    }

    protected StreamingIterator<? extends Trace> createSffIterator(File sffFile) throws DataStoreException{
        return SffFileIterator.createNewIteratorFor(sffFile);
    }

    protected StreamingIterator<? extends Trace> createFastaIterator(File fastaFile) throws DataStoreException{        
        try {
			NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fastaFile)
															.hint(DataStoreProviderHint.ITERATION_ONLY)
															.build();
			
			@SuppressWarnings("unchecked")
			TraceDataStore<Trace> fakeQualities = DataStoreUtil.adapt(TraceDataStore.class, datastore, 
					new DataStoreUtil.AdapterCallback<NucleotideFastaRecord, Trace>() {

						@Override
						public Trace get(final NucleotideFastaRecord from) {
						        int numberOfQualities =(int) from.getSequence().getLength();
								byte[] qualities = new byte[numberOfQualities];
								Arrays.fill(qualities, PhredQuality.valueOf(30).getQualityScore());
						        final QualitySequence qualSequence = new QualitySequenceBuilder(qualities).build();
							return new Trace() {
								
								@Override
								public QualitySequence getQualitySequence() {
									return qualSequence;
								}
								
								@Override
								public NucleotideSequence getNucleotideSequence() {

									return from.getSequence();
								}
								
								@Override
								public String getId() {
									return from.getId();
								}
							};
						}
				
			});
			return fakeQualities.iterator();
        } catch (IOException e) {
			throw new DataStoreException("error reading fasta file "+ fastaFile.getAbsolutePath(),e);
		}
    }
	/**
	 * The given {@link Trace} did not align to any references.    
	 * @param currentTrace the complete {@link Trace} that did not match.
	 */
    protected abstract void notAligned(Trace currentTrace);
    /**
     * The given {@link Trace} aligned to the given reference id
     * with the given alignment.
     * @param referenceId the reference id that the read aligned to.
     * @param read the alignment information of the read to the reference.
     * @param traceOfRead the complete {@link Trace} that aligned.
     */
    protected abstract void  aligned(Trace traceOfRead, String referenceId, CasPlacedRead read);
    
	@Override
	public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
		
		
		return new TraceCasMatchVisitor(IteratorUtil.createChainedStreamingIterator(iterators));
		
	}
	
	private class TraceCasMatchVisitor implements CasMatchVisitor{
		private final StreamingIterator<Trace> chainedTraceIterator;
		
		public TraceCasMatchVisitor(
				StreamingIterator<Trace> chainedTraceIterator) {
			this.chainedTraceIterator = chainedTraceIterator;
		}

		@Override
		public void visitMatch(CasMatch match) {
			if(!chainedTraceIterator.hasNext()){
				closeIterator();
				throw new IllegalStateException("possible cas file corruption : no more reads in input files but cas file says there are more reads");
			}
			Trace currentTrace = chainedTraceIterator.next();
			if(match.matchReported()){
				CasAlignment alignment = match.getChosenAlignment();
				long refIndex = alignment.getReferenceIndex();
				String refId = gappedReferenceDataStore.getIdByIndex(refIndex);
				
				
				String readId = currentTrace.getId();
				try {
					
					if(refId ==null){
						closeIterator();
						throw new IllegalStateException("could not get get gapped reference for index "+ refIndex);
					
					}
					if(!gappedReferenceDataStore.contains(refId)){
						return;
					}
					ReAlignReads reAligner =reAligners.get(refId);
					if(reAligner ==null){
						NucleotideSequence gappedReference = gappedReferenceDataStore.get(refId);
						reAligner = new ReAlignReads(gappedReference, true);
						reAligners.put(refId, reAligner);
					}
					 
			        Range trimRange = match.getTrimRange();
			        if(trimRange ==null && currentTrace instanceof SffFlowgram){
			        	//CLC uses the trimmed flowgrams when aligning
			        	//if the trimRange for this match isn't explicitly set
			        	//and the read is a flowgram, then use it's trim range 
			        	trimRange = SffUtil.computeTrimRangeFor((SffFlowgram)currentTrace);
			        }
			        Direction dir = alignment.readIsReversed()? Direction.REVERSE : Direction.FORWARD;
			       ReAlignResult reAlignResult= reAligner.realignValidBases(currentTrace.getNucleotideSequence(), 
			    		   alignment.getStartOfMatch(),
			        		dir,
			        			alignment.getAlignmentRegions(), trimRange);
			        
			       CasPlacedRead read=  new DefaultCasPlacedRead(readId, 
			    		  (ReferenceMappedNucleotideSequence) reAlignResult.getGappedValidBases(),
			    		   reAlignResult.getGappedStartOffset(), reAlignResult.getValidRange(), 
			    		   dir,(int)currentTrace.getNucleotideSequence().getLength());
			       
			      
			        
			        AbstractAlignedReadCasVisitor.this.aligned(currentTrace, refId, read);
				} catch (Throwable e) {
					closeIterator();
					throw new IllegalStateException("processing read " + readId + " for reference "+ refId, e);
				
				}
			}else{
				AbstractAlignedReadCasVisitor.this.notAligned(currentTrace);
			}
			
		}
		

		

		@Override
		public void visitEnd() {
			closeIterator();
		}

		@Override
		public void halted() {
			closeIterator();
		}
		
		private void closeIterator(){
			IOUtil.closeAndIgnoreErrors(chainedTraceIterator);
		}
	}
	
	private static class RemoveWhitespaceFromIdAdapter implements StreamingIterator<FastqRecord>{
		private final  StreamingIterator<FastqRecord> delegate;

		private static Pattern WHITESPACE_PATTERN = Pattern.compile("^(\\S+)\\s+(\\S+)$");
		public RemoveWhitespaceFromIdAdapter(StreamingIterator<FastqRecord> delegate) {
			this.delegate = delegate;
		}

		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}

		@Override
		public void close() {
			delegate.close();
			
		}

		@Override
		public FastqRecord next() {
			FastqRecord record =delegate.next();
			Matcher matcher= WHITESPACE_PATTERN.matcher(record.getId());
			if(matcher.matches()){
				return new FastqRecordBuilder(matcher.group(1)+"_"+matcher.group(2), 
											record.getNucleotideSequence(),
											record.getQualitySequence())
							.build();
			}
			return record;
		}

		@Override
		public void remove() {
			delegate.remove();
			
		}	
	}
	
	private static class InterleavedStreamingIterators implements StreamingIterator<Trace>{
		private final List<StreamingIterator<? extends Trace>> iterators;

		private int offset=0;
		
		public InterleavedStreamingIterators(
				List<StreamingIterator<? extends Trace>> iterators) {
			this.iterators = iterators;
		}

		@Override
		public boolean hasNext() {
			return iterators.get(offset).hasNext();
		}

		@Override
		public void close() {
			for(StreamingIterator<? extends Trace> iter : iterators){
				IOUtil.closeAndIgnoreErrors(iter);
			}			
			
		}

		@Override
		public Trace next() {
			Trace next = iterators.get(offset++).next();
			if(offset ==iterators.size()){
				offset=0;
			}
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
		
	}
}
