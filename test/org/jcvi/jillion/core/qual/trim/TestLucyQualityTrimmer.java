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
package org.jcvi.jillion.core.qual.trim;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.qual.trim.LucyQualityTrimmerBuilder;
import org.jcvi.jillion.core.qual.trim.QualityTrimmer;
import org.jcvi.jillion.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.jillion.fasta.qual.QualityFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestLucyQualityTrimmer {
    private final ResourceHelper resources;
    private QualitySequenceDataStore qualities;
    
    QualityTrimmer sut = new LucyQualityTrimmerBuilder(30)
                            .addTrimWindow(30, 0.1F)
                            .addTrimWindow(10, 0.35F)
                            .build();
    
    public TestLucyQualityTrimmer(){
    	resources = new ResourceHelper(TestLucyQualityTrimmer.class);
    }
    @Before
    public void setup() throws  IOException{
        File qualFile = resources.getFile("files/fullLength.qual");
		qualities = FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, 
				new QualityFastaFileDataStoreBuilder(qualFile).build());
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
				new QualityFastaFileDataStoreBuilder(qualFile).build());
        final QualitySequence badQualities = badQualDataStore.get("SCJIA01T48H08PB26F");
        assertEquals(new Range.Builder().build(), sut.trim(badQualities));
    }
    
    @Test
    public void bTrashShouldReturnEmptyRange() throws FileNotFoundException, IOException, DataStoreException{
        File qualFile = resources.getFile("files/trash.qual");
		QualitySequenceDataStore trashQualDataStore =FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, 
				new QualityFastaFileDataStoreBuilder(qualFile).build());
        final QualitySequence trashQualities = trashQualDataStore.get("JBYHA01T19A06PB2A628FB");
        assertEquals(new Range.Builder().build(), sut.trim(trashQualities));
    }
    
    @Test
    public void wTrashShouldReturnEmptyRange() throws FileNotFoundException, IOException, DataStoreException{
        File qualFile = resources.getFile("files/trash.qual");
		QualitySequenceDataStore trashQualDataStore =FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, 
				new QualityFastaFileDataStoreBuilder(qualFile).build());
        final QualitySequence trashQualities = trashQualDataStore.get("JBZTB06T19E09NA1F");
        assertEquals(new Range.Builder().build(), sut.trim(trashQualities));
    }
}
