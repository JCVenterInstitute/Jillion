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
 * Created on Mar 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.easymock.classextension.ConstructorArgs;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.qualClass.QualityClass;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestDefaultContigQualityClassComputer {
    
    
    byte delta = (byte)1;
    int index = 1234;
    PhredQuality threshold = PhredQuality.valueOf((byte)30);
    PhredQuality lowQuality = threshold.decreaseBy(delta);
    PhredQuality highQuality = threshold.increaseBy(delta);
    
    QualityValueStrategy qualityValueStrategy;
    DefaultContigQualityClassComputer  sut;
    CoverageMap<CoverageRegion<Placed>> coverageMap;
    DataStore<EncodedGlyphs<PhredQuality>> qualityFastaMap;
    EncodedGlyphs<NucleotideGlyph> consensus;
    @Before
    public void setup() throws SecurityException, NoSuchMethodException{
        qualityValueStrategy = createMock(QualityValueStrategy.class);
        ConstructorArgs args = new ConstructorArgs(
                DefaultContigQualityClassComputer.class.getDeclaredConstructor(
                        QualityValueStrategy.class, 
                        PhredQuality.class),
                        qualityValueStrategy,
                        threshold
                        );
        sut = createMock(DefaultContigQualityClassComputer.class, args, 
                DefaultContigQualityClassComputer.class.getDeclaredMethod("computeQualityClassFor", 
                        new Class[]{DataStore.class,Integer.TYPE, CoverageRegion.class, NucleotideGlyph.class}));
        
        
                
        coverageMap = createMock(CoverageMap.class);
        qualityFastaMap = createMock(DataStore.class);
        consensus = createMock(EncodedGlyphs.class);
    }
    
    @Test
    public void indexOutsideOfCoverageMapShouldReturnQualityClassZero(){
        
        expect(coverageMap.getRegionWhichCovers(index)).andReturn(null);
        replay(coverageMap,qualityFastaMap,consensus);
        assertEquals(QualityClass.valueOf((byte)0), 
                sut.computeQualityClass(coverageMap, qualityFastaMap, consensus, index));
        verify(coverageMap,qualityFastaMap,consensus);
    }
    
    @Test
    public void computeQualityClass() throws DataStoreException{
        final NucleotideGlyph consensusBase = NucleotideGlyph.Adenine;
        CoverageRegion<Placed> region = createMock(CoverageRegion.class);
        expect(coverageMap.getRegionWhichCovers(index)).andReturn(region);
        
        expect(consensus.get(index)).andReturn(consensusBase);
        QualityClass expectedQualityClass = QualityClass.NO_CONFLICT_HIGH_QUAL_BOTH_DIRS;
        expect(sut.computeQualityClassFor(qualityFastaMap,index,region, consensusBase)).andReturn(expectedQualityClass);
        
        replay(sut, coverageMap, qualityFastaMap,consensus, region);
        assertEquals(expectedQualityClass, sut.computeQualityClass(coverageMap, qualityFastaMap, consensus, index));
        verify(sut, coverageMap, qualityFastaMap,consensus, region);
        
        
    }
}
