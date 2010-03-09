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
 * Created on Feb 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.qualClass.QualityClass;
import org.jcvi.sequence.SequenceDirection;

public class DefaultQualityClassContigMap<P extends PlacedRead> implements Iterable<QualityClassRegion>{

    List<QualityClassRegion> qualityClassRegions;
    
   

    public DefaultQualityClassContigMap(
                    CoverageMap<CoverageRegion<P>> coverageMap, 
                    EncodedGlyphs<NucleotideGlyph> consensus,
                    DataStore<EncodedGlyphs<PhredQuality>> qualityFastaMap, 
                    QualityClassComputer<P,NucleotideGlyph> qualityClassComputer){
        qualityClassRegions = new ArrayList<QualityClassRegion>();
        QualityClass qualityClass =null;
        int qualityClassStart=0;
        for(int i =0; i<consensus.getLength(); i++){
            final QualityClass currentQualityClass = qualityClassComputer.computeQualityClass(coverageMap, 
                                                                qualityFastaMap, consensus, i);
            if(isDifferentQualityClass(qualityClass, currentQualityClass)){
                if(qualityClass!=null){
                    qualityClassRegions.add(new QualityClassRegion(qualityClass, Range.buildRange(qualityClassStart, i-1 )));
                }
                qualityClass = currentQualityClass;
                qualityClassStart=i;
            }
        }
        final long lastConsensusIndex = consensus.getLength()-1;
        if(qualityClassStart <=lastConsensusIndex){
            qualityClassRegions.add(new QualityClassRegion(qualityClass, Range.buildRange(qualityClassStart, lastConsensusIndex)));
        }
    }

   

    private boolean isDifferentQualityClass(QualityClass qualityClass,
            QualityClass currentQualityClass) {
        return qualityClass !=currentQualityClass;
    }

    protected void addRead(QualityClass.Builder builder,
            boolean agreesWithConsensus, boolean isHighQuality,
            SequenceDirection direction) {
        if(agreesWithConsensus){
            if(isHighQuality){
                builder.addHighQualityAgreement(direction);
            }
            else{
                builder.addLowQualityAgreement(direction);
            }
        }
        else{
            if(isHighQuality){
                builder.addHighQualityConflict(direction);
            }
            else{
                builder.addLowQualityConflict(direction);
            }
        }
    }
    
    public List<QualityClassRegion> getQualityClassRegions() {
        return qualityClassRegions;
    }



    @Override
    public Iterator<QualityClassRegion> iterator() {
        return qualityClassRegions.iterator();
    }
    
    
}
