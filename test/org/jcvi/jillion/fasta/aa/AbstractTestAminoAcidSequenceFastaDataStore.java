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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.aa;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.fasta.aa.UnCommentedAminoAcidSequenceFastaRecord;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestAminoAcidSequenceFastaDataStore {

	private final AminoAcidSequenceFastaRecord firstRecord = new UnCommentedAminoAcidSequenceFastaRecord(
			"ABN50481.1",
			new AminoAcidSequenceBuilder("MKAIIVLLLVVTSNADRICTGITSSNSPHVVKTATQGEVNVTGAIPLTTTPTKSHFANLKGTKTRGKLCPTCFN" +
			"CTDLDVALGRPMCVGITPSAKASILHEVRPVTSGCFPIMHDRTKIRQLPNLLRGYEKIRLSTQNVIDAEKAPGG" +
			"PYRLGTSGSCPNATSKSGFFATMAWAVPRDNNKTATNPLTVEVPHICTKEEDQITVWGFHSDNKTQMKNLYGDS" +
			"NPQKFTSSANGITTHYVSQIGGFPDQTEDGGLPQSGRIVVDYMVQKPGKTGTIVYQRGILLPQKVWCASGRSKV" +
			"IKGSLPLIGEADCLHEKYGGLNKSKPYYTGEHAKAIGNCPIWVKTPLKLANGTKYRPPAKLLKERGFFGAIAGF" +
			"LEGGWEGMIAGWHGYTSHGAHGVAVAADLKSTQEAINKITKNLNSLSELEVKNLQRLSGAMDELHNEILELDEK" +
			"VDDLRADTISSQIELAVLLSNEGIINSEDEHLLALERKLKKMLGPSAVDIGNGCFETKHKCNQTCLDRIAAGTF" +
			"NAGEFSLPTFDSLNITAASLNDDGLDNHTILLYYSTAASSLAVTLMIAIFIVYMISRDNVSCSICL").build());
	
	private final AminoAcidSequenceFastaRecord middleRecord = new UnCommentedAminoAcidSequenceFastaRecord(
			"ABR15984.1",
			new AminoAcidSequenceBuilder("MKAIIVLLMVVTSNADRICTGITSSNSPHVVKTATQGEVNVTGVIPLTTTPTKSYFANLKGTRTRGKLCPDCLN" +
			"CTDLDVALGRPMCVGTTPSAKASILHEVRPVTSGCFPIMHDRTKIRQLPNLLRGYENIRLSTQNVIDAENAPGG" +
			"PYRLGTSGSCPNATSKSGFFATMAWAVPKDNNKNATNPLTVEVPYICTEGEDQITVWGFHSDNKTQMKNLYGDS" +
			"NPQKFTSSANGVTTHYVSQIGGFPAQTEDGGLPQSGRIVVDYMVQKPRKTGTIVYQRGVLLPQKVWCASGRSKV" +
			"IKGSLPLIGEADCLHEKYGGLNKSKPYYTGEHAKAIGNCPIWVKTPLKLANGTKYRPPAKLLKERGFFGAIAGF" +
			"LEGGWEGMIAGWHGYTSHGAHGVAVAADLKSTQEAINKITKNLNSLSELEVKNLQRLSGAMDELHNEILELDEK" +
			"VDDLRADTISSQIELAVLLSNEGIINSEDEHLLALERKLKKMLGPSAVDIGNGCFETKHKCNQTCLDRIAAGTF" +
			"NAGEFSLPTFDSLNITAASLNDDGLDNHTILLYYSTAASSLAVTLMLAIFIVYMVSRDNVSCSICL").build());
	
	private final AminoAcidSequenceFastaRecord lastRecord = new UnCommentedAminoAcidSequenceFastaRecord(
			"EPI159954",
			new AminoAcidSequenceBuilder("MKAIIVLLMVVTSNADRICTGITSSNSPHVVKTATQGEVNVTGVIPLTTTPTKSYFANLKGTRTRGKLCPDCLN" +
			"CTDLDVALGRPMCVGTTPSAKASILHEVRPVTSGCFPIMHDRTKIRQLPNLLRGYENIRLSTQNVIDAEKAPGG" +
			"PYRLGTSGSCPNATSKSGFFATMAWAVPKDNNKNATNPLTVEVPYICTEGEDQITVWGFHSDDKTQMKNLYGDS" +
			"NPQKFTSSANGVTTHYVSQIGGFPDQTEDGGLPQSGRIVVDYMMQKPGKTGTIVYQRGVLLPQKVWCASGRSKV" +
			"IKGSLPLIGEADCLHEKYGGLNKSKPYYTGEHAKAIGNCPIWVKTPLKLANGTKYRPPAKLLKERGFFGAIAGF" +
			"LEGGWEGMIAGWHGYTSHGAHGVAVAADLKSTQEAINKITKNLNSLSELEVKNLQRLSGAMDELHNEILELDEK" +
			"VDDLRADTISSQIELAVLLSNEGIINSENEHLLALERKLKKMLGPSAVDIGNGCFETKHKCNQTCLDRIAAGTF" +
			"NAGEFSLPTFDSLNITAASLNDDGLDNHTILLYYSTAASSLAVTLMLAIFIVYMVSRDNVSCSICL").build());
	
	private final AminoAcidSequenceFastaDataStore sut;
	
	public AbstractTestAminoAcidSequenceFastaDataStore() throws Exception{
		ResourceHelper resources = new ResourceHelper(AbstractTestAminoAcidSequenceFastaDataStore.class);
		sut = create(resources.getFile("files/example.aa.fasta"));
	}
	
	protected abstract AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception;
	
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
		StreamingIterator<AminoAcidSequenceFastaRecord> iter= sut.iterator();
		
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
}
