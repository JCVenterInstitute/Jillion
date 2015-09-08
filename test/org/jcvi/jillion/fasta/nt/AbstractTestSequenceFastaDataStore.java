/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public abstract class AbstractTestSequenceFastaDataStore {

    protected static final String FASTA_FILE_PATH = "files/19150.fasta";
   
    NucleotideFastaRecord contig_1 = new NucleotideFastaRecordBuilder("1",
            new NucleotideSequenceBuilder("AACCATTTGAATGGATGTCAATCCGACTTTACTTTTCTTGAAAGTTCCAGYGCAAAATGC"+
            "CATAAGCACCACATTCCCATACACTGGAGATCCTCCATACAGCCATGGAACGGGAACAGG"+
            "ATACACCATGGACACAGTCAACAGAACACATCAATATTCAGAAAAGGGGAAATGGACAAC"+
            "AAACACAGARACYGGAGCACCACAACTTAACCCAATTGATGGACCATTACCTGAGGATAA"+
            "TGAGCCAAGTGGATATGCACAAACAGATTGTGTCCTGGAAGCAATGGCTTTCCTTGAAGA"+
            "GTCCCACCCAGGAATCTTTGAAAACTCGTGTCTCGAAACGATGGAAGTTGTTCAGCAAAC"+
            "AAGAGTGGACAAGCTGACTCAAGGTCGCCAGACCTATGATTGGACATTGAACAGGAATCA"+
            "GCCGGCTGCAACTGCATTAGCTAATACTATAGAGGTTTTCAGATCGAACGGTCTAACGGC"+
            "CAATGAATCAGGAAGGCTGATAGACTTCCTCAAGGATGTGATGGAATCAATGGACAAAGA"+
            "AGACATGGAAATAACAACGCACTTCCAAAGAAAGAGAAGAGTAAGGGACAACATGACCAA"+
            "AAAAATGGTCACACAAAGAACAATAGGAAAGAAGAAGCAGAGATTAAACAAGAGAAGTTA"+
            "CTTAATAAGGGCATTGACACTGAACACAATGACAAAAGATGCTGAAAGAGGCAAGTTAAA"+
            "RAGAAGAGCAATTGCGACACCCGGAATGCAAATCAGAGGATTTGTGTATTTTGTTGAAAC"+
            "ATTGGCGAGAAGCATCTGTGAGAAGCTTGAACAGTCTGGGCTCCCAGTCGGAGGCAATGA"+
            "AAAGAAGGCTAAACTGGCAAATGTCGTGAGGAAAATGATGACTAACTCACAGGACACAGA"+
            "GCTTTCTTTCACAATCACTGGAGACAACACCAAATGGAATGAAAATCAGAACCCTAGAAT"+
            "GTTTCTGGCAATGATAACATACATAACAAGAAATCAACCTGAATGGTTCAGGAATGTCTT"+
            "GAGCATCGCACCTATAATGTTCTCGAATAAAATGGCAAGGCTRGGGAAAGGATACATGTT"+
            "TGAAAGCAAGAGCATGAAGCTTCGAACACAGGTATCAGCAGAAATGCTAGCAAATATTGA"+
            "CCTGAAATATTTCAATGAGTCAACAAAAAAGAAAATAGAGAAGATAAGGCCTCTTTTAAT"+
            "AGAGGGCACAGCCTCATTGAGTCCCGGAATGATGATGGGCATGTTCAACATGCTAAGCAC"+
            "AGTTTTAGGAGTTTCAATCCTAAATCTGGGACAAAAGAGATACACCAAAACAACGTATTG"+
            "GTGGGACGGACTCCARTCCTCCGATGACTTTGCTCTCATAGTGAATGCACCGAATCATGA"+
            "GGGAATACAAGCAGGAGTAGATAGATTCTATAGGACTTGCAAACTAGTCGGAATCAATAT"+
            "GAGCAAAAAGAAGTCCTACATAAACAGGACAGGAACGTTTGAATTCACAAGCTTTTTCTA"+
            "TCGCTATGGGTTCGTAGCCAATTTCAGCATGGAACTGCCCAGCTTTGGAGTGTCTGGGAT"+
            "CAATGAATCAGCTGACATGAGCATTGGGGTAACAGTGATAAAGAACAACATGATAAACAA"+
            "TGACCTTGGGCCAGCAACGGCCCAAATGGCTCTCCAGCTGTTCATCAAGGATTACAGATA"+
            "TACATACCGGTGCCACAGAGGGGACACACAAATCCAGACAAGGAGATCATTCGAACTGAA"+
            "GAAATTATGGGAACAAACCCGATCAAAGGCAGGGCTGCTGGTTTCCGATGGGGGACCAAA"+
            "CCTGTACAATATCCGAAATCTCCACATCCCGGAGGTCTGCCTGAAATGGGAGCTGATGGA"+
            "CGAAGAATATCAGGGAAGGCTTTGTAATCCCTTGAACCCATTTGTCAGCCATAAGGAGAT"+
            "AGAGTCTGTGAACAGTGCAGTGGTGATGCCAGCTCACGGCCCAGCCAAAAGCATGGAATA"+
            "TGATGCTGTTGCTACTACGCACTCCTGGATCCCCAAGAGGAATCGCTCCATTCTTAACAC"+
            "GAGTCAAAGGGGAATCCTCGAAGATGAACAGATGTATCAAAAGTGCTGCAATCTATTCGA"+
            "AAAGTTCTTCCCTAGCAGTTCGTACAGAAGACCGGTCGGGATTTCTAGCATGGGGGAGGC"+
            "CATGGTGTCCAGGGCCCGAATTGATGCTCGAATTGACTTCGAATCTGGACGGATTAAGAA"+
            "AGAGGAGTTTGCTGAGATCATGAAGATCTGTTCCACCATTGAAGAACTCAGACGGCAGAA"+
            "ATAGTGAATTTAGCTTGTCCTTCATGAAA").build())
    	.comment("47 2313 bases, 00000000 checksum.")
    	.build();

    
    NucleotideFastaRecord contig_5 = new NucleotideFastaRecordBuilder("5",
    		new NucleotideSequenceBuilder( "ATGTTTAAAGATGAGTCTTCTAACCGAGGTCGAAACGTACGTTCTCTCTATCATCCCATC" +
            "AGGCCCCCTCAAAGCCGAGATCGCGCAGAGACTTGAAGATGTTTTTGCAGGGAAGAACAC" +
            "AGATCTTGAGGCACTCATGGAATGGCTAAAGACAAGACCAATCCTGTCACCTCTGACTAA" +
            "GGGGATTTTAGGATTTGTGTTCACGCTCACCGTGCCCAGTGAGCGAGGACTGCAGCGTAG" +
            "ACGCTTTGTCCAAAATGCTCTTAATGGGAATGGAGATCCAAATAACATGGACAGGGCAGT" +
            "CAAACTGTACAGGAAATTAAAAAGGGAAATTACATTCCATGGGGCCAAAGAGGTAGCACT" +
            "CAGTTATTCCACTGGTGCACTTGCCAGTTGCATGGGCCTTATATACAACAGAATGGGAAC" +
            "TGTGACCACTGAAGGGGCATTTGGCCTGGTGTGCGCCACGTGTGAACAGATTGCTGACTC" +
            "CCAGCATCGGTCCCACAGACAGATGGTGACAACAACCAACCCACTGATCAAACATGAAAA" +
            "CAGAATGGTACTGGCTAGTACTACAGCTAAAGCCATGGAACAGGTGGCAGGGTCAAGTGA" +
            "ACAGGCAGCAGAGGCTATGGAGGTTGCCAGTCAGGCTAGGCAGATGGTGCAGGCGATGAG" +
            "GACCATTGGGACTCATCCTAGCTCCAGTGCCGGTCTAAGAGATGATCTTCTTGAAAATTT" +
            "GCAGGCCTATCAGAAAAGGATGGGAGTGCAATTGCAGCGATTCAAGTGATCCTCTCGTCA" +
            "TTGCCGCAAGTATCATTGGAATCTTGCACTTGATATTGTGGATTCTTGATCGCCTTTTTT" +
            "TCAAATGCATTCATCGTCGCCTTAAATACGGGTTGAAACGAGGGCCTTCTACGGAAGGAG" +
            "TGCCTAAGTCTATGAGGGAGGAATATCGGCAGGAACAGCAGAGCGCTGTGGATGTTGACG" +
            "ATGGTCATTTTGTCAACATAGAGCTGGAGTAAA").build())
            .comment("19 995 bases, 00000000 checksum.")
            .build();
    NucleotideFastaRecord contig_9 = new NucleotideFastaRecordBuilder("9",
    		new NucleotideSequenceBuilder("AATATATTCAATATGGAGAGAATAAAAGAACTGAGAGATCTAATGTCACAGTCTCGCACC" +
            "CGCGAGATACTMACCAAAACCACTGTGGACCACATGGCCATAATCAAAAAATACACATCA" +
            "GGAAGGCAAGAGAAGAACCCCGCACTTAGAATGAAGTGGATGATGGCAATGAAATATCCA" +
            "ATTACAGCAGATAAGAGAATAATGGAAATGATTCCTGAAAGGAATGAACAAGGACAAACT" +
            "CTCTGGAGCAAAACAAACGATGCCGGCTCAGACCGAGTGATGGTATCACCTCTGGCTGTT" +
            "ACATGGTGGAATAGGAATGGACCAACAACAAGTACAGTTCATTACCCAAAGATATATAAG" +
            "ACCTATTTCGAAAAAGTCGAAAGGTTGAAACACGGGACCTTTGGCCCTGTTCACTTCAGA" +
            "AATCAAGTTAAAATAAGACGGAGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCC" +
            "AAAGAGGCACAGGATGTAATCATGGAAGTTGTTTTCCCTAATGAAGTGGGAGCGAGAATA" +
            "CTAACATCAGAATCGCAACTGACGATAACAAAAGAGAAGAAAGAGGAACTGCAGGACTGC" +
            "AAAATTGCCCCTCTGATGGTTGCATACATGCTGGAAAGAGAGTTGGTCCGCAAAACGAGA" +
            "TTTCTCCCAGTGGCTGGTGGAACAAGCAGTGTCTATATTGAAGTGCTGCATTTAACCCAG" +
            "GGGACATGCTGGGAGCAGATGTACACCCCAGGAGGGGARGTGAGAAATGATGATATTGAC" +
            "CAAAGCTTGATTATCGCTGCAAGGAACATAGTAAGAAGAGCAACAGTATCAGCAGACCCA" +
            "CTAGCATCTCTATTGGAGATGTGCCACAGCACACAGATCGGGGGGGTAAGGATGGTAGAC" +
            "ATTCTTCGGCAAAATCCAACAGAGGAACAAGCCGTGGACATATGCAAGGCAGCATTGGGC" +
            "TTAAGGATTAGCTCGTCTTTTAGCTTTGGTGGATTCACTTTCAAAAGAACAAGCGGATCG" +
            "TCAGTTGGGAGAGAAGAAGAAGTGCTTACGGGCAACCTTCAAACATTGAAAATAAGAGTA" +
            "CATGAGGGGTATGAAGAGTTCACAATGATTGGGAGGAGAGCAACAGCTATTCTCAGGAAA" +
            "GCAACCAGAAGATTGATCCAGCTAATAGTAAGYGGGAGAGACGAGCAGTCAATTGCTGAG" +
            "GCAATAATTGTGGCCATGGTATTTTCACAAGAAGATTGCATGATCAAGGCAGTTCGGGGT" +
            "GACCTGAACTTTGTCAATAGGGCAAACCAGCGACTGAACCCAATGCATCAACTCTTGAGA" +
            "CACTTCCAAAAGGATGCAAAAGTGCTTTTCCAAAACTGGGGAATTGARCCCATTGACAAT" +
            "GTAATGGGAATGATCGGAATATTGCCCGACATGACCCCAAGTACTGAGATGTCGCTGAGG" +
            "GGGATAAGAGTCAGTAAGATGGGAGTAGATGAATACTCCAGCACAGAGAGGGTGACAGTG" +
            "AGCATTGACCGATTTTTAAGAGTTCGGGACCAACGGGGGAACGTACTATTGTCACCCGAA" +
            "GAAGTCAGCGAGACACAAGGAACAGAAAAGCTGACAATAACTTACTCGTCATCAATGATG" +
            "TGGGAAATTAATGGTCCTGAGTCAGTGTTGGTCAATACTTATCAGTGGATCATCAGAAAT" +
            "TGGGAAACYGTGAAAATTCAATGGTCACAGGATCCCACAATTTTRTATAACAAGATGGAA" +
            "TTCGAGCCATTTCAGTCTCTGGTCCCTAAGGCAGCCAGAGGTCAGTACAGTGGATTCGTG" +
            "AGGACACTATTCCAGCAGATGCGGGATGTGCTTGGGACGTTTGACACTGTCCAGATAATA" +
            "AAACTTCTCCCCTTTGCTGCTGCCCCACCAGAACAGAGTAGGATGCAGTTCTCCTCCTTG" +
            "ACTGTGAATGTGAGAGGATCAGGGATGAGGATACTGGTGAGAGGCAATTCTCCAGTGTTC" +
            "AATTACAACAAGGCCACCAAGAGACTTACGGTTCTCGGGAAAGATGCAGGTGCATTGACC" +
            "GAAGATCCAGATGAAGGCACAGCTGGAGTAGAGTCTGCTGTTTTAAGAGGTTTCCTCATT" +
            "TTGGGCAAAGAAGACAAGAGATACGGCCCAGCATTGAGCATCAATGAACTGAGCAATCTT" +
            "GCAAAGGGAGAAAAGGCTAATGTGCTAATTGGGCAAGGAGACGTGGTGTTGGTAATGAAA" +
            "CGGAAACGGGACTCTAGCATACTTACTGACAGCCAGACAGCGACCAAAAGGATTCGGATG" +
            "GCCATCAATTAATGTCGAATTGTTTAA").build())
            .comment("48 2311 bases, 00000000 checksum.")
            .build();
    
    
    ResourceHelper RESOURCES = new ResourceHelper(AbstractTestSequenceFastaDataStore.class);
    protected File getFile() throws IOException {
        return RESOURCES.getFile(FASTA_FILE_PATH);
    }
    
    @Test
    public void parseFileGet() throws IOException, DataStoreException{
        
        DataStore<NucleotideFastaRecord> sut = parseFile(getFile());
        assertEquals(9, sut.getNumberOfRecords());
        assertTrue(sut.contains("1"));
        assertEquals(contig_1, sut.get("1"));
        assertTrue(sut.contains("5"));
        assertEquals(contig_5, sut.get("5"));
        assertTrue(sut.contains("9"));
        assertEquals(contig_9, sut.get("9"));
        
        assertFalse(sut.contains("not in datastore"));
        assertNull(sut.get("not in datastore"));
    }
    
    @Test
    public void parseIdIterator() throws IOException, DataStoreException{
        
        DataStore<NucleotideFastaRecord> sut = parseFile(getFile());
        Iterator<String> iter = sut.idIterator();
        assertTrue(iter.hasNext());
        for(int i=1; i<=9; i++){
            assertEquals(""+i, iter.next());
        }
        assertFalse(iter.hasNext());
    }
    @Test
    public void closingIdIteratorShouldStopIteration() throws IOException, DataStoreException{
        
        DataStore<NucleotideFastaRecord> sut = parseFile(getFile());
        StreamingIterator<String> iter = sut.idIterator();
        assertTrue(iter.hasNext());
        iter.next();
        iter.next();
        iter.close();
        assertFalse(iter.hasNext());
    }
    
    @Test
    public void parseFileRecordIterator() throws IOException, DataStoreException{
        
        DataStore<NucleotideFastaRecord> sut = parseFile(getFile());
        Iterator<NucleotideFastaRecord> iter = sut.iterator();
        assertTrue(iter.hasNext());
        for(int i=1; i<=9; i++){
            final NucleotideFastaRecord next = iter.next();
            assertEquals(""+i, next.getId());
        }
        assertFalse(iter.hasNext());
    }
    @Test
    public void closingFileRecordIteratorShouldStopIteration() throws IOException, DataStoreException{
        
        DataStore<NucleotideFastaRecord> sut = parseFile(getFile());
        StreamingIterator<NucleotideFastaRecord> iter = sut.iterator();
        assertTrue(iter.hasNext());
        iter.next();
        iter.next();
        iter.close();
        assertFalse(iter.hasNext());
    }
    protected abstract DataStore<NucleotideFastaRecord> parseFile(File file) throws IOException;
    
   
    @Test
    public void closedDataStoreShouldThrowClosedExceptions() throws IOException, DataStoreException{
    	 DataStore<NucleotideFastaRecord> sut = parseFile(getFile());
    	 
    	 sut.close();
    	 assertTrue(sut.isClosed());
    	try {
			sut.contains("id");
			fail("contains should throw DataStoreClosed exception");
		} catch (DataStoreClosedException ignore) {
		}
    	
    	try {
			sut.get("id");
			fail("get should throw DataStoreClosed exception");
		} catch (DataStoreClosedException ignore) {
		}
    	
    	try {
			sut.getNumberOfRecords();
			fail("getNumberOfRecords should throw DataStoreClosed exception");
		} catch (DataStoreClosedException ignore) {
		}
    	
    	try {
			sut.idIterator();
			fail("idIterator should throw DataStoreClosed exception");
		} catch (DataStoreClosedException ignore) {
		}
    	try {
			sut.iterator();
			fail("iterator should throw DataStoreClosed exception");
		} catch (DataStoreClosedException ignore) {
		}
    	
    	
    }
    
}
