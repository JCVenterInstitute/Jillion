package org.jcvi.common.core.seq.read.trace.sanger.primer;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.read.trace.sanger.primer.PrimerDetector;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
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
