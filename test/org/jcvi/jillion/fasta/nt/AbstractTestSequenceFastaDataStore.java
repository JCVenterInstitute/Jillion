/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
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
    protected abstract NucleotideFastaDataStore parseFile(File file) throws IOException;
    
   
    @Test
    public void getSequenceById() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	
    	assertEquals(contig_1.getSequence(), sut.getSequence(contig_1.getId()));
    	assertEquals(contig_5.getSequence(), sut.getSequence(contig_5.getId()));
    	assertEquals(contig_9.getSequence(), sut.getSequence(contig_9.getId()));
    }
    
    @Test
    public void getSubSequenceById() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	
    	assertEquals(getSubSequence( contig_1.getSequence(), 100), sut.getSubSequence(contig_1.getId(), 100));
    	assertEquals(getSubSequence( contig_5.getSequence(), 50), sut.getSubSequence(contig_5.getId(), 50));
    	assertEquals(getSubSequence( contig_9.getSequence(), 87), sut.getSubSequence(contig_9.getId(), 87));
    }
    
    @Test
    public void getSubSequenceAtOffset0ShouldBeFullSeq() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	
    	assertEquals(contig_1.getSequence(), sut.getSubSequence(contig_1.getId(), 0));
    	assertEquals(contig_5.getSequence(), sut.getSubSequence(contig_5.getId(), 0));
    	assertEquals(contig_9.getSequence(), sut.getSubSequence(contig_9.getId(), 0));
    }
    
    @Test
    public void getSubSequenceByIdThatDoesNotExistShouldReturnNull() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	assertNull(sut.getSequence("does not exist"));
    }
    @Test
    public void getSubSequenceRangeById() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	Range range = Range.of(35, 349);
    	assertEquals(getSubSequence( contig_1.getSequence(), range), sut.getSubSequence(contig_1.getId(), range));
    	assertEquals(getSubSequence( contig_5.getSequence(), range), sut.getSubSequence(contig_5.getId(), range));
    	assertEquals(getSubSequence( contig_9.getSequence(), range), sut.getSubSequence(contig_9.getId(), range));
    }
    @Test
    public void getSubSequenceByOffsetThatDoesNotExistShouldReturnNull() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	assertNull(sut.getSubSequence("does not exist", 100));
    }
    
    @Test
    public void getSubSequenceByRangeThatDoesNotExistShouldReturnNull() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	assertNull(sut.getSubSequence("does not exist", Range.ofLength(100)));
    }
    
    @Test
    public void getSubSequenceNullRangeShouldThrowNPE() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	
    	expectedException.expect(NullPointerException.class);    	
    	sut.getSubSequence(contig_1.getId(), null);
    }
    
    @Test
    public void getSubSequenceNegativeOffsetShouldThrowIllegalArgumentException() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	
    	expectedException.expect(IllegalArgumentException.class);
    	expectedException.expectMessage("negative");
    	sut.getSubSequence(contig_1.getId(), -1);
    	
    }
    
    @Test
    public void getSubSequenceBeyondLengthOffsetShouldThrowIllegalArgumentException() throws IOException, DataStoreException{
    	NucleotideFastaDataStore sut = parseFile(getFile());
    	
    	expectedException.expect(IllegalArgumentException.class);
    	expectedException.expectMessage("beyond sequence length");
    	sut.getSubSequence(contig_1.getId(), 1_000_000);
    	
    }
    
    
    private NucleotideSequence getSubSequence(NucleotideSequence fullSeq, int startOffset){
    	Range range = Range.of(startOffset, fullSeq.getLength() -1);
    	return getSubSequence(fullSeq, range);
    	
    }

	private NucleotideSequence getSubSequence(NucleotideSequence fullSeq, Range range) {
		//to really test we aren't going to use the helper trim methods on the builder
		//but just the iterator
    	NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder();
		Iterator<Nucleotide> iter = fullSeq.iterator(range);
    	while(iter.hasNext()){
    		builder.append(iter.next());
    	}
    	return builder.build();
	}
    
    
    
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
