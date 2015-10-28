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
package org.jcvi.jillion.fasta.aa;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.fasta.aa.UnCommentedProteinFastaRecord;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
public abstract class AbstractTestProteinFastaDataStore {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private final ProteinFastaRecord firstRecord = new UnCommentedProteinFastaRecord(
			"ABN50481.1",
			new ProteinSequenceBuilder("MKAIIVLLLVVTSNADRICTGITSSNSPHVVKTATQGEVNVTGAIPLTTTPTKSHFANLKGTKTRGKLCPTCFN" +
			"CTDLDVALGRPMCVGITPSAKASILHEVRPVTSGCFPIMHDRTKIRQLPNLLRGYEKIRLSTQNVIDAEKAPGG" +
			"PYRLGTSGSCPNATSKSGFFATMAWAVPRDNNKTATNPLTVEVPHICTKEEDQITVWGFHSDNKTQMKNLYGDS" +
			"NPQKFTSSANGITTHYVSQIGGFPDQTEDGGLPQSGRIVVDYMVQKPGKTGTIVYQRGILLPQKVWCASGRSKV" +
			"IKGSLPLIGEADCLHEKYGGLNKSKPYYTGEHAKAIGNCPIWVKTPLKLANGTKYRPPAKLLKERGFFGAIAGF" +
			"LEGGWEGMIAGWHGYTSHGAHGVAVAADLKSTQEAINKITKNLNSLSELEVKNLQRLSGAMDELHNEILELDEK" +
			"VDDLRADTISSQIELAVLLSNEGIINSEDEHLLALERKLKKMLGPSAVDIGNGCFETKHKCNQTCLDRIAAGTF" +
			"NAGEFSLPTFDSLNITAASLNDDGLDNHTILLYYSTAASSLAVTLMIAIFIVYMISRDNVSCSICL").build());
	
	private final ProteinFastaRecord middleRecord = new UnCommentedProteinFastaRecord(
			"ABR15984.1",
			new ProteinSequenceBuilder("MKAIIVLLMVVTSNADRICTGITSSNSPHVVKTATQGEVNVTGVIPLTTTPTKSYFANLKGTRTRGKLCPDCLN" +
			"CTDLDVALGRPMCVGTTPSAKASILHEVRPVTSGCFPIMHDRTKIRQLPNLLRGYENIRLSTQNVIDAENAPGG" +
			"PYRLGTSGSCPNATSKSGFFATMAWAVPKDNNKNATNPLTVEVPYICTEGEDQITVWGFHSDNKTQMKNLYGDS" +
			"NPQKFTSSANGVTTHYVSQIGGFPAQTEDGGLPQSGRIVVDYMVQKPRKTGTIVYQRGVLLPQKVWCASGRSKV" +
			"IKGSLPLIGEADCLHEKYGGLNKSKPYYTGEHAKAIGNCPIWVKTPLKLANGTKYRPPAKLLKERGFFGAIAGF" +
			"LEGGWEGMIAGWHGYTSHGAHGVAVAADLKSTQEAINKITKNLNSLSELEVKNLQRLSGAMDELHNEILELDEK" +
			"VDDLRADTISSQIELAVLLSNEGIINSEDEHLLALERKLKKMLGPSAVDIGNGCFETKHKCNQTCLDRIAAGTF" +
			"NAGEFSLPTFDSLNITAASLNDDGLDNHTILLYYSTAASSLAVTLMLAIFIVYMVSRDNVSCSICL").build());
	
	private final ProteinFastaRecord lastRecord = new UnCommentedProteinFastaRecord(
			"EPI159954",
			new ProteinSequenceBuilder("MKAIIVLLMVVTSNADRICTGITSSNSPHVVKTATQGEVNVTGVIPLTTTPTKSYFANLKGTRTRGKLCPDCLN" +
			"CTDLDVALGRPMCVGTTPSAKASILHEVRPVTSGCFPIMHDRTKIRQLPNLLRGYENIRLSTQNVIDAEKAPGG" +
			"PYRLGTSGSCPNATSKSGFFATMAWAVPKDNNKNATNPLTVEVPYICTEGEDQITVWGFHSDDKTQMKNLYGDS" +
			"NPQKFTSSANGVTTHYVSQIGGFPDQTEDGGLPQSGRIVVDYMMQKPGKTGTIVYQRGVLLPQKVWCASGRSKV" +
			"IKGSLPLIGEADCLHEKYGGLNKSKPYYTGEHAKAIGNCPIWVKTPLKLANGTKYRPPAKLLKERGFFGAIAGF" +
			"LEGGWEGMIAGWHGYTSHGAHGVAVAADLKSTQEAINKITKNLNSLSELEVKNLQRLSGAMDELHNEILELDEK" +
			"VDDLRADTISSQIELAVLLSNEGIINSENEHLLALERKLKKMLGPSAVDIGNGCFETKHKCNQTCLDRIAAGTF" +
			"NAGEFSLPTFDSLNITAASLNDDGLDNHTILLYYSTAASSLAVTLMLAIFIVYMVSRDNVSCSICL").build());
	
	private final ProteinFastaDataStore sut;
	
	public AbstractTestProteinFastaDataStore() throws Exception{
		ResourceHelper resources = new ResourceHelper(AbstractTestProteinFastaDataStore.class);
		sut = create(resources.getFile("files/example.aa.fasta"));
	}
	
	protected abstract ProteinFastaDataStore create(File fastaFile) throws Exception;
	
	@Test
	public void numberOfRecords() throws DataStoreException{
		assertEquals(13L, sut.getNumberOfRecords());
	}
	
