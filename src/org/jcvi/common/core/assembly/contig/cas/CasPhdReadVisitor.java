/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.contig.cas;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedReadAdapter;
import org.jcvi.common.core.assembly.contig.ace.PhdInfo;
import org.jcvi.common.core.assembly.contig.ace.consed.ChromatDirFastaConsedPhdAdaptedIterator;
import org.jcvi.common.core.assembly.contig.ace.consed.EditedFastaChromatDirPhdAdapterIterator;
import org.jcvi.common.core.assembly.contig.ace.consed.FastaConsedPhdAdaptedIterator;
import org.jcvi.common.core.assembly.contig.ace.consed.FastqConsedPhdAdaptedIterator;
import org.jcvi.common.core.assembly.contig.ace.consed.FlowgramConsedPhdAdaptedIterator;
import org.jcvi.common.core.assembly.contig.ace.consed.PhdReadRecord;
import org.jcvi.common.core.assembly.contig.cas.align.score.CasScoringScheme;
import org.jcvi.common.core.assembly.contig.cas.read.CasPlacedRead;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.LargeFastQFileIterator;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.nuc.fasta.LargeNucleotideFastaIterator;
import org.jcvi.common.core.seq.qual.PhredQuality;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffFileIterator;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.util.ChainedCloseableIterator;
import org.jcvi.common.core.util.CloseableIterator;
import org.jcvi.io.IOUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

public abstract class CasPhdReadVisitor extends AbstractOnePassCasFileVisitor{

	private final File workingDir;
	private final CasTrimMap trimMap;
	private CloseableIterator<PhdReadRecord> phdIterator;
	private final List<NucleotideSequence> orderedGappedReferences;
	private final TrimDataStore validRangeDataStore;
	private final List<CloseableIterator<PhdReadRecord>> iterators = new ArrayList<CloseableIterator<PhdReadRecord>>();
    private final TraceDetails traceDetails;
	public CasPhdReadVisitor(File workingDir, CasTrimMap trimMap,
			List<NucleotideSequence> orderedGappedReferences,
			TrimDataStore validRangeDataStore,
			TraceDetails traceDetails) {
		super();
		this.traceDetails = traceDetails;
		this.workingDir = workingDir;
		this.trimMap = trimMap;
		this.orderedGappedReferences = orderedGappedReferences;
		this.validRangeDataStore = validRangeDataStore;
	}

	protected final NucleotideSequence getGappedReference(int index){
	    return orderedGappedReferences.get(index);
	}
	@Override
	public final synchronized void visitReadFileInfo(CasFileInfo readFileInfo) {
		super.visitReadFileInfo(readFileInfo);
		for(String filename :readFileInfo.getFileNames()){
			
			File file = getTrimmedFileFor(filename);
			ReadFileType readType = ReadFileType.getTypeFromFile(filename);
			switch(readType){
			    case ILLUMINA:
        			    iterators.add(new FastqConsedPhdAdaptedIterator(                     
                                LargeFastQFileIterator.createNewIteratorFor(file, traceDetails.getFastqQualityCodec()),
                                file, 
                                traceDetails.getPhdDate()));
        			    break;
			    case SFF:
			        iterators.add(
	                        new FlowgramConsedPhdAdaptedIterator(
	                        SffFileIterator.createNewIteratorFor(file),
	                        file,
	                        traceDetails.getPhdDate()));
			        break;
			    case FASTA:
			        CloseableIterator<PhdReadRecord> iter;
			                if(!traceDetails.hasChromatDir()){
			                    iter= new FastaConsedPhdAdaptedIterator(
	                                LargeNucleotideFastaIterator.createNewIteratorFor(file),
	                                file,
	                                traceDetails.getPhdDate(), PhredQuality.valueOf(30));
			                }else{
			                    if(traceDetails.hasFastaEdits()){
			                        iter = new EditedFastaChromatDirPhdAdapterIterator(
			                                LargeNucleotideFastaIterator.createNewIteratorFor(file),
			                                file, 
			                                traceDetails.getPhdDate(), 
			                                PhredQuality.valueOf(30), 
			                                traceDetails.getChromatDir());
			                    }else{
    			                    iter = new ChromatDirFastaConsedPhdAdaptedIterator(
    			                            LargeNucleotideFastaIterator.createNewIteratorFor(file),
    	                                    file,
    	                                    traceDetails.getPhdDate(), PhredQuality.valueOf(30),
    	                                    traceDetails.getChromatDir());
			                    }
			                }
			                
			                iterators.add(iter);
			        break;
		        default: throw new IllegalArgumentException("unsupported type "+ file.getName());
			        
			}			
		}
		
	}
	
