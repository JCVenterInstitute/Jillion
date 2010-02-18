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
/*
 * Created on May 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fasta.QualityFastaRecord;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class SnpXMLWriter<T extends PlacedRead>{
    private static final String SNP_FORMAT = "<snp fullRangePosition=\"%d\" basecall=\"%s\" quality = \"%d\" offset =\"%d\" consensus = \"%s\"/>\n";
   
    private static final String READ_BEGIN_TAG = "<read id= \"%s\">\n";
    private static final String READ_END_TAG = "</read>\n";
    
    private final QualityValueStrategy qualityValueStrategy;
   
    public SnpXMLWriter(QualityValueStrategy qualityValueStrategy){
        this.qualityValueStrategy = qualityValueStrategy;
    }
    
    public void write(OutputStream out, ContigCheckerStruct<T> struct ) throws IOException, DataStoreException{
        final NucleotideEncodedGlyphs consensus = struct.getContig().getConsensus();
        
        for(PlacedRead read: struct.getContig().getPlacedReads()){
            EncodedGlyphs<PhredQuality> fullQualities =struct.getQualityDataStore().get(read.getId());
            if(fullQualities !=null){
            final Map<Integer, NucleotideGlyph> snps = read.getSnps();
                           
            StringBuilder snpBuilder = new StringBuilder();
            for(Entry<Integer, NucleotideGlyph> snp : snps.entrySet()){            
                Integer gappedIndex =snp.getKey();
                final NucleotideGlyph snpBasecall = snp.getValue();
                if(!snpBasecall.isGap()){
                    PhredQuality qualityValue =qualityValueStrategy.getQualityFor(read, fullQualities, gappedIndex);
                    int consensusOffset = (int)(read.getStart()+gappedIndex);
                    int fullRangeIndex = read.getEncodedGlyphs().convertGappedValidRangeIndexToUngappedValidRangeIndex(gappedIndex);
                    snpBuilder.append(String.format(SNP_FORMAT, 
                            fullRangeIndex,
                            snpBasecall,
                            qualityValue.getNumber(),
                            consensusOffset,
                            consensus.get(consensusOffset)
                    ));
                }
                    
            }
            if(snpBuilder.length()>0){
                out.write(String.format(READ_BEGIN_TAG, read.getId()).getBytes());
                out.write(snpBuilder.toString().getBytes());
                out.write(READ_END_TAG.getBytes());
            }
        }
            
            
            
        }
    }
}