	@Test
	public void idIterator() throws DataStoreException{
		Iterator<String> expectedIterator = Arrays.asList(
				"ABN50481.1",
				"EPI55973",
				"ABL77156.1",
				"EPI51649",
				"ACF54202.1",
				"ABR15984.1",
				"EPI62257",
				"ACF54180.1",
				"EPI159930",
				"ABL76749.1",
				"EPI50928",
				"ACF54213.1",
				"EPI159954"
				).iterator();
		
		StreamingIterator<String> actual = sut.idIterator();
		try{
			while(expectedIterator.hasNext()){
				assertTrue(actual.hasNext());
				assertEquals(expectedIterator.next(), actual.next());
			}
			assertFalse(actual.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(actual);
		}
	}
	
	@Test
	public void getFirstRecord() throws DataStoreException{
		assertEquals(firstRecord, sut.get("ABN50481.1"));
	}
	
	@Test
	public void getMiddleRecord() throws DataStoreException{
		assertEquals(middleRecord, sut.get("ABR15984.1"));
	}
	
	@Test
	public void getLastRecord() throws DataStoreException{
		assertEquals(lastRecord, sut.get("EPI159954"));
	}
	
	@Test
	public void iterator() throws DataStoreException{
		//we aren't going to check each record.
		//we will assume that if the order
		//is correct and our first, middle
		//and last records are correct and in the 
		//correct order then the other records
		//in between are correct as well.
		StreamingIterator<ProteinFastaRecord> iter= sut.iterator();
		
		try{
			assertTrue(iter.hasNext());
			assertEquals(firstRecord, iter.next());
			assertTrue(iter.hasNext());
			//skip to the next record we know about
			for(int i=0; i<4; i++){
				assertTrue(iter.hasNext());
				assertNotNull(iter.next());
			}
			assertEquals(middleRecord, iter.next());
			assertTrue(iter.hasNext());
			//skip to the last record
			for(int i=0; i<6; i++){
				assertTrue(iter.hasNext());
				assertNotNull(iter.next());
			}
			assertEquals(lastRecord, iter.next());
			assertFalse(iter.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
	
	
	 @Test
	    public void getSubSequenceById() throws IOException, DataStoreException{
	    	
	    	assertEquals(getSubSequence( firstRecord.getSequence(), 100), sut.getSubSequence(firstRecord.getId(), 100));
	    	assertEquals(getSubSequence( middleRecord.getSequence(), 50), sut.getSubSequence(middleRecord.getId(), 50));
	    	assertEquals(getSubSequence( lastRecord.getSequence(), 87), sut.getSubSequence(lastRecord.getId(), 87));
	    }
	    
	    @Test
	    public void getSubSequenceByIdThatDoesNotExistShouldReturnNull() throws IOException, DataStoreException{
	    	assertNull(sut.getSequence("does not exist"));
	    }
	    @Test
	    public void getSubSequenceRangeById() throws IOException, DataStoreException{
	    	Range range = Range.of(35, 349);
	    	assertEquals(getSubSequence( firstRecord.getSequence(), range), sut.getSubSequence(firstRecord.getId(), range));
	    	assertEquals(getSubSequence( middleRecord.getSequence(), range), sut.getSubSequence(middleRecord.getId(), range));
	    	assertEquals(getSubSequence( lastRecord.getSequence(), range), sut.getSubSequence(lastRecord.getId(), range));
	    }
	    @Test
	    public void getSubSequenceByOffsetThatDoesNotExistShouldReturnNull() throws IOException, DataStoreException{
	    	assertNull(sut.getSubSequence("does not exist", 100));
	    }
	    
	    @Test
	    public void getSubSequenceByRangeThatDoesNotExistShouldReturnNull() throws IOException, DataStoreException{
	    	assertNull(sut.getSubSequence("does not exist", Range.ofLength(100)));
	    }
	    
	    @Test
	    public void getSubSequenceNullRangeShouldThrowNPE() throws IOException, DataStoreException{
	    	
	    	expectedException.expect(NullPointerException.class);    	
	    	sut.getSubSequence(firstRecord.getId(), null);
	    }
	    
	    @Test
	    public void getSubSequenceNegativeOffsetShouldThrowIllegalArgumentException() throws IOException, DataStoreException{
	    	
	    	expectedException.expect(IllegalArgumentException.class);
	    	expectedException.expectMessage("negative");
	    	sut.getSubSequence(firstRecord.getId(), -1);
	    	
	    }
	    
	    @Test
	    public void getSubSequenceBeyondLengthOffsetShouldThrowIllegalArgumentException() throws IOException, DataStoreException{
	    	
	    	expectedException.expect(IllegalArgumentException.class);
	    	expectedException.expectMessage("beyond sequence length");
	    	sut.getSubSequence(firstRecord.getId(), 1_000_000);
	    	
	    }
	    
	    
	    private ProteinSequence getSubSequence(ProteinSequence fullSeq, int startOffset){
	    	Range range = Range.of(startOffset, fullSeq.getLength() -1);
	    	return getSubSequence(fullSeq, range);
	    	
	    }

		private ProteinSequence getSubSequence(ProteinSequence fullSeq, Range range) {
			//to really test we aren't going to use the helper trim methods on the builder
			//but just the iterator
			ProteinSequenceBuilder builder = new ProteinSequenceBuilder();
			Iterator<AminoAcid> iter = fullSeq.iterator(range);
	    	while(iter.hasNext()){
	    		builder.append(iter.next());
	    	}
	    	return builder.build();
		}
}
