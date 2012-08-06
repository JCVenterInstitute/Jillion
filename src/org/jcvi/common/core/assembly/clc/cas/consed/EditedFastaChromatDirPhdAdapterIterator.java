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
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.pos.LargePositionFastaRecordIterator;
import org.jcvi.common.core.seq.fastx.fasta.pos.PositionSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.qual.LargeQualityFastaIterator;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaRecord;
import org.jcvi.common.core.seq.read.trace.sanger.PositionSequence;
import org.jcvi.common.core.seq.read.trace.sanger.PositionSequenceBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.phd.DefaultPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * EditedFastaChromatDirPhdAdapterIterator
 * @author dkatzel
 *
 *
 */
public class EditedFastaChromatDirPhdAdapterIterator extends ChromatDirFastaConsedPhdAdaptedIterator{

    private final StreamingIterator<QualitySequenceFastaRecord> qualityIterator;
    private final StreamingIterator<PositionSequenceFastaRecord<Sequence<ShortSymbol>>> positionIterator;
    
    private QualitySequence currentQualitySequence;
    private PositionSequence currentPositions;
    /**
     * @param fastaIterator
     * @param fastaFile
     * @param phdDate
     * @param defaultQualityValue
     * @param chromatDir
     */
    public EditedFastaChromatDirPhdAdapterIterator(
            StreamingIterator<NucleotideSequenceFastaRecord> fastaIterator,
            File fastaFile, Date phdDate, PhredQuality defaultQualityValue,
            File chromatDir) {
        super(fastaIterator, fastaFile, phdDate, defaultQualityValue, chromatDir);
        qualityIterator = createQualityIterator(fastaFile);
        positionIterator = createPositionIterator(fastaFile);

    }
    /**
     * @param fastaFile
     * @return
     */
    private StreamingIterator<PositionSequenceFastaRecord<Sequence<ShortSymbol>>> createPositionIterator(
            File fastaFile) {
        File posFile = getFileLike(fastaFile,"pos");
        if(!posFile.exists()){
            throw new IllegalStateException("could not find untrimmed position file "+ posFile.getAbsolutePath());
        }
        return LargePositionFastaRecordIterator.createNewIteratorFor(posFile);
    }
    private StreamingIterator<QualitySequenceFastaRecord> createQualityIterator(
            File fastaFile) {
        File qualityFile = getFileLike(fastaFile,"qual");
        if(!qualityFile.exists()){
            throw new IllegalStateException("could not find untrimmed quality file "+ qualityFile.getAbsolutePath());
        }
        return LargeQualityFastaIterator.createNewIteratorFor(qualityFile);
    }
    /**
     * @param fastaFile
     * @param string
     * @return
     */
    private File getFileLike(File fastaFile, String type) {
        String fileName=fastaFile.getName().replaceAll("_final.fasta.untrimmed$", "_final."+type+".untrimmed");
        return new File(fastaFile.getParent(),fileName);
    }
    @Override
    protected Phd createPhd(Properties requiredComments, NucleotideSequenceFastaRecord fasta,
            SCFChromatogram chromo) {
        final String id = fasta.getId();
        return new DefaultPhd(id, fasta.getSequence(), currentQualitySequence, 
        		currentPositions, requiredComments);
    }

    @Override
    public PhdReadRecord next() {
        currentQualitySequence = qualityIterator.next().getSequence();
        Sequence<ShortSymbol> sequence = positionIterator.next().getSequence();
        PositionSequenceBuilder builder = new PositionSequenceBuilder((int)sequence.getLength());
        for(ShortSymbol s : sequence){
        	builder.append(IOUtil.toUnsignedShort(s.getValue().shortValue()));
        }
        currentPositions = builder.build();
        return super.next();
    }
    @Override
    public void close() throws IOException {
        qualityIterator.close();
        positionIterator.close();
        super.close();
    }
}
