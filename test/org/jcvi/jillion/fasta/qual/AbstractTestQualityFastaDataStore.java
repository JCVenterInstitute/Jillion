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
/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.qual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestQualityFastaDataStore {

    private static final String QUAL_FILE_PATH = "files/19150.qual";
    
    
    QualitySequenceFastaRecord JGBAA02T21A12PB1A1F = 
    		new QualitySequenceFastaRecordBuilder(
                    "JGBAA02T21A12PB1A1F",
                   new QualitySequenceBuilder(
                                    new byte[]{
                                            7, 7, 6, 6, 6, 7, 7, 7, 8, 8, 13, 12, 14, 8, 8, 11, 12,
                                            12, 14, 14, 19, 19, 19, 19, 21, 19, 17, 25, 30, 23, 23, 21, 23, 23,
                                            19, 19, 21, 23, 23, 19, 24, 33, 30, 34, 26, 38, 27, 23, 24, 22, 21,
                                            21, 34, 36, 28, 28, 26, 32, 30, 24, 23, 29, 32, 32, 32, 38, 23, 23,
                                            10, 10, 7, 9, 9, 9, 9, 10, 15, 22, 32, 32, 29, 29, 34, 34, 33,
                                            33, 27, 30, 23, 23, 27, 29, 29, 38, 34, 38, 44, 44, 29, 29, 28, 40,
                                            32, 40, 28, 26, 20, 29, 26, 32, 19, 19, 9, 10, 16, 22, 30, 38, 30,
                                            23, 24, 24, 24, 24, 32, 47, 47, 40, 36, 33, 27, 28, 26, 29, 24, 31,
                                            33, 31, 33, 31, 35, 44, 33, 34, 34, 29, 34, 45, 45, 40, 32, 27, 39,
                                            18, 19, 19, 39, 30, 36, 32, 32, 28, 23, 28, 28, 28, 32, 39, 45, 47,
                                            47, 38, 35, 35, 34, 26, 30, 33, 38, 34, 38, 45, 45, 47, 47, 47, 44,
                                            35, 24, 27, 14, 13, 27, 23, 29, 39, 40, 29, 29, 38, 38, 38, 38, 47,
                                            47, 47, 47, 47, 47, 47, 47, 47, 47, 45, 45, 32, 34, 40, 40, 32, 40,
                                            38, 44, 38, 44, 44, 38, 47, 47, 38, 40, 40, 40, 40, 40, 34, 35, 38,
                                            34, 34, 38, 40, 34, 36, 38, 38, 38, 34, 34, 35, 49, 38, 38, 31, 31,
                                            38, 29, 38, 38, 47, 47, 47, 45, 45, 49, 25, 23, 27, 17, 16, 16, 29,
                                            27, 40, 49, 49, 27, 28, 40, 40, 38, 40, 36, 36, 28, 40, 40, 40, 47,
                                            47, 36, 45, 49, 49, 32, 35, 32, 32, 40, 23, 28, 32, 28, 32, 45, 45,
                                            36, 40, 45, 32, 40, 28, 28, 26, 26, 28, 30, 29, 34, 40, 36, 40, 44,
                                            44, 36, 40, 36, 36, 49, 32, 49, 38, 40, 40, 38, 34, 32, 45, 40, 32,
                                            27, 27, 23, 24, 23, 14, 11, 13, 20, 27, 39, 36, 39, 40, 32, 32, 28,
                                            32, 45, 45, 40, 32, 32, 28, 28, 49, 32, 40, 36, 36, 40, 40, 36, 49,
                                            49, 40, 40, 47, 36, 23, 29, 21, 10, 10, 45, 24, 49, 28, 40, 27, 29,
                                            17, 25, 39, 36, 32, 39, 23, 23, 25, 40, 32, 40, 38, 40, 32, 34, 36,
                                            36, 40, 32, 40, 28, 28, 40, 40, 40, 36, 28, 40, 28, 28, 28, 49, 32,
                                            34, 36, 40, 34, 45, 45, 38, 40, 38, 38, 40, 40, 28, 40, 39, 36, 36,
                                            40, 40, 45, 35, 32, 39, 28, 28, 28, 23, 27, 27, 27, 40, 44, 38, 47,
                                            45, 30, 30, 21, 21, 39, 30, 36, 49, 32, 49, 49, 49, 39, 47, 47, 47,
                                            47, 47, 49, 47, 47, 36, 39, 39, 34, 34, 45, 39, 39, 40, 27, 27, 28,
                                            28, 40, 36, 39, 39, 29, 29, 26, 30, 28, 26, 27, 49, 32, 34, 49, 40,
                                            32, 32, 40, 23, 23, 40, 32, 40, 40, 38, 38, 47, 47, 38, 38, 38, 38,
                                            38, 44, 38, 38, 40, 39, 40, 32, 40, 40, 39, 45, 23, 25, 29, 21, 20,
                                            40, 40, 40, 40, 49, 40, 40, 40, 45, 40, 40, 45, 38, 40, 49, 27, 49,
                                            27, 27, 27, 39, 39, 32, 40, 29, 39, 24, 28, 49, 28, 28, 36, 32, 32,
                                            32, 23, 28, 40, 36, 32, 32, 49, 34, 34, 44, 38, 45, 45, 36, 38, 40,
                                            34, 44, 38, 36, 29, 27, 19, 19, 29, 28, 28, 26, 26, 21, 19, 45, 45,
                                            47, 36, 36, 36, 32, 28, 26, 16, 12, 9, 6, 7, 8, 7, 11, 12, 14,
                                            10, 10, 6, 6, 6, 6, 6, 9
                                    }).build())
    				.build();
    
    
    QualitySequenceFastaRecord JGBAA07T21D08MP605F = 
    		new QualitySequenceFastaRecordBuilder(
                "JGBAA07T21D08MP605F",
                new QualitySequenceBuilder(
                                new byte[]{
                                        6,9,6,6,6,6,9,6,6,10,8,8,8,6,7,8,12,
                                        12,14,12,12,9,9,12,13,13,13,13,13,13,12,21,31,30,
                                        31,44,44,44,34,34,34,24,20,20,24,20,20,31,35,31,33,
                                        27,24,23,24,30,22,22,26,26,26,32,32,29,28,21,20,29,
                                        9,9,9,9,9,9,12,13,16,40,40,28,24,24,20,22,17,
                                        14,16,19,39,44,38,40,39,26,23,26,32,28,24,22,21,21,
                                        23,23,24,33,38,38,45,49,45,29,24,24,26,29,29,33,33,
                                        33,33,30,33,35,35,45,38,36,29,21,18,20,16,16,29,45,
                                        34,29,29,29,28,28,31,40,47,47,47,45,45,38,47,47,38,
                                        45,34,40,40,47,47,47,38,34,34,29,26,26,36,39,40,40,
                                        40,45,45,49,45,49,32,40,40,34,32,45,40,36,39,39,39,
                                        28,32,28,45,45,45,40,34,34,29,45,34,38,38,47,38,47,
                                        38,44,45,40,40,32,40,39,41,40,38,32,34,40,49,38,40,
                                        32,36,44,38,47,47,38,38,38,36,32,32,49,49,36,40,47,
                                        47,44,45,38,38,38,29,40,39,40,40,26,29,14,12,23,23,
                                        49,49,27,28,28,49,40,28,40,45,38,38,38,40,34,36,45,
                                        49,40,40,40,40,40,34,32,34,49,49,34,49,36,49,38,40,
                                        40,34,32,40,28,49,32,35,39,40,40,34,40,38,34,27,29,
                                        23,18,17,29,29,29,23,21,27,49,27,32,28,32,28,40,39,
                                        36,38,38,35,28,32,26,28,23,27,29,40,36,32,29,27,27,
                                        27,27,27,27,45,49,49,28,28,28,28,35,38,38,38,40,32,
                                        40,40,21,20,28,23,19,27,27,28,40,40,36,41,47,47,36,
                                        45,45,47,36,39,49,32,27,28,49,34,40,40,28,40,28,32,
                                        45,45,45,38,40,40,38,45,36,32,35,40,40,38,45,40,40,
                                        32,27,27,20,18,32,30,34,40,12,8,9,6,6,6,6,8,
                                        11,19,12,7,7,6,6,6
                                }).build())
    					.build();
    
    
    QualitySequenceFastaRecord JGBAA01T21H05PB2A2341BRB = 
        new QualitySequenceFastaRecordBuilder(
                "JGBAA01T21H05PB2A2341BRB",
                new QualitySequenceBuilder(
                                new byte[]{
                                        6, 6, 6, 6, 7, 7, 7, 8, 12, 12, 12, 14, 17, 14, 14, 14, 22,
                                        14, 16, 17, 17, 14, 14, 14, 12, 12, 23, 22, 21, 17, 17, 16, 17, 19,
                                        17, 17, 17, 24, 28, 19, 35, 35, 29, 35, 35, 34, 34, 44, 44, 44, 35,
                                        35, 35, 35, 35, 44, 47, 47, 47, 47, 45, 38, 45, 44, 35, 27, 19, 13,
                                        14, 7, 9, 9, 9, 9, 9, 13, 13, 17, 25, 19, 27, 40, 40, 39, 39,
                                        28, 40, 40, 40, 47, 41, 38, 38, 40, 40, 40, 40, 30, 29, 26, 26, 32,
                                        32, 38, 38, 34, 29, 29, 24, 21, 16, 13, 9, 9, 9, 14, 20, 32, 36,
                                        40, 32, 40, 28, 21, 34, 29, 44, 40, 40, 41, 41, 38, 44, 45, 44, 38,
                                        44, 47, 47, 47, 47, 45, 49, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47,
                                        40, 44, 44, 44, 40, 38, 38, 40, 38, 38, 47, 47, 47, 47, 47, 41, 47,
                                        39, 39, 19, 19, 39, 39, 47, 47, 44, 47, 47, 47, 41, 38, 44, 49, 49,
                                        49, 49, 45, 49, 41, 47, 41, 47, 47, 41, 49, 49, 47, 49, 49, 49, 45,
                                        41, 47, 47, 41, 47, 47, 47, 40, 40, 44, 44, 35, 44, 47, 47, 47, 47,
                                        47, 47, 47, 47, 41, 47, 36, 47, 47, 47, 47, 47, 47, 47, 47, 41, 47,
                                        47, 47, 47, 47, 47, 38, 47, 47, 47, 47, 47, 44, 44, 44, 35, 44, 38,
                                        47, 47, 47, 47, 41, 47, 47, 41, 47, 47, 47, 47, 41, 44, 44, 38, 44,
                                        38, 44, 49, 45, 47, 47, 47, 41, 47, 47, 47, 45, 47, 41, 41, 47, 47,
                                        47, 47, 41, 47, 47, 47, 39, 39, 17, 17, 39, 39, 47, 38, 38, 47, 47,
                                        47, 47, 47, 47, 47, 41, 47, 47, 47, 47, 47, 41, 47, 49, 47, 41, 38,
                                        47, 47, 47, 47, 41, 47, 47, 41, 45, 47, 47, 47, 47, 47, 47, 44, 47,
                                        47, 44, 45, 44, 45, 38, 33, 34, 40, 44, 38, 38, 45, 49, 49, 47, 47,
                                        47, 47, 47, 47, 47, 47, 47, 47, 38, 47, 47, 47, 47, 47, 47, 47, 49,
                                        45, 47, 47, 47, 47, 47, 47, 41, 47, 41, 41, 41, 47, 47, 47, 47, 47,
                                        47, 47, 41, 47, 47, 41, 41, 47, 47, 47, 47, 47, 38, 47, 47, 47, 38,
                                        47, 44, 47, 47, 47, 47, 41, 47, 47, 47, 47, 47, 47, 49, 49, 49, 49,
                                        49, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 49, 49, 47,
                                        47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 41,
                                        47, 47, 47, 41, 49, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47,
                                        47, 47, 47, 41, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 49,
                                        49, 47, 47, 47, 47, 47, 47, 49, 49, 49, 41, 47, 47, 47, 47, 47, 47,
                                        49, 49, 45, 49, 49, 47, 45, 47, 47, 47, 47, 44, 38, 47, 47, 47, 47,
                                        47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 47, 38, 38, 40, 44, 45, 44,
                                        47, 47, 47, 45, 40, 47, 47, 40, 45, 38, 44, 38, 44, 40, 40, 40, 40,
                                        40, 45, 47, 44, 44, 44, 38, 38, 38, 47, 47, 47, 47, 47, 44, 36, 47,
                                        47, 45, 44, 40, 38, 44, 47, 41, 38, 44, 45, 27, 26, 13, 16, 39, 39,
                                        47, 47, 41, 47, 49, 49, 47, 47, 41, 47, 41, 47, 49, 49, 44, 38, 44,
                                        44, 44, 44, 47, 47, 47, 47, 47, 36, 47, 47, 41, 47, 47, 47, 47, 47,
                                        47, 49, 49, 47, 47, 47, 47, 44, 47, 47, 49, 49, 47, 47, 47, 47, 47,
                                        47, 47, 38, 47, 47, 45, 45, 44, 44, 44, 45, 47, 44, 44, 44, 40, 44,
                                        44, 44, 44, 44, 45, 44, 44, 47, 38, 44, 45, 40, 40, 45, 40, 40, 39,
                                        20, 20, 20, 27, 17, 17, 9, 9, 20, 9, 9, 27, 24, 35, 26, 30, 33,
                                        33, 33, 35, 44, 44, 22, 16, 9, 7, 7
                                }).build())
    						.build();
    ResourceHelper RESOURCES = new ResourceHelper(AbstractTestQualityFastaDataStore.class);
    @Test
    public void parseFile() throws IOException, DataStoreException{
    	File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
    	QualitySequenceFastaDataStore sut = createDataStore(qualFile);
        assertEquals(321, sut.getNumberOfRecords());
        assertEquals(JGBAA02T21A12PB1A1F, sut.get("JGBAA02T21A12PB1A1F"));
        assertEquals(JGBAA07T21D08MP605F, sut.get("JGBAA07T21D08MP605F"));
        assertEquals(JGBAA01T21H05PB2A2341BRB, sut.get("JGBAA01T21H05PB2A2341BRB"));
    }
    
    protected abstract QualitySequenceFastaDataStore createDataStore(File file) throws IOException;
    
    @Test
    public void idIterator() throws IOException, DataStoreException{
    	File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
		QualitySequenceFastaDataStore sut = createDataStore(qualFile);
    	Iterator<String> expectedIdsIter = createExpectedIdsFrom(qualFile);
    	Iterator<String> actual = sut.idIterator();
    	assertTrue(expectedIdsIter.hasNext());
    	while(expectedIdsIter.hasNext()){
    		assertTrue(actual.hasNext());
    		assertEquals(expectedIdsIter.next(), actual.next());
    	}
    	assertFalse(actual.hasNext());
    }
    @Test
    public void iterator() throws IOException, DataStoreException{
    	File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
    	QualitySequenceFastaDataStore sut = createDataStore(qualFile);
    	Iterator<QualitySequenceFastaRecord> iterator = sut.iterator();
    	boolean hasJGBAA02T21A12PB1A1F = false;
    	boolean hasJGBAA07T21D08MP605F = false;
    	boolean hasJGBAA01T21H05PB2A2341BRB = false;
    	
    	while(iterator.hasNext()){
    		QualitySequenceFastaRecord next = iterator.next();
    		if(next.equals(JGBAA02T21A12PB1A1F)){
    			hasJGBAA02T21A12PB1A1F=true;
    		}else if(next.equals(JGBAA07T21D08MP605F)){
    			hasJGBAA07T21D08MP605F=true;
    		}else if(next.equals(JGBAA01T21H05PB2A2341BRB)){
    			hasJGBAA01T21H05PB2A2341BRB=true;
    		}
    	}
    	assertTrue(hasJGBAA02T21A12PB1A1F);
    	assertTrue(hasJGBAA07T21D08MP605F);
    	assertTrue(hasJGBAA01T21H05PB2A2341BRB);
    }
    @Test
    public void closingDataStoreAfterIteratingShouldActLikeNoMoreElements() throws IOException, DataStoreException{
    	File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
    	QualitySequenceFastaDataStore sut = createDataStore(qualFile);
    	StreamingIterator<QualitySequenceFastaRecord> iter = null;
    	try{
	    	iter =sut.iterator();
	    	while(iter.hasNext()){
	    		iter.next();
	    	}
	    	sut.close();
	    	assertFalse(iter.hasNext());
	    	try{
	    		iter.next();
	    		fail("should throw NoSuchElementException");
	    	}catch(NoSuchElementException expected){
	    		//expected
	    	}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
    @Test
    public void closingDataStoreDuringIterationShouldThrowExceptionOnHasNext() throws IOException, DataStoreException{
    	File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
    	QualitySequenceFastaDataStore sut = createDataStore(qualFile);
    	StreamingIterator<QualitySequenceFastaRecord> iter = null;
    	try{
	    	iter =sut.iterator();
	    	
	    	assertTrue(iter.hasNext());
	    	assertNotNull(iter.next());
	    	sut.close();
	    	try{
	    		iter.hasNext();
	    		fail("hasNext() should throw exception if datastore is closed");
	    	}catch(DataStoreClosedException expected){
	    		//expected
	    	}
	    	
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
    @Test
    public void closingDataStoreDuringIterationShouldThrowExceptionOnNext() throws IOException, DataStoreException{
    	File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
    	QualitySequenceFastaDataStore sut = createDataStore(qualFile);
    	StreamingIterator<QualitySequenceFastaRecord> iter = null;
    	try{
	    	iter =sut.iterator();
	    	
	    	assertTrue(iter.hasNext());
	    	assertNotNull(iter.next());
	    	sut.close();
	    	
	    	try{
	    		iter.next();
	    		fail("next() should throw exception if datastore is closed");
	    	}catch(DataStoreClosedException expected){
	    		//expected
	    	}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
    @Test
    public void closingIdIteratorDuringIterationShouldThrowExceptionOnNext() throws IOException, DataStoreException{
    	File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
		QualitySequenceFastaDataStore sut = createDataStore(qualFile);
    	StreamingIterator<String> iter = null;
    	try{
	    	iter =sut.idIterator();
	    	assertTrue(iter.hasNext());
	    	assertNotNull(iter.next());
	    	sut.close();
	    	try{
	    		iter.next();
	    		fail("next() should throw exception if datastore is closed");
	    	}catch(DataStoreClosedException expected){
	    		//expected
	    	}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
    
    @Test
    public void closingIdIteratorDuringIterationShouldThrowExceptionOnHasNext() throws IOException, DataStoreException{
    	File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
		QualitySequenceFastaDataStore sut = createDataStore(qualFile);
    	StreamingIterator<String> iter = null;
    	try{
	    	iter =sut.idIterator();
	    	assertTrue(iter.hasNext());
	    	assertNotNull(iter.next());
	    	sut.close();
	    	
	    	try{
	    		iter.hasNext();
	    		fail("hasNext() should throw exception if datastore is closed");
	    	}catch(DataStoreClosedException expected){
	    		//expected
	    	}
	    	
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
    @Test
    public void close() throws IOException, DataStoreException{
    	File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
    	QualitySequenceFastaDataStore sut = createDataStore(qualFile);
    	sut.close();
    	assertTrue(sut.isClosed());
    	
    	try{
    		sut.get("something");
    		fail("should fail if already closed");
    	}catch(IllegalStateException expected){
    		//expected
    	}
    }

	private Iterator<String> createExpectedIdsFrom(File qualFile) throws FileNotFoundException {
		Scanner scanner = new Scanner(qualFile, IOUtil.UTF_8_NAME);
		Pattern pattern = Pattern.compile("^>(\\S+)");
		List<String> ids = new ArrayList<String>();
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			Matcher matcher = pattern.matcher(line);
			if(matcher.find()){
				ids.add(matcher.group(1));
			}
		}
		return ids.iterator();
	}
}
