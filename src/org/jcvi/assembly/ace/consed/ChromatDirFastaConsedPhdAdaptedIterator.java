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
import java.util.Properties;

import org.jcvi.fastX.fasta.seq.NucleotideSequenceFastaRecord;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramFile;
import org.jcvi.trace.sanger.phd.DefaultPhd;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.util.CloseableIterator;
import org.joda.time.DateTime;

/**
 * @author dkatzel
 *
 *
 */
public class ChromatDirFastaConsedPhdAdaptedIterator extends FastaConsedPhdAdaptedIterator{

    private final File chromatDir;
    /**
     * @param fastaIterator
     * @param fastaFile
     * @param phdDate
     * @param defaultQualityValue
     */
    public ChromatDirFastaConsedPhdAdaptedIterator(
            CloseableIterator<NucleotideSequenceFastaRecord> fastaIterator,
            File fastaFile, DateTime phdDate, PhredQuality defaultQualityValue,
            File chromatDir) {
        super(fastaIterator, fastaFile, phdDate, defaultQualityValue);
        this.chromatDir = chromatDir;
    }
    
    
    @Override
    protected Properties createAdditionalCommentsFor(String id,
            Properties preExistingComments) {
        Properties props = new Properties(preExistingComments);
        props.put("CHROMAT_FILE", id);
        return props;
    }


    @Override
    protected Phd createPhdRecordFor(NucleotideSequenceFastaRecord nextFasta,
            Properties requiredComments) {
        final String id = nextFasta.getId();
        File chromatFile = new File(chromatDir,id);
        if(chromatFile.exists()){
            try {
                SCFChromatogramFile chromo = new SCFChromatogramFile(chromatFile);
                return new DefaultPhd(id, chromo.getBasecalls(), chromo.getQualities(), chromo.getPeaks(), requiredComments);
            } catch (Exception e) {
                throw new IllegalStateException("error parsing chromatogram for "+ id,e);
            } 
        }
        return super.createPhdRecordFor(nextFasta, requiredComments);

    }


   
    
    

}
