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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fastq.DefaultFastQRecord;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastQRecord;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.CommonUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestFastQFileDataStore {
    static final FastQQualityCodec QUALITY_CODEC = FastQQualityCodec.ILLUMINA;
    DataStore<FastQRecord> sut;
    String file = "files/example.fastq";
    ResourceFileServer resources = new ResourceFileServer(
            TestDefaultFastQFileDataStore.class);
    DefaultFastQRecord solexa_1489 = new DefaultFastQRecord(
            "SOLEXA1:4:1:12:1489#0/1",
            new NucleotideSequenceBuilder("TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT").build(),
            QUALITY_CODEC
                    .decode("abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S"));

    DefaultFastQRecord solexa_1692 = new DefaultFastQRecord(
            "SOLEXA1:4:1:12:1692#0/1",
            new NucleotideSequenceBuilder("ACGCCTGCGTTATGGTNTAACAGGCATTCCGCCCCAGACAAACTCCCCCCCTAACCATGTCTTTCGCAAAAATCAGTCAATAAATGACCTTAACTTTAGA").build(),
            QUALITY_CODEC
                    .decode("`a\\a`^\\a^ZZa[]^WB_aaaa^^a`]^a`^`aaa`]``aXaaS^a^YaZaTW]a_aPY\\_UVY[P_ZHQY_NLZUR[^UZ\\TZWT_[_VWMWaRFW]BB"),
            "example comment");

    protected abstract DataStore<FastQRecord> createFastQFileDataStore(File file,FastQQualityCodec qualityCodec) throws IOException;
    @Before
    public void setup() throws IOException{
        sut = createFastQFileDataStore(resources.getFile(file), QUALITY_CODEC);
    }
    @Test
    public void size() throws DataStoreException{
        assertEquals(2, sut.size());
    }
    @Test
    public void contains() throws DataStoreException{
        assertTrue(sut.contains(solexa_1489.getId()));
    }
    @Test
    public void containQueryForIdThatIsNotContainedShouldReturnFalse() throws DataStoreException{
        assertFalse(sut.contains("notInDataStore"));
    }
    
    @Test
    public void get() throws DataStoreException{
        assertFastQRecordsEqual(solexa_1489, sut.get(solexa_1489.getId()));
        assertFastQRecordsEqual(solexa_1692, sut.get(solexa_1692.getId()));
    }
    @Test
    public void shouldThrowExceptionIfTryToGetAfterClose() throws IOException{
        sut.close();
        try{
            sut.get(solexa_1489.getId());
            fail("should throw exception when get called when already closed");
        }catch(DataStoreException e){
            //pass
        }
    }
    
    @Test
    public void idIterator() throws DataStoreException{
        List<String> expectedIds = Arrays.asList(solexa_1489.getId(),solexa_1692.getId());
        Iterator<String> iterator = sut.getIds();
        assertTrue(iterator.hasNext());
        for(String expectedId : expectedIds){
            assertTrue(iterator.hasNext());
            assertEquals(expectedId, iterator.next());
        }
        assertFalse(iterator.hasNext());
        try{
            iterator.next();
            fail("should throw exception after !hasNext()");
        }catch(NoSuchElementException expected ){
        }
    }
    @Test
    public void closingIdIteratorEarlyShouldHaltIterating() throws DataStoreException, IOException{
        CloseableIterator<String> iter = sut.getIds();
        iter.next();
        iter.close();
        assertFalse(iter.hasNext());
        try{
            iter.next();
            fail("should throw exception after closing");
        }catch(NoSuchElementException expected ){
        }
    }
    @Test
    public void iterator(){
        Iterator<FastQRecord> iter = sut.iterator();
        assertTrue(iter.hasNext());
        assertFastQRecordsEqual(solexa_1489, iter.next());
        assertTrue(iter.hasNext());
        assertFastQRecordsEqual(solexa_1692, iter.next());
        assertFalse(iter.hasNext());
        try{
            iter.next();
            fail("should throw exception after !hasNext()");
        }catch(NoSuchElementException expected ){
        }
    }
    
    @Test
    public void closingIteratorEarlyShouldStopIterating() throws IOException{
        CloseableIterator<FastQRecord> iter = sut.iterator();
        assertTrue(iter.hasNext());
        assertFastQRecordsEqual(solexa_1489, iter.next());
        assertTrue(iter.hasNext());
        iter.close();
        assertFalse(iter.hasNext());
        try{
            iter.next();
            fail("should throw exception after !hasNext()");
        }catch(NoSuchElementException expected ){
        }
    }

    private void assertFastQRecordsEqual(FastQRecord expected,
            FastQRecord actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getNucleotides().asList(), actual
                .getNucleotides().asList());
        assertEquals(expected.getQualities().asList(), actual.getQualities()
                .asList());
        assertTrue(CommonUtil.similarTo(expected.getComment(), actual
                .getComment()));
    }
}
