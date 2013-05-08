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
package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;

/**
 * @author dkatzel
 *
 *
 */
public class ChromatDirFastaConsedPhdAdaptedIterator extends QualFastaConsedPhdAdaptedIterator{

    private final File chromatDir;
    /**
     * @param fastaIterator
     * @param fastaFile
     * @param phdDate
     * @param defaultQualityValue
     */
    public ChromatDirFastaConsedPhdAdaptedIterator(
            StreamingIterator<NucleotideFastaRecord> fastaIterator,
            File fastaFile, Date phdDate, PhredQuality defaultQualityValue,
            File chromatDir) {
        super(fastaIterator, fastaFile, phdDate, defaultQualityValue);
        this.chromatDir = chromatDir;
    }
    
    /**
     * Adds the property "CHROMAT_FILE" with the value of the read id.
     */
    @Override
    protected  Map<String,String> createAdditionalCommentsFor(String id) {
    	 Map<String,String> props = new HashMap<String, String>();        
        props.put("CHROMAT_FILE", id);
        return props;
    }


    @Override
    protected Phd createPhdRecordFor(NucleotideFastaRecord fasta,
    		 Map<String,String> requiredComments) {
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
            	return new ScfChromatogramBuilder(id, chromatFile)
            				.build();            
               
            } catch (Exception e) {
                throw new IllegalStateException("error parsing chromatogram for "+ id,e);
            } 
        }
		return null;
	}

    protected Phd createPhd( Map<String,String> requiredComments, NucleotideFastaRecord fasta,
            Chromatogram chromo) {
        final String id = fasta.getId();
       
        return new PhdBuilder(id, chromo.getNucleotideSequence(), chromo.getQualitySequence())
        						.peaks(chromo.getPositionSequence())
        						.comments(requiredComments)
        						.build();
    
    }


    

   
    
    

}
