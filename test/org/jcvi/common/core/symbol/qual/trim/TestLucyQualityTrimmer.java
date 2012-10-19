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

package org.jcvi.common.core.symbol.qual.trim;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileDataStoreType;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaFileDataStoreFactory;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.trim.LucyLikeQualityTrimmer;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestLucyQualityTrimmer {
    private final ResourceFileServer resources;
    private QualitySequenceDataStore qualities;
    
    LucyLikeQualityTrimmer sut = new LucyLikeQualityTrimmer.Builder(30)
                            .addTrimWindow(30, 0.1F)
                            .addTrimWindow(10, 0.35F)
                            .build();
    
    public TestLucyQualityTrimmer(){
    	resources = new ResourceFileServer(TestLucyQualityTrimmer.class);
    }
    @Before
    public void setup() throws  IOException{
        File qualFile = resources.getFile("files/fullLength.qual");
		qualities = FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, 
				QualitySequenceFastaFileDataStoreFactory.create(qualFile, FastaFileDataStoreType.MAP_BACKED));
    }
    
    @Test
    public void SAJJA07T27G07MP1F() throws DataStoreException{
        final QualitySequence fullQualities = qualities.get("SAJJA07T27G07MP1F");
        Range actualTrimRange = sut.trim(fullQualities);
        Range expectedRange = Range.of(CoordinateSystem.RESIDUE_BASED, 12,679);
        assertEquals(expectedRange, actualTrimRange);
    }
    @Test
    public void SAJJA07T27G07MP675R() throws DataStoreException{
        final QualitySequence fullQualities = qualities.get("SAJJA07T27G07MP675R");
        Range actualTrimRange = sut.trim(fullQualities);
        Range expectedRange = Range.of(CoordinateSystem.RESIDUE_BASED, 16,680);
        assertEquals(expectedRange, actualTrimRange);
    }
    
    @Test
    public void noGoodQualityDataShouldReturnEmptyRange() throws FileNotFoundException, IOException, DataStoreException{
        File qualFile = resources.getFile("files/bad.qual");
		QualitySequenceDataStore badQualDataStore =FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, 
				QualitySequenceFastaFileDataStoreFactory.create(qualFile, FastaFileDataStoreType.MAP_BACKED));
        final QualitySequence badQualities = badQualDataStore.get("SCJIA01T48H08PB26F");
        assertEquals(new Range.Builder().build(), sut.trim(badQualities));
    }
    
    @Test
    public void bTrashShouldReturnEmptyRange() throws FileNotFoundException, IOException, DataStoreException{
        File qualFile = resources.getFile("files/trash.qual");
		QualitySequenceDataStore trashQualDataStore =FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, 
				QualitySequenceFastaFileDataStoreFactory.create(qualFile, FastaFileDataStoreType.MAP_BACKED));
        final QualitySequence trashQualities = trashQualDataStore.get("JBYHA01T19A06PB2A628FB");
        assertEquals(new Range.Builder().build(), sut.trim(trashQualities));
    }
    
    @Test
    public void wTrashShouldReturnEmptyRange() throws FileNotFoundException, IOException, DataStoreException{
        File qualFile = resources.getFile("files/trash.qual");
		QualitySequenceDataStore trashQualDataStore =FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, 
				QualitySequenceFastaFileDataStoreFactory.create(qualFile, FastaFileDataStoreType.MAP_BACKED));
        final QualitySequence trashQualities = trashQualDataStore.get("JBZTB06T19E09NA1F");
        assertEquals(new Range.Builder().build(), sut.trim(trashQualities));
    }
}