	  @Override
    public final synchronized void visitScoringScheme(CasScoringScheme scheme) {
        super.visitScoringScheme(scheme);
        phdIterator = new ChainedCloseableIterator<PhdReadRecord>(iterators);
    }

    private File getTrimmedFileFor(String pathToDataStore) {
	        File dataStoreFile = new File(workingDir, pathToDataStore);
	        File trimmedDataStore = trimMap.getUntrimmedFileFor(dataStoreFile);
	        return trimmedDataStore;
	    }

	@Override
	protected final synchronized void visitMatch(CasMatch match, long readCounter) {
		PhdReadRecord phdReadRecord =phdIterator.next();
		
		if(match.matchReported()){
			Phd phd = phdReadRecord.getPhd();
			PhdInfo info = phdReadRecord.getPhdInfo();
			int casReferenceId = (int)match.getChosenAlignment().contigSequenceId();
			NucleotideSequence gappedReference =orderedGappedReferences.get(casReferenceId);
			String id = phd.getId();
			try {
				CasPlacedRead placedRead = CasUtil.createCasPlacedRead(match, id, 
						phd.getBasecalls(), 
						validRangeDataStore.get(id), gappedReference);
				AcePlacedRead acePlacedRead = new AcePlacedReadAdapter(placedRead, info, 
				        placedRead.getUngappedFullLength());
				visitAcePlacedRead(acePlacedRead,phd,casReferenceId);
			} catch (Exception e) {
			    IOUtil.closeAndIgnoreErrors(phdIterator);
				throw new IllegalStateException("error getting trim range for " + id, e);
			}
		}
	}

	protected abstract void visitAcePlacedRead(AcePlacedRead acePlacedRead,
			Phd phd,
			int casReferenceId);
	

	public static final class TraceDetails{
	    private final boolean hasFastaEdits;
	    private final File chromatDir;
	    private final DateTime phdDate;
	    private final FastQQualityCodec fastqQualityCodec;
	    
	    public static class Builder implements org.jcvi.Builder<TraceDetails>{
	        private boolean hasFastaEdits=false;
	        private File chromatDir;
	        private DateTime phdDate =null;
	        private final FastQQualityCodec fastqQualityCodec;
	        public Builder(FastQQualityCodec fastqQualityCodec){
	            if(fastqQualityCodec==null){
	                throw new NullPointerException("can not be null");
	            }
	            this.fastqQualityCodec = fastqQualityCodec;
	        }
            /**
            * {@inheritDoc}
            */
            @Override
            public TraceDetails build() {
                if(phdDate ==null){
                    phdDate = new DateTime(DateTimeUtils.currentTimeMillis());
                }
                return new TraceDetails(chromatDir, phdDate, fastqQualityCodec, hasFastaEdits);
            }
	        public Builder hasEdits(boolean hasEdits){
	            this.hasFastaEdits = hasEdits;
	            return this;
	        }
	        public Builder phdDate(DateTime phdDate){
	            this.phdDate = phdDate;
	            return this;
	        }
	        public Builder chromatDir(File chromatDir){
	            this.chromatDir = chromatDir;
	            return this;
	        }
	    }
        private TraceDetails(File chromatDir, DateTime phdDate,
                FastQQualityCodec fastqQualityCodec, boolean hasFastaEdits) {
            this.chromatDir = chromatDir;
            this.phdDate = phdDate;
            this.fastqQualityCodec = fastqQualityCodec;
            this.hasFastaEdits = hasFastaEdits;
        }
        /**
         * @return the hasFastaEdits
         */
        public boolean hasFastaEdits() {
            return hasFastaEdits;
        }
        /**
         * @return the chromatDir
         */
        public File getChromatDir() {
            return chromatDir;
        }
        /**
         * @return the phdDate
         */
        public DateTime getPhdDate() {
            return phdDate;
        }
        /**
         * @return the fastqQualityCodec
         */
        public FastQQualityCodec getFastqQualityCodec() {
            return fastqQualityCodec;
        }
	    
        public boolean hasChromatDir(){
            return chromatDir !=null;
        }
        
	    
	}
}
