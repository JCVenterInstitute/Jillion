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

package org.jcvi.assembly.cas;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.AcePlacedReadAdapter;
import org.jcvi.assembly.ace.PhdInfo;
import org.jcvi.assembly.ace.consed.ChromatDirFastaConsedPhdAdaptedIterator;
import org.jcvi.assembly.ace.consed.FastaConsedPhdAdaptedIterator;
import org.jcvi.assembly.ace.consed.FastqConsedPhdAdaptedIterator;
import org.jcvi.assembly.ace.consed.FlowgramConsedPhdAdaptedIterator;
import org.jcvi.assembly.ace.consed.PhdReadRecord;
import org.jcvi.assembly.cas.alignment.score.CasScoringScheme;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.fastX.fasta.seq.LargeNucleotideFastaIterator;
import org.jcvi.fastX.fastq.FastQQualityCodec;
import org.jcvi.fastX.fastq.LargeFastQFileIterator;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffFileIterator;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.util.ChainedCloseableIterator;
import org.jcvi.util.CloseableIterator;
import org.joda.time.DateTime;

public abstract class CasPhdReadVisitor extends AbstractOnePassCasFileVisitor{

	private final File workingDir;
	private final CasTrimMap trimMap;
	private final DateTime phdDate;
	private final FastQQualityCodec fastqQualityCodec;
	private CloseableIterator<PhdReadRecord> phdIterator;
	protected final List<NucleotideEncodedGlyphs> orderedGappedReferences;
	private final TrimDataStore validRangeDataStore;
	private final List<CloseableIterator<PhdReadRecord>> iterators = new ArrayList<CloseableIterator<PhdReadRecord>>();
    private final File chromatDir;
	public CasPhdReadVisitor(File workingDir, CasTrimMap trimMap,
			FastQQualityCodec fastqQualityCodec,
			List<NucleotideEncodedGlyphs> orderedGappedReferences,
			TrimDataStore validRangeDataStore,
			DateTime phdDate,
			File chromatDir) {
		super();
		this.workingDir = workingDir;
		this.trimMap = trimMap;
		this.phdDate = phdDate;
		this.fastqQualityCodec = fastqQualityCodec;
		this.orderedGappedReferences = orderedGappedReferences;
		this.validRangeDataStore = validRangeDataStore;
		System.out.println(chromatDir.getAbsolutePath());
		this.chromatDir = chromatDir;
	}

	@Override
	public synchronized void visitReadFileInfo(CasFileInfo readFileInfo) {
		super.visitReadFileInfo(readFileInfo);
		for(String filename :readFileInfo.getFileNames()){
			
			File file = getTrimmedFileFor(filename);
			ReadFileType readType = ReadFileType.getTypeFromFile(filename);
			switch(readType){
			    case ILLUMINA:
        			    iterators.add(new FastqConsedPhdAdaptedIterator(                     
                                LargeFastQFileIterator.createNewIteratorFor(file, fastqQualityCodec),
                                file, 
                                phdDate));
        			    break;
			    case SFF:
			        iterators.add(
	                        new FlowgramConsedPhdAdaptedIterator(
	                        SffFileIterator.createNewIteratorFor(file),
	                        file,
	                        phdDate));
			        break;
			    case FASTA:
			        CloseableIterator<PhdReadRecord> iter;
			                if(chromatDir==null){
			                    iter= new FastaConsedPhdAdaptedIterator(
	                                LargeNucleotideFastaIterator.createNewIteratorFor(file),
	                                file,
	                                phdDate, PhredQuality.valueOf(30));
			                }else{
			                    iter = new ChromatDirFastaConsedPhdAdaptedIterator(
			                            LargeNucleotideFastaIterator.createNewIteratorFor(file),
	                                    file,
	                                    phdDate, PhredQuality.valueOf(30),
	                                    chromatDir);
			                }
			                
			                iterators.add(iter);
			        break;
		        default: throw new IllegalArgumentException("unsupported type "+ file.getName());
			        
			}			
		}
		
	}
	
	  @Override
    public synchronized void visitScoringScheme(CasScoringScheme scheme) {
        super.visitScoringScheme(scheme);
        phdIterator = new ChainedCloseableIterator<PhdReadRecord>(iterators);
    }

    private File getTrimmedFileFor(String pathToDataStore) {
	        File dataStoreFile = new File(workingDir, pathToDataStore);
	        File trimmedDataStore = trimMap.getUntrimmedFileFor(dataStoreFile);
	        return trimmedDataStore;
	    }

	@Override
	protected synchronized void visitMatch(CasMatch match, long readCounter) {
		PhdReadRecord phdReadRecord =phdIterator.next();
		
		if(match.matchReported()){
			Phd phd = phdReadRecord.getPhd();
			PhdInfo info = phdReadRecord.getPhdInfo();
			int casReferenceId = (int)match.getChosenAlignment().contigSequenceId();
			NucleotideEncodedGlyphs gappedReference =orderedGappedReferences.get(casReferenceId);
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
	

}
