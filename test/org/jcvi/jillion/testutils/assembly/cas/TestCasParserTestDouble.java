package org.jcvi.jillion.testutils.assembly.cas;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IAnswer;
import org.jcvi.jillion.assembly.clc.cas.AbstractCasFileVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasAlignment;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegion;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType;
import org.jcvi.jillion.assembly.clc.cas.CasFileInfo;
import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor.CasVisitorCallback;
import org.jcvi.jillion.assembly.clc.cas.CasMatch;
import org.jcvi.jillion.assembly.clc.cas.CasMatchVisitor;
import org.jcvi.jillion.assembly.clc.cas.CasParser;
import org.jcvi.jillion.assembly.clc.cas.CasUtil;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.testutils.assembly.cas.CasParserTestDouble;
import org.jcvi.jillion.testutils.assembly.cas.FastaRecordWriter;
import org.jcvi.jillion.testutils.assembly.cas.FastqRecordWriter;
import org.jcvi.jillion.testutils.assembly.cas.SffRecordWriter;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.sff.SffFileDataStore;
import org.jcvi.jillion.trace.sff.SffFileDataStoreBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestCasParserTestDouble {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	private File workingDir;
	
	@Before
	public void setupWorkingDir() throws IOException{
		workingDir = tmpDir.newFolder();
	}
	@Test(expected = IllegalArgumentException.class)
	public void addForwardReadToUnknownReferenceShouldThrowException() throws IOException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		
		sut.forwardMatch("unknown", 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void readFastaRecordWriterWithMax0ReadsShouldThrowException() throws IOException{
		new CasParserTestDouble.Builder(workingDir,
				new FastaRecordWriter(workingDir, 0));

	}
	
	@Test(expected = NullPointerException.class)
	public void nullRecordWriterShouldThrowException() throws IOException{
		new CasParserTestDouble.Builder(workingDir,
				new FastaRecordWriter[]{null});

	}
	@Test(expected = NullPointerException.class)
	public void AnyRecordWritersAreNullReadsShouldThrowException() throws IOException{
		new CasParserTestDouble.Builder(workingDir,
				new FastaRecordWriter[]{
				new FastaRecordWriter(workingDir,1),
				null,
				new FastaRecordWriter(workingDir)});

	}
	@Test(expected = IllegalArgumentException.class)
	public void readFastqRecordWriterWithMax0ReadsShouldThrowException() throws IOException{
		new CasParserTestDouble.Builder(workingDir,
				new FastqRecordWriter(workingDir, 0));

	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addReverseReadToUnknownReferenceShouldThrowException() throws IOException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		
		sut.reverseMatch("unknown", 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addForwardReadToStartsBeyondReferenceShouldThrowException() throws IOException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 20);
	}
	@Test(expected = IllegalArgumentException.class)
	public void addReverseReadToStartsBeyondReferenceShouldThrowException() throws IOException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.reverseMatch("ref", 20);
	}
	
	@Test
	public void forwardReadAllMatch() throws IOException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				
		//		assertEquals(Range.ofLength(8), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(1, alignments.size());
				CasAlignmentRegion next = alignments.get(0);
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, next.getType());
				assertEquals(8, next.getLength());
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
	}
	
	
	@Test
	public void haltAfterLastRead() throws IOException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		//ugly hack to be able to use callback in mock
		//before we actually get it (array is populated in visitMatches() answer)
		CasVisitorCallback[] callback = new CasVisitorCallback[1];
		
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				
		//		assertEquals(Range.ofLength(8), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(1, alignments.size());
				CasAlignmentRegion next = alignments.get(0);
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, next.getType());
				assertEquals(8, next.getLength());
				
				callback[0].haltParsing();
				return null;
			}
			
		});
		matchVisitor.halted();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andAnswer(new IAnswer<CasMatchVisitor>(){

			@Override
			public CasMatchVisitor answer() throws Throwable {
				callback[0] = (CasVisitorCallback) getCurrentArguments()[0];
				return matchVisitor;
			}
			
		
		});
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
	}
	
	@Test
	public void haltInMiddleOfMatchesShouldNotVisitAllMatches() throws IOException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
				.build();
		//this match should not be visited
		sut.unMatched();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		//ugly hack to be able to use callback in mock
		//before we actually get it (array is populated in visitMatches() answer)
		CasVisitorCallback[] callback = new CasVisitorCallback[1];
		
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				
		//		assertEquals(Range.ofLength(8), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(1, alignments.size());
				CasAlignmentRegion next = alignments.get(0);
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, next.getType());
				assertEquals(8, next.getLength());
				
				callback[0].haltParsing();
				return null;
			}
			
		});
		matchVisitor.halted();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andAnswer(new IAnswer<CasMatchVisitor>(){

			@Override
			public CasMatchVisitor answer() throws Throwable {
				callback[0] = (CasVisitorCallback) getCurrentArguments()[0];
				return matchVisitor;
			}
			
		
		});
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
	}
	
	@Test
	public void forwardReadDoesNotStartAt0() throws IOException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 2)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 6)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				
		//		assertEquals(Range.ofLength(8), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(2,alignment.getStartOfMatch());
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(1, alignments.size());
				CasAlignmentRegion next = alignments.get(0);
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, next.getType());
				assertEquals(6, next.getLength());
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
	}
	
	@Test
	public void forwardReadHasInternalInsertion() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
				.addAlignmentRegion(CasAlignmentRegionType.INSERT, 1)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				
				//assertEquals(Range.ofLength(9), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(3, alignments.size());

				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(0).getType());
				assertEquals(4, alignments.get(0).getLength());
				
				assertEquals(CasAlignmentRegionType.INSERT, alignments.get(1).getType());
				assertEquals(1, alignments.get(1).getLength());
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(2).getType());
				assertEquals(4, alignments.get(2).getLength());
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("unspecified insertion should be N", 
				"ACGTNACGT", readDatastore.get("read0").toString());
	}
	
	@Test
	public void forwardReadHasSpecifiedInternalInsertion() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
				.addAlignmentRegion(CasAlignmentRegionType.INSERT, "R")
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				
				//assertEquals(Range.ofLength(9), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(3, alignments.size());

				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(0).getType());
				assertEquals(4, alignments.get(0).getLength());
				
				assertEquals(CasAlignmentRegionType.INSERT, alignments.get(1).getType());
				assertEquals(1, alignments.get(1).getLength());
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(2).getType());
				assertEquals(4, alignments.get(2).getLength());
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("specified insertion sequence should be used", 
				"ACGTRACGT", readDatastore.get("read0").toString());
	}
	
	private NucleotideSequenceDataStore getReadSequenceDataStore(CasParser parser) throws IOException {
		List<NucleotideSequenceDataStore> datastores = new ArrayList<NucleotideSequenceDataStore>();
		parser.parse(new AbstractCasFileVisitor() {

			@Override
			public void visitReadFileInfo(CasFileInfo readFileInfo) {
				for(String path :readFileInfo.getFileNames()){
					try {
						File readFile = CasUtil.getFileFor(workingDir, path);
						String extension =FileUtil.getExtension(readFile);
						if("fasta".equals(extension)){
							NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(readFile).build();
							datastores.add(DataStoreUtil.adapt(NucleotideSequenceDataStore.class, datastore, 
									record -> record.getSequence()));
						}else if("fastq".equals(extension)){
							FastqDataStore datastore = new FastqFileDataStoreBuilder(readFile)
																.qualityCodec(FastqQualityCodec.SANGER)
																.build();
							datastores.add(DataStoreUtil.adapt(NucleotideSequenceDataStore.class, datastore, 
									record -> record.getNucleotideSequence()));
						}else if("sff".equals(extension)){
							SffFileDataStore datastore = new SffFileDataStoreBuilder(
									readFile)
							.build();
							datastores.add(DataStoreUtil.adapt(
									NucleotideSequenceDataStore.class,
									datastore,
									record -> record.getNucleotideSequence()));
						}
						else{
							throw new IllegalStateException("unknown extension " + extension + " file = " + readFile.getName());
						}
						
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			}
			
		});
		return DataStoreUtil.chain(NucleotideSequenceDataStore.class, datastores);
	}
	
	
	
	private NucleotideFastaDataStore getReferenceFastaDataStore() throws IOException {
		
		return new NucleotideFastaFileDataStoreBuilder(new File(workingDir, "reference.fasta"))
					.build();
	}
	@Test
	public void forwardReadHasInternalDeletion() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
				.addAlignmentRegion(CasAlignmentRegionType.DELETION, 1)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 3)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
			//	assertEquals(Range.ofLength(7), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(3, alignments.size());

				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(0).getType());
				assertEquals(4, alignments.get(0).getLength());
				
				assertEquals(CasAlignmentRegionType.DELETION, alignments.get(1).getType());
				assertEquals(1, alignments.get(1).getLength());
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(2).getType());
				assertEquals(3, alignments.get(2).getLength());
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("deletion", 
				"ACGTCGT", readDatastore.get("read0").toString());
	}
	
	@Test
	public void forwardReadHasInsertionAtBeginning() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.INSERT, 4)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
			//	assertEquals(Range.of(4,11), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(2, alignments.size());

				
				assertEquals(CasAlignmentRegionType.INSERT, alignments.get(0).getType());
				assertEquals(4, alignments.get(0).getLength());

				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(1).getType());
				assertEquals(8, alignments.get(1).getLength());
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("unspecified insertion should be N", 
				"NNNNACGTACGT", readDatastore.get("read0").toString());
	}
	
	@Test
	public void forwardReadHasSpecifiedInsertionAtBeginning() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.INSERT, "GGGG")
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
			//	assertEquals(Range.of(4,11), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(2, alignments.size());

				
				assertEquals(CasAlignmentRegionType.INSERT, alignments.get(0).getType());
				assertEquals(4, alignments.get(0).getLength());

				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(1).getType());
				assertEquals(8, alignments.get(1).getLength());
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("specified insertion", 
				"GGGGACGTACGT", readDatastore.get("read0").toString());
	}
	
	@Test
	public void forwardReadHasInsertionAtEnd() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
				.addAlignmentRegion(CasAlignmentRegionType.INSERT, 4)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
				//assertEquals(Range.ofLength(8), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(2, alignments.size());
				
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(0).getType());
				assertEquals(8, alignments.get(0).getLength());
				
				assertEquals(CasAlignmentRegionType.INSERT, alignments.get(1).getType());
				assertEquals(4, alignments.get(1).getLength());
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("unspecified insertion should be N", 
				"ACGTACGTNNNN", readDatastore.get("read0").toString());
}
	
	@Test
	public void reverseReadHasInsertionAtBeginning() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.reverseMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.INSERT, 4)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				CasAlignment alignment = match.getChosenAlignment();
				//reverse complement valid range so insertion at the end
			//	assertEquals(Range.ofLength(8), match.getTrimRange());
				assertNull(match.getTrimRange());
				assertEquals(0,alignment.getStartOfMatch());
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(2, alignments.size());

				

				assertEquals(CasAlignmentRegionType.INSERT, alignments.get(0).getType());
				assertEquals(4, alignments.get(0).getLength());

				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(1).getType());
				assertEquals(8, alignments.get(1).getLength());
				
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("reversed read should have left insertions on the right", 
				"ACGTACGTNNNN", readDatastore.get("read0").toString());
	}
	
	@Test
	public void reverseReadHasInsertionAtEnd() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.reverseMatch("ref", 0)
				
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 8)
				.addAlignmentRegion(CasAlignmentRegionType.INSERT, 4)
				.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				CasAlignment alignment = match.getChosenAlignment();
				
				//reverse complement valid range so insertion at the beginning
			//	assertEquals(Range.of(4, 11), match.getTrimRange());
				assertNull(match.getTrimRange());
				assertEquals(0,alignment.getStartOfMatch());
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(2, alignments.size());
				
				
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(0).getType());
				assertEquals(8, alignments.get(0).getLength());
				
				assertEquals(CasAlignmentRegionType.INSERT, alignments.get(1).getType());
				assertEquals(4, alignments.get(1).getLength());
				
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("reversed read should have right insertions on the left", 
				"NNNNACGTACGT", readDatastore.get("read0").toString());
	}
	
	@Test
	public void forwardReadNoAlignmentShouldNotHaveMatch() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
								.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				assertFalse(match.matchReported());
				assertNull(match.getChosenAlignment());
		
				assertNull(match.getTrimRange());
				
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("should be empty", 
				"", readDatastore.get("read0").toString());
	}
	
	@Test
	public void forwardReadAllInsertsShouldNotHaveMatch() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
								.addAlignmentRegion(CasAlignmentRegionType.INSERT, 10)
								.build();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				assertFalse(match.matchReported());
				assertNull(match.getChosenAlignment());
		
				assertNull(match.getTrimRange());
				
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("should be empty", 
				"", readDatastore.get("read0").toString());
	}
	@Test
	public void addNotMatch() throws IOException, DataStoreException{
		CasParserTestDouble.Builder sut = new CasParserTestDouble.Builder(workingDir);
		sut.addReference("ref", "ACGTACGT");
		
		sut.unMatched();
		
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				assertFalse(match.matchReported());
				assertNull(match.getChosenAlignment());
				
				assertNull(match.getTrimRange());
				
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		
		assertEquals("no match should be 10 Ns", 
				"NNNNNNNNNN", readDatastore.get("read0").toString());
	}
	
	@Test
	public void multipleReadsOneFastaFile() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir));
	}
	@Test
	public void multipleReadsTwoFastaFiles() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new FastaRecordWriter(workingDir, 2),
				new FastaRecordWriter(workingDir)));
	}
	
	@Test(expected = IllegalStateException.class)
	public void multipleReadsOverMaxAllowedInFastaFilesShouldThrowException() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new FastaRecordWriter(workingDir, 2)
				));
	}
	//////////
	@Test
	public void multipleReadsOneFastqFile() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new FastqRecordWriter(workingDir)));
	}
	@Test
	public void multipleReadsTwoFastqFiles() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new FastqRecordWriter(workingDir, 2),
				new FastqRecordWriter(workingDir)));
	}
	
	@Test
	public void multipleReadsMixOfFastaAndFastqFiles() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new FastqRecordWriter(workingDir, 2),
				new FastaRecordWriter(workingDir)));
	}
	
	@Test(expected = IllegalStateException.class)
	public void multipleReadsOverMaxAllowedInFastqFilesShouldThrowException() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new FastqRecordWriter(workingDir, 2)
				));
	}

	////////////
	@Test
	public void multipleReadsOneSffFile() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new SffRecordWriter.Builder(workingDir).build()));
	}
	@Test
	public void multipleReadsTwoSffFiles() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new SffRecordWriter.Builder(workingDir)
							.maxRecordsToWrite(2)
							.build(),
				new SffRecordWriter.Builder(workingDir).build()));
	}
	
	@Test
	public void multipleReadsMixOfFastaAndSffFiles() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new SffRecordWriter.Builder(workingDir)
							.maxRecordsToWrite(2)
							.build(),
				new SffRecordWriter.Builder(workingDir).build()));
	}
	
	@Test(expected = IllegalStateException.class)
	public void multipleReadsOverMaxAllowedInSffFilesShouldThrowException() throws IOException, DataStoreException{
		threeReads(new CasParserTestDouble.Builder(workingDir,
				new SffRecordWriter.Builder(workingDir)
							.maxRecordsToWrite(2)
							.build()
				));
	}
	private void threeReads(CasParserTestDouble.Builder sut) throws IOException, DataStoreException{
	
		CasParser parser = setup3Reads(sut);
		
		
		//verify fasta files correct
		NucleotideFastaDataStore referenceDatastore =getReferenceFastaDataStore();
		assertEquals("ACGTACGT", referenceDatastore.get("ref").getSequence().toString());
		
		NucleotideSequenceDataStore readDatastore =getReadSequenceDataStore(parser);
		assertEquals("unspecified insertion should be N", 
				"ACGTNACGT", readDatastore.get("read0").toString());

		assertEquals("no match should be 10 Ns", 
				"NNNNNNNNNN", readDatastore.get("read1").toString());
	
		assertEquals("reversed read should have left insertions on the right", 
				"ACGTANNNN", readDatastore.get("read2").toString());
	}
	
	
	
	private CasParser setup3Reads(CasParserTestDouble.Builder sut)
			throws IOException {
		sut.addReference("ref", "ACGTACGT");
		
		sut.forwardMatch("ref", 0)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
				.addAlignmentRegion(CasAlignmentRegionType.INSERT, 1)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
				.build()
			.unMatched()
			.reverseMatch("ref", 3)
				.addAlignmentRegion(CasAlignmentRegionType.INSERT, 4)
				.addAlignmentRegion(CasAlignmentRegionType.MATCH_MISMATCH, 5)
				.build();
		CasParser parser = sut.build();
		
		CasFileVisitor mockVisitor = createNiceMock(CasFileVisitor.class);
		CasMatchVisitor matchVisitor = createMock(CasMatchVisitor.class);
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				
				//assertEquals(Range.ofLength(9), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(0,alignment.getStartOfMatch());
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(3, alignments.size());

				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(0).getType());
				assertEquals(4, alignments.get(0).getLength());
				
				assertEquals(CasAlignmentRegionType.INSERT, alignments.get(1).getType());
				assertEquals(1, alignments.get(1).getLength());
				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(2).getType());
				assertEquals(4, alignments.get(2).getLength());
				
				return null;
			}
			
		});
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				assertFalse(match.matchReported());
				assertNull(match.getChosenAlignment());
				
				assertNull(match.getTrimRange());
				
				
				return null;
			}
			
		});
		
		matchVisitor.visitMatch(isA(CasMatch.class));
		expectLastCall().andAnswer(new IAnswer<Void>() {

			@Override
			public Void answer() throws Throwable {
				CasMatch match = (CasMatch) getCurrentArguments()[0];
				
				CasAlignment alignment = match.getChosenAlignment();
				assertEquals(3,alignment.getStartOfMatch());
				//reverse complement valid range so insertion at the end
			//	assertEquals(Range.ofLength(8), match.getTrimRange());
				assertNull(match.getTrimRange());
				
				List<CasAlignmentRegion> alignments =alignment.getAlignmentRegions();
				
				assertEquals(2, alignments.size());


				assertEquals(CasAlignmentRegionType.INSERT, alignments.get(0).getType());
				assertEquals(4, alignments.get(0).getLength());

				
				assertEquals(CasAlignmentRegionType.MATCH_MISMATCH, alignments.get(1).getType());
				assertEquals(5, alignments.get(1).getLength());
				
				
				return null;
			}
			
		});
		matchVisitor.visitEnd();
		
		expect(mockVisitor.visitMatches(isA(CasVisitorCallback.class))).andReturn(matchVisitor);
		
		
		
		replay(mockVisitor, matchVisitor);
		
		parser.parse(mockVisitor);
		
		verify(mockVisitor, matchVisitor);
		return parser;
	}
}
