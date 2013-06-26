/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
package org.jcvi.jillion.assembly.consed.phd;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdReadTag;
import org.jcvi.jillion.assembly.consed.phd.PhdWholeReadItem;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestPhdBuilder {

	String id = "id";
	NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTACGT").build();
	
	QualitySequence quals = new QualitySequenceBuilder(new byte[]{10,20,30,40,50,60,70,80}).build();


	@Test
	public void notSpecifyingPeaksSetsThemToNull(){
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		Phd phd = sut.build();
		assertEquals(id, phd.getId());
		assertEquals(seq, phd.getNucleotideSequence());
		assertEquals(quals, phd.getQualitySequence());
		assertNull(phd.getPeakSequence());
		assertTrue(phd.getComments().isEmpty());
		assertTrue(phd.getWholeReadItems().isEmpty());
		assertTrue(phd.getReadTags().isEmpty());
	}
	
	@Test(expected = NullPointerException.class)
	public void nullIdShouldThrowNPE(){
		 new PhdBuilder(null, seq,quals);
	}
	@Test(expected = NullPointerException.class)
	public void nullSeqShouldThrowNPE(){
		 new PhdBuilder(id, null,quals);
	}
	@Test(expected = NullPointerException.class)
	public void nullQualsShouldThrowNPE(){
		 new PhdBuilder(id, seq,null);
	}
	
	@Test
	public void qualsLongerThanSeqShouldThrowException(){
		QualitySequence qualsTooBig = new QualitySequenceBuilder(quals)
											.append(5)
											.build();
		try{
			new PhdBuilder(id, seq,qualsTooBig);
			fail("should throw exception if seq and qual diff length");
		}catch(IllegalArgumentException expected){
			//ignore
		}
	}
	
	@Test
	public void seqLongerThanQualsShouldThrowException(){
		NucleotideSequence seqTooBig = new NucleotideSequenceBuilder(seq)
											.append("A")
											.build();
		try{
			new PhdBuilder(id, seqTooBig,quals);
			fail("should throw exception if seq and qual diff length");
		}catch(IllegalArgumentException expected){
			//ignore
		}
	}
	
	@Test
	public void setPeaksToNull(){
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
				
		Phd phd = sut.peaks(null)
						.build();
		assertEquals(id, phd.getId());
		assertEquals(seq, phd.getNucleotideSequence());
		assertEquals(quals, phd.getQualitySequence());
		assertNull(phd.getPeakSequence());
		assertTrue(phd.getComments().isEmpty());
		assertTrue(phd.getWholeReadItems().isEmpty());
		assertTrue(phd.getReadTags().isEmpty());
	}
	
	@Test
	public void setPeaksToNonNull(){
		PositionSequence peaks = new PositionSequenceBuilder(new short[]{5,10,15,20,25,30,35,40})
									.build();
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
				
		Phd phd = sut.peaks(peaks)
						.build();
		assertEquals(id, phd.getId());
		assertEquals(seq, phd.getNucleotideSequence());
		assertEquals(quals, phd.getQualitySequence());
		assertEquals(peaks,phd.getPeakSequence());
		assertTrue(phd.getComments().isEmpty());
		assertTrue(phd.getWholeReadItems().isEmpty());
		assertTrue(phd.getReadTags().isEmpty());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void setPeaksWrongLengthShouldThrowException(){
		PositionSequence peaksTooShort = new PositionSequenceBuilder(new short[]{5,10,15,20,25,30,35})
									.build();
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
				
		sut.peaks(peaksTooShort);		
	}
	
	@Test
	public void settingPeaksAfterfakingPeaksShouldUseRealPeaks(){
		PositionSequence peaks = new PositionSequenceBuilder(new short[]{5,10,15,20,25,30,35,40})
										.build();
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		
		Phd phd = sut.fakePeaks()				
						.peaks(peaks)
						.build();
		assertEquals(id, phd.getId());
		assertEquals(seq, phd.getNucleotideSequence());
		assertEquals(quals, phd.getQualitySequence());
		assertEquals(peaks,phd.getPeakSequence());
		assertTrue(phd.getComments().isEmpty());
		assertTrue(phd.getWholeReadItems().isEmpty());
		assertTrue(phd.getReadTags().isEmpty());
	}
	
	@Test(expected = NullPointerException.class)
	public void settingCommentsToNullShouldThrowNPE(){
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		
		sut.comments(null);
	}
	
	@Test
	public void settingComments(){
		Map<String, String> comments = new HashMap<String, String>();
		comments.put("Test", "1-2-3");
		
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		sut.comments(comments);
		
		Phd phd = sut.build();
		assertEquals(comments, phd.getComments());
	}
	@Test
	public void changingValuesAfterSettingCommentsShouldNotAffectPhdComments(){
		Map<String, String> comments = new HashMap<String, String>();
		comments.put("Test", "1-2-3");
		
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		sut.comments(comments);
		
		comments.put("added later", "not in phd");
		
		Phd phd = sut.build();
		assertFalse(comments.equals(phd.getComments()));
	}
	
	@Test(expected = NullPointerException.class)
	public void settingReadTagsToNullShouldThrowNPE(){
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		
		sut.readTags(null);
	}
	
	@Test
	public void settingReadTags(){
		List<PhdReadTag> tags = Arrays.asList(
				createMock(PhdReadTag.class)
				);
		
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		
		sut.readTags(tags);
		
		Phd phd = sut.build();
		assertEquals(tags, phd.getReadTags());
	}
	
	@Test
	public void changingReadTagsAfterSettingShouldNotChangePhd(){
		List<PhdReadTag> tags = new ArrayList<PhdReadTag>();
		tags.add(createMock(PhdReadTag.class));
		
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		
		sut.readTags(tags);
		tags.add(createMock(PhdReadTag.class));
		Phd phd = sut.build();
		assertFalse(tags.equals(phd.getReadTags()));
	}
	
	@Test(expected = NullPointerException.class)
	public void settingWholeReadItemToNullShouldThrowNPE(){
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		
		sut.wholeReadItems(null);
	}
	
	@Test
	public void settingWholeReadItems(){
		List<PhdWholeReadItem> items = Arrays.asList(
				createMock(PhdWholeReadItem.class)
				);
		
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		
		sut.wholeReadItems(items);
		
		Phd phd = sut.build();
		assertEquals(items, phd.getWholeReadItems());
	}
	
	@Test
	public void changingWholeReadItemAfterSettingShouldNotChangePhd(){
		List<PhdWholeReadItem> items = new ArrayList<PhdWholeReadItem>();
		items.add(createMock(PhdWholeReadItem.class));
		
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		
		sut.wholeReadItems(items);
		items.add(createMock(PhdWholeReadItem.class));
		Phd phd = sut.build();
		assertFalse(items.equals(phd.getWholeReadItems()));
	}
	
	@Test
	public void fakePeaksShouldStartAt15AndBeSpacedBy19(){
		PositionSequence expected = createFakePeaks(seq.getLength(),15,19);
		
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		sut.fakePeaks();
		
		Phd phd = sut.build();
		
		assertEquals(expected, phd.getPeakSequence());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fakePeak0StartShouldThrowException(){
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		sut.fakePeaks(0, 15);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fakePeak0SpacingShouldThrowException(){
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		sut.fakePeaks(13, 0);
	}
	
	@Test
	public void userDefinedfakePeaks(){
		int start = 13;
		int spacing = 11;
		
		PositionSequence expected = createFakePeaks(seq.getLength(),start,spacing);
		
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		sut.fakePeaks(start,spacing);
		
		Phd phd = sut.build();
		
		assertEquals(expected, phd.getPeakSequence());
	}
	
	private PositionSequence createFakePeaks(long length, int start, int spacing){
		PositionSequenceBuilder builder = new PositionSequenceBuilder((int)length);
		builder.append(start);
		for(int i=1; i<length; i++){
			builder.append(start + i* spacing);
		}
		
		return builder.build();
	}
	
	@Test
	public void callingFakePeaksAfterRealPeaksShouldUseFakePeaks(){
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		PositionSequence peaks = createMock(PositionSequence.class);
		expect(peaks.getLength()).andReturn(seq.getLength());
		replay(peaks);
		
		sut.peaks(peaks);
		
		sut.fakePeaks(10,12);
		
		Phd phd = sut.build();
		
		PositionSequence expected = createFakePeaks(seq.getLength(),10,12);
		
		assertEquals(expected, phd.getPeakSequence());
	}
	
	@Test
	public void changesToCopiesDoNotAffectEachOther(){
		PhdBuilder sut = new PhdBuilder(id, seq,quals);
		
		PhdBuilder copy = sut.copy();
		
		copy.fakePeaks();
		Map<String, String> comments = new HashMap<String, String>();
		comments.put("Test", "1-2-3");
		sut.comments(comments);
		
		Phd phdOfSut = sut.build();
		Phd phdOfCopy = copy.build();
		
		assertEquals(comments, phdOfSut.getComments());
		assertTrue(phdOfCopy.getComments().isEmpty());
		
		assertNull(phdOfSut.getPeakSequence());
		assertNotNull(phdOfCopy.getPeakSequence());
	}
	@Test
	public void constructUsingPhd(){
		Map<String, String> comments = new HashMap<String, String>();
		comments.put("Test", "1-2-3");
		
		PhdBuilder builder = new PhdBuilder(id, seq,quals);
		builder.fakePeaks();		
		builder.comments(comments);
		
		Phd phd = builder.build();
		
		Phd sut = new PhdBuilder(phd).build();
		
		assertEquals(phd, sut);
		assertEquals(sut, phd);
	}
}
