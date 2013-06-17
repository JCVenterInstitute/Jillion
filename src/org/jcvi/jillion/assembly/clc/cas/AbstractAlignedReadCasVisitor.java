/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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

import org.jcvi.jillion.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.jillion.assembly.clc.cas.read.DefaultCasPlacedReadFromCasAlignmentBuilder;
import org.jcvi.jillion.core.Range;
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
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordBuilder;
import org.jcvi.jillion.trace.sff.SffFileIterator;
import org.jcvi.jillion.trace.sff.SffFlowgram;
import org.jcvi.jillion.trace.sff.SffUtil;

public abstract class AbstractAlignedReadCasVisitor extends AbstractCasFileVisitor{

	private final CasGappedReferenceDataStore gappedReferenceDataStore;

	private final File workingDir;
	
	private List<StreamingIterator<? extends Trace>> iterators = new ArrayList<StreamingIterator<? extends Trace>>();
	
	
	
	public AbstractAlignedReadCasVisitor(File casFile,
			CasGappedReferenceDataStore gappedReferenceDataStore) {
		if(gappedReferenceDataStore ==null){
			throw new NullPointerException("gapped Reference DataStore can not be null");
		}
		if(casFile ==null){
			throw new NullPointerException("cas file can not be null");
		}
		this.workingDir = casFile.getParentFile();
		this.gappedReferenceDataStore = gappedReferenceDataStore;
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
	
	private StreamingIterator<? extends Trace> createIteratorFor(File file) throws DataStoreException{
        ReadFileType readType = ReadFileType.getTypeFromFile(file.getName());
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
			FastqDataStore datastore = new FastqFileDataStoreBuilder(illuminaFile)
											.hint(DataStoreProviderHint.ITERATION_ONLY)
											.build();
			return new RemoveWhitespaceFromIdAdapter(datastore.iterator());
		} catch (IOException e) {
			throw new IllegalStateException("fastq file no longer exists! : "+ illuminaFile.getAbsolutePath());
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
	    
    protected abstract void visitUnMatched(Trace currentTrace);

    protected abstract void  visitMatch(String referenceId, CasPlacedRead read, Trace traceOfRead);
    
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
				CasPlacedRead read =null;
				String readId = currentTrace.getId();
				try {
					if(refId ==null){
						closeIterator();
						throw new IllegalStateException("could not get get gapped reference for index "+ refIndex);
					
					}
					NucleotideSequence gappedReference = gappedReferenceDataStore.get(refId);
					long ungappedStartOffset = alignment.getStartOfMatch();
			        long gappedStartOffset = gappedReference.getGappedOffsetFor((int)ungappedStartOffset);
			        
			        List<CasAlignmentRegion> regionsToConsider = new ArrayList<CasAlignmentRegion>(alignment.getAlignmentRegions());
			        int lastIndex = regionsToConsider.size()-1;
			        if(regionsToConsider.get(lastIndex).getType()==CasAlignmentRegionType.INSERT){
			            regionsToConsider.remove(lastIndex);
			        }
			        
			        NucleotideSequence sequence = currentTrace.getNucleotideSequence();
			        
			        Range trimRange = match.getTrimRange();
			        if(trimRange ==null && currentTrace instanceof SffFlowgram){
			        	//CLC uses the trimmed flowgrams when aligning
			        	//if the trimRange for this match isn't explicitly set
			        	//and the read is a flowgram, then use it's trim range 
			        	trimRange = SffUtil.computeTrimRangeFor((SffFlowgram)currentTrace);
			        }
			        DefaultCasPlacedReadFromCasAlignmentBuilder readBuilder= new DefaultCasPlacedReadFromCasAlignmentBuilder(readId,
			       		 gappedReference,
			       		sequence, 
			       		alignment.readIsReversed(), gappedStartOffset,
			       		trimRange);
			        
			        readBuilder.addAlignmentRegions(regionsToConsider, gappedReference);
			        read = readBuilder.build();
			        
			        AbstractAlignedReadCasVisitor.this.visitMatch(refId, read, currentTrace);
				} catch (Throwable e) {
					closeIterator();
					throw new IllegalStateException("processing read " + readId + " for reference "+ refId, e);
				
				}
			}else{
				AbstractAlignedReadCasVisitor.this.visitUnMatched(currentTrace);
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
		public void close() throws IOException {
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
		public void close() throws IOException {
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
