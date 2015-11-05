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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
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
/**
 * A {@link CasFileVisitor}
 * that handles iterating over the aligned reads in the cas file.
 * Cas files don't actually store any read information, just pointers
 * to where the input read files exist on the file system.
 * This means that all the read alignments in the cas must be matched up 
 * exactly with the input read files.  This can get very complicated
 * so this class handles all of that for you.
 * <p>
 * This abstract class finds all the read files to be parsed
 * and maps them to the corresponding read in the input sequence file(s)
 * so you can get the {@link Trace} for each read.
 * For each read that aligned (matched), the {@link #aligned(Trace, String, CasMatch)}
 * method is called; for each non-aligned read, the {@link #notAligned(Trace)}
 * is called.
 * 
 * @author dkatzel
 *
 * @since 5.1
 */
public abstract class AbstractReadCasVisitor extends AbstractCasFileVisitor{

	private final File workingDir;
	
	private final List<StreamingIterator<? extends Trace>> iterators = new ArrayList<StreamingIterator<? extends Trace>>();
	
	private FastqQualityCodec qualityCodec=null;
	
	private List<String> referenceIds = new ArrayList<>();
	
	private StreamingIterator<Trace> chainedTraceIterator;
	
	
	/**
	 * Create a new instance.
	 * 
	 * @param workingDir the working directory of the cas file;
	 * may be null (means current directory).
	 * 
	 * @throws NullPointerException if gappedReferenceDataStore is null.
	 */
	public AbstractReadCasVisitor(File workingDir) {
		
		this.workingDir = workingDir;
		
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


	@Override
	public void visitReferenceFileInfo(CasFileInfo referenceFileInfo) {
		for(String path: referenceFileInfo.getFileNames()){
			try{
				File fastaFile = CasUtil.getFileFor(workingDir, path);
				
				try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fastaFile)
															.hint(DataStoreProviderHint.ITERATION_ONLY)
															.build();
					StreamingIterator<String> iter = datastore.idIterator();
						){
					while(iter.hasNext()){
						referenceIds.add(iter.next());
					}
				}
			}catch(Exception e){
				throw new IllegalStateException("error parsing reference data", e);
			}
		}
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
     * with the given {@link CasMatch}.
     * @param referenceId the reference id that the read aligned to.
     * @param match the {@link CasMatch} of this read describing the alignment.
     * 
     * @param traceOfRead the complete {@link Trace} that aligned.
     */
    protected abstract void aligned(Trace traceOfRead, String referenceId, CasMatch match);
    
	@Override
	public CasMatchVisitor visitMatches(CasVisitorCallback callback) {
		
		chainedTraceIterator = IteratorUtil.createChainedStreamingIterator(iterators);
		return new TraceCasMatchVisitor(chainedTraceIterator);
		
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
				int refIndex = (int)alignment.getReferenceIndex();
				
				String refId = referenceIds.get(refIndex);
				
				aligned(currentTrace, refId, match);
				
			}else{
				AbstractReadCasVisitor.this.notAligned(currentTrace);
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
		
		
	}
	
	protected void closeIterator(){
		IOUtil.closeAndIgnoreErrors(chainedTraceIterator);
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
