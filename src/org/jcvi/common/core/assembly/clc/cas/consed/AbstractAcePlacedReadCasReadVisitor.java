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

package org.jcvi.common.core.assembly.clc.cas.consed;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.assembly.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.ace.AcePlacedReadAdapter;
import org.jcvi.common.core.assembly.clc.cas.AbstractCasReadVisitor;
import org.jcvi.common.core.assembly.clc.cas.CasInfo;
import org.jcvi.common.core.assembly.clc.cas.CasMatch;
import org.jcvi.common.core.assembly.clc.cas.TraceDetails;
import org.jcvi.common.core.assembly.clc.cas.read.CasPlacedRead;
import org.jcvi.common.core.seq.fastx.fasta.nt.LargeNucleotideSequenceFastaIterator;
import org.jcvi.common.core.seq.fastx.fastq.FastqDataStore;
import org.jcvi.common.core.seq.fastx.fastq.LargeFastqFileDataStore;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffFileIterator;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractAcePlacedReadCasReadVisitor extends AbstractCasReadVisitor<PhdReadRecord>{

    public AbstractAcePlacedReadCasReadVisitor(CasInfo casInfo) {
        super(casInfo);
    }


    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<PhdReadRecord> createIlluminaIterator(
            File illuminaFile, TraceDetails traceDetails) {
        FastqDataStore datastore;
		try {
			datastore = LargeFastqFileDataStore.create(illuminaFile, traceDetails.getFastqQualityCodec());
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("fastq file no longer exists! : "+ illuminaFile.getAbsolutePath());
		}
		return new FastqConsedPhdAdaptedIterator( 
        		datastore.iterator(),
                illuminaFile, 
                traceDetails.getPhdDate());
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<PhdReadRecord> createSffIterator(File sffFile,
            TraceDetails traceDetails) {
        return new FlowgramConsedPhdAdaptedIterator(
                SffFileIterator.createNewIteratorFor(sffFile),
                sffFile,
                traceDetails.getPhdDate());
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<PhdReadRecord> createFastaIterator(File fastaFile,
            TraceDetails traceDetails) {
        return new FastaConsedPhdAdaptedIterator(
                LargeNucleotideSequenceFastaIterator.createNewIteratorFor(fastaFile),
                fastaFile,
                traceDetails.getPhdDate(), PhredQuality.valueOf(30));
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<PhdReadRecord> createChromatogramIterator(
            File chromatogramFile, TraceDetails traceDetails) {
        if(traceDetails.hasFastaEdits()){
            return new EditedFastaChromatDirPhdAdapterIterator(
                    LargeNucleotideSequenceFastaIterator.createNewIteratorFor(chromatogramFile),
                    chromatogramFile, 
                    traceDetails.getPhdDate(), 
                    PhredQuality.valueOf(30), 
                    traceDetails.getChromatDir());
        }
        return new ChromatDirFastaConsedPhdAdaptedIterator(
                LargeNucleotideSequenceFastaIterator.createNewIteratorFor(chromatogramFile),
                chromatogramFile,
                traceDetails.getPhdDate(), PhredQuality.valueOf(30),
                traceDetails.getChromatDir());
    }

    /**
    * {@inheritDoc}
    * <p/>
    * Defaults to a no-op; please override this method
    * if you want to get notified of reads that that did not
    * assemble.
    */
    @Override
    protected void visitUnMatched(PhdReadRecord readRecord) throws Exception {
        //no-op
        
    }


    /**
    * {@inheritDoc}
    */
    @Override
    protected void visitMatch(CasMatch match, PhdReadRecord readRecord,
            CasPlacedRead placedRead) throws Exception {
        AcePlacedRead acePlacedRead = new AcePlacedReadAdapter(placedRead, readRecord.getPhdInfo());
        int casReferenceId = (int)match.getChosenAlignment().contigSequenceId();
        visitMatch(acePlacedRead,readRecord.getPhd(),casReferenceId);
        
    }
    
    protected abstract void visitMatch(AcePlacedRead acePlacedRead,
            Phd phd,
            int casReferenceId);

}
