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
package org.jcvi.jillion_experimental.primer;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion_experimental.primer.PrimerDetector;
import org.junit.Test;

public class TestPrimerDetector {
	 private final PrimerDetector sut = new PrimerDetector(5,.9f);
	    @Test
	    public void onlyPrimerShouldReturnFullRange(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("AAACGACGTACGTACGT").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(sequence);
	        
	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(new Range.Builder(sequence.getLength()).build(), actualRanges.get(0).asRange());
	        assertEquals(Direction.FORWARD, actualRanges.get(0).getDirection());
	    }
	    @Test
	    public void onlyReverseComplementPrimerShouldReturnFullRange(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("AAACGACGTACGTACGT").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
	        		new NucleotideSequenceBuilder(sequence)
	        		.reverseComplement()
	        		.build());
	        
	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(new Range.Builder(sequence.getLength()).build(), actualRanges.get(0).asRange());
	        assertEquals(Direction.REVERSE, actualRanges.get(0).getDirection());
	    }
	    @Test
	    public void hits5primeEnd(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("AAACGACGTACGTACGT").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
	        		new NucleotideSequenceBuilder("AAACG").build());
	        
	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(Range.of(0,4), actualRanges.get(0).asRange());
	        assertEquals(Direction.FORWARD, actualRanges.get(0).getDirection());
	    }
	    @Test
	    public void reverseComplementHits5primeEnd(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("AAACGACGTACGTACGT").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
	        		new NucleotideSequenceBuilder("AAACG")
	        		.reverseComplement()
	        		.build());
	        
	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(Range.of(0,4), actualRanges.get(0).asRange());
	        assertEquals(Direction.REVERSE, actualRanges.get(0).getDirection());
	    }
	    
	    @Test
	    public void hitsCloseTo5primeEnd(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("TTAAACGACGTACGTACGT").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
	        		new NucleotideSequenceBuilder("AAACG").build());

	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(new Range.Builder(5).shift(2).build(), actualRanges.get(0).asRange());
	        assertEquals(Direction.FORWARD, actualRanges.get(0).getDirection());
	    }
	    
	    @Test
	    public void reverseComplementHitsCloseTo5primeEnd(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("TTAAACGACGTACGTACGT").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
	        		new NucleotideSequenceBuilder("AAACG")
	        		.reverseComplement()
	        		.build());

	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(new Range.Builder(5).shift(2).build(), actualRanges.get(0).asRange());
	        assertEquals(Direction.REVERSE, actualRanges.get(0).getDirection());
	    }
	    @Test
	    public void hitsOn3primerEnd(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("ACGTACGTACGTAAACG").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
	        		new NucleotideSequenceBuilder("AAACG").build());
	        
	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(new Range.Builder(5).shift(12).build(), actualRanges.get(0).asRange());
	        assertEquals(Direction.FORWARD, actualRanges.get(0).getDirection());
	    }
	    @Test
	    public void reverseComplementHitsOn3primerEnd(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("ACGTACGTACGTAAACG").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
	        		new NucleotideSequenceBuilder("AAACG")
	        		.reverseComplement()
	        		.build());
	        
	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(new Range.Builder(5).shift(12).build(), actualRanges.get(0).asRange());
	        assertEquals(Direction.REVERSE, actualRanges.get(0).getDirection());
	    }
	    @Test
	    public void hitsCloseTo3primerEnd(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("ACGTACGTACGTAAACGTT").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
	        		new NucleotideSequenceBuilder("AAACG").build());
	        
	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(new Range.Builder(5).shift(12).build(), actualRanges.get(0).asRange());
	        assertEquals(Direction.FORWARD, actualRanges.get(0).getDirection());
	    }
	    @Test
	    public void reverseComplementHitsCloseTo3primerEnd(){
	        NucleotideSequence sequence = new NucleotideSequenceBuilder("ACGTACGTACGTAAACGTT").build();
	        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
	        		new NucleotideSequenceBuilder("AAACG")
	        		.reverseComplement()
	        		.build());
	        
	        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
	        assertEquals(1, actualRanges.size());
	        assertEquals(new Range.Builder(5).shift(12).build(), actualRanges.get(0).asRange());
	        assertEquals(Direction.REVERSE, actualRanges.get(0).getDirection());
	    }
	    @Test
	    public void hitsInMiddle(){
	    	 NucleotideSequence sequence = new NucleotideSequenceBuilder(
	    			 "AAATTTACGTACGTGGGAAAAAATATA").build();
		        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
		        		new NucleotideSequenceBuilder("ACGTACGTG")
		        		.build());
		        
		        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
		        assertEquals(1, actualRanges.size());
		        assertEquals(new Range.Builder(9).shift(6).build(), actualRanges.get(0).asRange());
		        assertEquals(Direction.FORWARD, actualRanges.get(0).getDirection());
		        
	    }
	    
	    @Test
	    public void reverseComplementHitsInMiddle(){
	    	 NucleotideSequence sequence = new NucleotideSequenceBuilder(
	    			 "AAATTTACGTACGTGGGAAAAAATATA").build();
		        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
		        		new NucleotideSequenceBuilder("ACGTACGTG")
		        		.reverseComplement()
		        		.build());
		        
		        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
		        assertEquals(1, actualRanges.size());
		        assertEquals(new Range.Builder(9).shift(6).build(), actualRanges.get(0).asRange());
		        assertEquals(Direction.REVERSE, actualRanges.get(0).getDirection());
		        
	    }
	    
	    @Test
	    public void multiplePrimerHits(){
	    	 NucleotideSequence sequence = new NucleotideSequenceBuilder(
	    			 "AAATTTACGTACGTGGGAAAAAATATA").build();
		        NucleotideSequenceDataStore datastore = TestPrimerTrimmerUtil.createDataStoreFor(
		        		new NucleotideSequenceBuilder("ACGTACGTG")
		        		.reverseComplement()
		        		.build(),
		        		
		        		new NucleotideSequenceBuilder("AAAAAATATA")
		        		.build()
		        		);
		        
		        List<DirectedRange> actualRanges= sut.detect(sequence, datastore);
		        assertEquals(2, actualRanges.size());
		        assertEquals(new Range.Builder(9).shift(6).build(), actualRanges.get(0).asRange());
		        assertEquals(Direction.REVERSE, actualRanges.get(0).getDirection());
		        
		        assertEquals(new Range.Builder(10).shift(17).build(), actualRanges.get(1).asRange());
		        assertEquals(Direction.FORWARD, actualRanges.get(1).getDirection());
		        
	    }
}
