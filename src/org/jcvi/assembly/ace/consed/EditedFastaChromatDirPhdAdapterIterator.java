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

package org.jcvi.assembly.ace.consed;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.jcvi.fastX.fasta.pos.LargePositionFastaRecordIterator;
import org.jcvi.fastX.fasta.pos.PositionFastaRecord;
import org.jcvi.fastX.fasta.qual.LargeQualityFastaIterator;
import org.jcvi.fastX.fasta.qual.QualityFastaRecord;
import org.jcvi.fastX.fasta.seq.NucleotideSequenceFastaRecord;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.phd.DefaultPhd;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.util.CloseableIterator;
import org.joda.time.DateTime;

/**
 * EditedFastaChromatDirPhdAdapterIterator
 * @author dkatzel
 *
 *
 */
public class EditedFastaChromatDirPhdAdapterIterator extends ChromatDirFastaConsedPhdAdaptedIterator{

    private final CloseableIterator<QualityFastaRecord> qualityIterator;
    private final CloseableIterator<PositionFastaRecord<EncodedGlyphs<ShortGlyph>>> positionIterator;
    
    private QualityFastaRecord currentQualityFasta;
    private Peaks currentPeaks;
    /**
     * @param fastaIterator
     * @param fastaFile
     * @param phdDate
     * @param defaultQualityValue
     * @param chromatDir
     */
    public EditedFastaChromatDirPhdAdapterIterator(
            CloseableIterator<NucleotideSequenceFastaRecord> fastaIterator,
            File fastaFile, DateTime phdDate, PhredQuality defaultQualityValue,
            File chromatDir) {
        super(fastaIterator, fastaFile, phdDate, defaultQualityValue, chromatDir);
        qualityIterator = createQualityIterator(fastaFile);
        positionIterator = createPositionIterator(fastaFile);

    }
    /**
     * @param fastaFile
     * @return
     */
    private CloseableIterator<PositionFastaRecord<EncodedGlyphs<ShortGlyph>>> createPositionIterator(
            File fastaFile) {
        File posFile = getFileLike(fastaFile,"pos");
        if(!posFile.exists()){
            throw new IllegalStateException("could not find untrimmed position file "+ posFile.getAbsolutePath());
        }
        return LargePositionFastaRecordIterator.createNewIteratorFor(posFile);
    }
    private CloseableIterator<QualityFastaRecord> createQualityIterator(
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
        return new DefaultPhd(id, fasta.getValue(), currentQualityFasta.getValue(), 
                currentPeaks, requiredComments);
    }

    @Override
    public PhdReadRecord next() {
        currentQualityFasta = qualityIterator.next();
        currentPeaks = new Peaks(positionIterator.next().getValue());
        return super.next();
    }
    @Override
    public void close() throws IOException {
        qualityIterator.close();
        positionIterator.close();
        super.close();
    }
}
