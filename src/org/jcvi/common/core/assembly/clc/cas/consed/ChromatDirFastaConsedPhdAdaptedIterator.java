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
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.scf.SCFChromatogramBuilder;
import org.jcvi.common.core.seq.trace.sanger.phd.DefaultPhd;
import org.jcvi.common.core.seq.trace.sanger.phd.Phd;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.util.iter.StreamingIterator;

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
            StreamingIterator<NucleotideSequenceFastaRecord> fastaIterator,
            File fastaFile, Date phdDate, PhredQuality defaultQualityValue,
            File chromatDir) {
        super(fastaIterator, fastaFile, phdDate, defaultQualityValue);
        this.chromatDir = chromatDir;
    }
    
    /**
     * Adds the property "CHROMAT_FILE" with the value of the read id.
     */
    @Override
    protected Properties createAdditionalCommentsFor(String id,
            Properties preExistingComments) {
        Properties props = new Properties();
        //properties constructors only set defaults
        //not actually populate hash table...
        //manually put everything
        for(Entry<Object, Object> entry : preExistingComments.entrySet()){
            props.put(entry.getKey(), entry.getValue());
        }
        props.put("CHROMAT_FILE", id);
        return props;
    }


    @Override
    protected Phd createPhdRecordFor(NucleotideSequenceFastaRecord fasta,
            Properties requiredComments) {
    	Chromatogram chromo = tryToParseFromChromatDir(fasta.getId());
        if(chromo !=null){
        	 return createPhd(requiredComments, fasta, chromo);
        }
        return super.createPhdRecordFor(fasta, requiredComments);

    }

    /**
     * Look for an parse an scf chromatogram with the given
     * id.  The file to be parsed will be named
     * id only without any file extensions and must be in SCF format (since 
     * this is what consed expects).
     * @param id the id to look for in the chromat directory.
     * @return a {@link Chromatogram} object if parsing
     * is a success; or null if no chromatogram is found.
     * @throws IllegalStateException if there is a problem 
     * parsing chromatogram file.
     */
	protected Chromatogram tryToParseFromChromatDir(
			String id) {
        File chromatFile = new File(chromatDir,id);
        if(chromatFile.exists()){
            try {
            	return new SCFChromatogramBuilder(id, chromatFile)
            				.build();            
               
            } catch (Exception e) {
                throw new IllegalStateException("error parsing chromatogram for "+ id,e);
            } 
        }
		return null;
	}

    protected Phd createPhd(Properties requiredComments, NucleotideSequenceFastaRecord fasta,
            Chromatogram chromo) {
        final String id = fasta.getId();
        return new DefaultPhd(id, chromo.getNucleotideSequence(), chromo.getQualitySequence(), chromo.getPositionSequence(), requiredComments);
    }


    

   
    
    

}
