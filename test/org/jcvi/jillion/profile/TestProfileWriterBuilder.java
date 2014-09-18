package org.jcvi.jillion.profile;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigBuilder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Test;
public class TestProfileWriterBuilder {


	@Test(expected= NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new ProfileWriterBuilder((OutputStream)null, seq("A"));
	}
	@Test(expected= NullPointerException.class)
	public void nullFileShouldThrowNPE(){
		new ProfileWriterBuilder((File)null, seq("A"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void seqLengthOverIntMaxShouldThrowIllegaArgEx(){
		NucleotideSequence tooLong = createMock(NucleotideSequence.class);
		expect(tooLong.getLength()).andStubReturn(Integer.MAX_VALUE +1L);
		replay(tooLong);
		new ProfileWriterBuilder(new ByteArrayOutputStream(), tooLong);
		
	}
	
	@Test
	public void writeProfileToFile() throws IOException{
		File tmp = File.createTempFile("junit", "testProfile");
		try{
			Contig<?> contig = new ContigBuilder("ACGTACGT")
										.addRead("read1", 0, "ACGTACGT")
										.addRead("read2", 0, "ACGTACGT")
										.addRead("read3", 4,     "ACGT")
										.build();
			
			
			try(ProfileWriter sut = new ProfileWriterBuilder(tmp, contig.getConsensusSequence())
												.build();
					StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
					while(iter.hasNext()){
					AssembledRead read = iter.next();
					sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
					}
			}
			
			String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
								"A\t0\t2\t0\t0\t0%n"+
								"C\t0\t0\t2\t0\t0%n"+
								"G\t0\t0\t0\t2\t0%n"+
								"T\t0\t0\t0\t0\t2%n"+
								"A\t0\t3\t0\t0\t0%n"+
								"C\t0\t0\t3\t0\t0%n"+
								"G\t0\t0\t0\t3\t0%n"+
								"T\t0\t0\t0\t0\t3%n");
			
			assertEquals(expected, TestUtil.getFileAsString(tmp));
		}finally{
			tmp.delete();
		}
	}
	
	@Test
	public void createProfile() throws IOException{
		Contig<?> contig = new ContigBuilder("ACGTACGT")
								.addRead("read1", 0, "ACGTACGT")
								.addRead("read2", 0, "ACGTACGT")
								.addRead("read3", 4,     "ACGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t0\t0\t2\t0\t0%n"+
										"G\t0\t0\t0\t2\t0%n"+
										"T\t0\t0\t0\t0\t2%n"+
										"A\t0\t3\t0\t0\t0%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
										
		assertEquals(expected, new String(out.toByteArray(), "UTF-8"));		
		
	}
	
	@Test
	public void createProfileWithMajorityGaps() throws IOException{
		Contig<?> contig = new ContigBuilder("AC-TACGT")
								.addRead("read1", 0, "AC-TACGT")
								.addRead("read2", 0, "AC-TACGT")
								.addRead("read3", 4,     "ACGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t0\t0\t2\t0\t0%n"+
										"A\t2\t0\t0\t0\t0%n"+
										"T\t0\t0\t0\t0\t2%n"+
										"A\t0\t3\t0\t0\t0%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
										
		assertEquals(expected, new String(out.toByteArray(), "UTF-8"));		
		
	}
	
	@Test
	public void createProfileIgnoreSlicesWithGapInConsensus() throws IOException{
		Contig<?> contig = new ContigBuilder("AC-TACGT")
								.addRead("read1", 0, "AC-TACGT")
								.addRead("read2", 0, "AC-TACGT")
								.addRead("read3", 4,     "ACGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
													.ignoreGappedConsensusPositions(true)
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t0\t0\t2\t0\t0%n"+
									//	"A\t2\t0\t0\t0\t0%n"+
										"T\t0\t0\t0\t0\t2%n"+
										"A\t0\t3\t0\t0\t0%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
										
		assertEquals(expected, new String(out.toByteArray(), "UTF-8"));		
		
	}
	
	
	@Test
	public void createProfileWithAmbiguitiesShouldBeFractional() throws IOException{
		Contig<?> contig = new ContigBuilder("ACGTACGT")
								.addRead("read1", 0, "ACGTACGT")
								.addRead("read2", 0, "ACGTWCGT")
								.addRead("read3", 4,     "ACGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t0\t0\t2\t0\t0%n"+
										"G\t0\t0\t0\t2\t0%n"+
										"T\t0\t0\t0\t0\t2%n"+
										"A\t0\t2.5\t0\t0\t0.5%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
										
		assertEquals(expected, new String(out.toByteArray(), "UTF-8"));		
		
	}
	
	@Test
	public void createProfileTieUseLowestAscii() throws IOException{
		Contig<?> contig = new ContigBuilder("ACGTACGT")
								.addRead("read1", 0, "ACGTACGT")
								.addRead("read2", 0, "AGGAACGT")
								.addRead("read3", 4,     "ACGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		String actual = new String(out.toByteArray(), "UTF-8");
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t0\t0\t1\t1\t0%n"+
										"G\t0\t0\t0\t2\t0%n"+
										"A\t0\t1\t0\t0\t1%n"+
										"A\t0\t3\t0\t0\t0%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
										
		
		assertEquals(expected, actual);		
		
	}
	
	@Test
	public void createProfileTieUseAmbiguity() throws IOException{
		Contig<?> contig = new ContigBuilder("ACGTACGT")
								.addRead("read1", 0, "ACGTACGT")
								.addRead("read2", 0, "AGGAACGT")
								.addRead("read3", 4,     "ACGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
														.setMostFrequentTieBreakerRule(MostFrequentTieBreakerRule.AMBIGUITY)
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		String actual = new String(out.toByteArray(), "UTF-8");
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"S\t0\t0\t1\t1\t0%n"+
										"G\t0\t0\t0\t2\t0%n"+
										"W\t0\t1\t0\t0\t1%n"+
										"A\t0\t3\t0\t0\t0%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
										
		
		assertEquals(expected, actual);		
		
	}
	@Test
	public void createProfileTieUseAmbiguityWithAmibiguityInReads() throws IOException{
		Contig<?> contig = new ContigBuilder("ACGTACGT")
								.addRead("read1", 0, "ACGTWCGT")
								.addRead("read2", 0, "ACGTWCGT")
								.addRead("read3", 4,     "WCGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
														.setMostFrequentTieBreakerRule(MostFrequentTieBreakerRule.AMBIGUITY)
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		String actual = new String(out.toByteArray(), "UTF-8");
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t0\t0\t2\t0\t0%n"+
										"G\t0\t0\t0\t2\t0%n"+
										"T\t0\t0\t0\t0\t2%n"+
										"W\t0\t1.5\t0\t0\t1.5%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
										
		
		assertEquals(expected, actual);		
		
	}
	
	@Test
	public void createProfileTieUseRandom() throws IOException{
		Contig<?> contig = new ContigBuilder("ACGTACGT")
								.addRead("read1", 0, "ACGTACGT")
								.addRead("read2", 0, "ACGAACGT")
								.addRead("read3", 4,     "ACGT")
								.build();
		
		
		String expectedA = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t0\t0\t2\t0\t0%n"+
										"G\t0\t0\t0\t2\t0%n"+
										"A\t0\t1\t0\t0\t1%n"+
										"A\t0\t3\t0\t0\t0%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
		
		String expectedT = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t0\t0\t2\t0\t0%n"+
										"G\t0\t0\t0\t2\t0%n"+
										"T\t0\t1\t0\t0\t1%n"+
										"A\t0\t3\t0\t0\t0%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
					
		int numberOfAs=0, numberOfTs=0;
		for(int i=0; i<1000; i++){
			String actual =generateProfile(contig, MostFrequentTieBreakerRule.RANDOM);
			if(actual.equals(expectedA)){
				numberOfAs ++;
			}else if(actual.equals(expectedT)){
				numberOfTs ++;
			}else{
				fail("did not match either expected input: " + actual);
			}
		}
		
		assertTrue(numberOfAs >0);
		assertTrue(numberOfTs >0);
		
	}
	
	private String generateProfile(Contig<?> contig, MostFrequentTieBreakerRule rule) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
										.setMostFrequentTieBreakerRule(rule)				
										.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		return new String(out.toByteArray(), "UTF-8");
	}
	
	@Test
	public void createProfileWithGap() throws IOException{
		Contig<?> contig = new ContigBuilder("ACGTACGT")
								.addRead("read1", 0, "A-GTACGT")
								.addRead("read2", 0, "ACGTA-GT")
								.addRead("read3", 4,     "ACGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t1\t0\t1\t0\t0%n"+
										"G\t0\t0\t0\t2\t0%n"+
										"T\t0\t0\t0\t0\t2%n"+
										"A\t0\t3\t0\t0\t0%n"+
										"C\t1\t0\t2\t0\t0%n"+
										"G\t0\t0\t0\t3\t0%n"+
										"T\t0\t0\t0\t0\t3%n");
										
		assertEquals(expected, new String(out.toByteArray(), "UTF-8"));		
		
	}
	
	
	@Test
	public void createProfileWithVariation() throws IOException{
		Contig<?> contig = new ContigBuilder("ACGTACGT")
								.addRead("read1", 0, "ACGTACGT")
								.addRead("read2", 0, "ACGTGCTT")
								.addRead("read3", 4,     "ACGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t2\t0\t0\t0%n"+
										"C\t0\t0\t2\t0\t0%n"+
										"G\t0\t0\t0\t2\t0%n"+
										"T\t0\t0\t0\t0\t2%n"+
										"A\t0\t2\t0\t1\t0%n"+
										"C\t0\t0\t3\t0\t0%n"+
										"G\t0\t0\t0\t2\t1%n"+
										"T\t0\t0\t0\t0\t3%n");
										
		assertEquals(expected, new String(out.toByteArray(), "UTF-8"));		
		
	}
	
	@Test
	public void createProfileWithVariationWithPercentages() throws IOException{
		Contig<?> contig = new ContigBuilder("ACGTACGT")
								.addRead("read1", 0, "ACGTACGT")
								.addRead("read2", 0, "ACGTGCTT")
								.addRead("read3", 4,     "ACGT")
								.build();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		
		try(ProfileWriter sut = new ProfileWriterBuilder(out, contig.getConsensusSequence())
														.displayPercentages(DisplayCountStrategy.PERCENTAGES)
														.build();
			StreamingIterator<? extends AssembledRead> iter = contig.getReadIterator()){
			
			while(iter.hasNext()){
				AssembledRead read = iter.next();
				sut.addSequence((int)read.getGappedStartOffset(), read.getNucleotideSequence());
			}
		}
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t100\t0\t0\t0%n"+
										"C\t0\t0\t100\t0\t0%n"+
										"G\t0\t0\t0\t100\t0%n"+
										"T\t0\t0\t0\t0\t100%n"+
										"A\t0\t67\t0\t33\t0%n"+
										"C\t0\t0\t100\t0\t0%n"+
										"G\t0\t0\t0\t67\t33%n"+
										"T\t0\t0\t0\t0\t100%n");
										
		assertEquals(expected, new String(out.toByteArray(), "UTF-8"));		
		
	}
	
	@Test
	public void include0xEdges() throws IOException{
		NucleotideSequence ref =seq("ACGTACGT");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(ProfileWriter sut = new ProfileWriterBuilder(out, ref)
									.build();
			){
				sut.addSequence(2, seq("GTAC"));
		}
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										"A\t0\t0\t0\t0\t0%n"+
										"C\t0\t0\t0\t0\t0%n"+
										"G\t0\t0\t0\t1\t0%n"+
										"T\t0\t0\t0\t0\t1%n"+
										"A\t0\t1\t0\t0\t0%n"+
										"C\t0\t0\t1\t0\t0%n"+
										"G\t0\t0\t0\t0\t0%n"+
										"T\t0\t0\t0\t0\t0%n");
				
		assertEquals(expected, new String(out.toByteArray(), "UTF-8"));	
		
	}
	
	@Test
	public void doNotInclude0xEdges() throws IOException{
		NucleotideSequence ref =seq("ACGTACGT");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(ProfileWriter sut = new ProfileWriterBuilder(out, ref)
									.include0xEdges(false)
									.build();
			){
				sut.addSequence(2, seq("GTAC"));
		}
		
		String expected = String.format("#Major\t-\tA\tC\tG\tT%n"+
										//"A\t0\t0\t0\t0\t0%n"+
									//	"C\t0\t0\t0\t0\t0%n"+
										"G\t0\t0\t0\t1\t0%n"+
										"T\t0\t0\t0\t0\t1%n"+
										"A\t0\t1\t0\t0\t0%n"+
										"C\t0\t0\t1\t0\t0%n"
									//	"G\t0\t0\t0\t0\t0%n"+
									//	"T\t0\t0\t0\t0\t0%n"
									);
				
		assertEquals(expected, new String(out.toByteArray(), "UTF-8"));	
		
	}
	
	private static NucleotideSequence seq(String s){
		return new NucleotideSequenceBuilder(s).build();
	}
	
	private static class ContigBuilder{
		private static int id=0;
		private final TigrContigBuilder builder;
		
		public ContigBuilder(String consensus){
			builder = new TigrContigBuilder("contigId"+id, new NucleotideSequenceBuilder(consensus).build());
			
		}
		
		public ContigBuilder addRead(String id, int offset, String seq){
			String ungapped =seq.replaceAll("-", "");
			builder.addRead(id, offset, Range.ofLength(ungapped.length()), seq, Direction.FORWARD, ungapped.length());
		
			return this;
		}
		
		public Contig<?> build(){
			return builder.build();
		}
	}
}
