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
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.phd.ArtificalPhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author dkatzel
 * 
 * 
 */
public class TestDefaultAceFileWriter {

	private final ResourceHelper resources = new ResourceHelper(
			TestAceFileUtil_writingAceContigs.class);
	private File tmpDir;
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setupTempFolder() throws IOException {
		tmpDir = folder.newFolder("temp");
	}

	@Test
	public void convertCtg2Ace() throws IOException, DataStoreException {
		File contigFile = resources.getFile("files/flu_644151.contig");
		File seqFile = resources.getFile("files/flu_644151.seq");
		File qualFile = resources.getFile("files/flu_644151.qual");

		final Date phdDate = new Date(0L);
		NucleotideSequenceDataStore nucleotideDataStore = NucleotideFastaFileDataStore.fromFile(seqFile).asSequenceDataStore();
		final QualityFastaDataStore qualityFastaDataStore = new QualityFastaFileDataStoreBuilder(
				qualFile).build();

		PhdDataStore phdDataStore = new ArtificalPhdDataStore(
				nucleotideDataStore, qualityFastaDataStore.asSequenceDataStore(), phdDate);

		AceFileDataStore aceDataStore = AceAdapterContigFileDataStore
				.create(qualityFastaDataStore, phdDate, contigFile);

		File outputFile = folder.newFile();

		AceFileWriter sut = new AceFileWriterBuilder(outputFile, phdDataStore)
				.tmpDir(tmpDir).build();
		writeContigs(aceDataStore, sut);
		sut.close();

		AceFileDataStore reparsedAceDataStore = DefaultAceFileDataStore
				.create(outputFile);
		assertContigsAreEqual(aceDataStore, reparsedAceDataStore);
	}
	
	@Test
	public void callingClose2xShouldIgnore2ndCall() throws IOException, DataStoreException {
		File contigFile = resources.getFile("files/flu_644151.contig");
		File seqFile = resources.getFile("files/flu_644151.seq");
		File qualFile = resources.getFile("files/flu_644151.qual");

		final Date phdDate = new Date(0L);
		NucleotideSequenceDataStore nucleotideDataStore = NucleotideFastaFileDataStore.fromFile(seqFile).asSequenceDataStore();
		final QualityFastaDataStore qualityFastaDataStore = new QualityFastaFileDataStoreBuilder(
				qualFile).build();

		PhdDataStore phdDataStore = new ArtificalPhdDataStore(
				nucleotideDataStore, qualityFastaDataStore.asSequenceDataStore(), phdDate);

		AceFileDataStore aceDataStore = AceAdapterContigFileDataStore
				.create(qualityFastaDataStore, phdDate, contigFile);

		File outputFile = folder.newFile();

		AceFileWriter sut = new AceFileWriterBuilder(outputFile, phdDataStore)
				.tmpDir(tmpDir).build();
		writeContigs(aceDataStore, sut);
		sut.close();
		sut.close();
		AceFileDataStore reparsedAceDataStore = DefaultAceFileDataStore
				.create(outputFile);
		assertContigsAreEqual(aceDataStore, reparsedAceDataStore);
	}

	@Test
	public void convertCtg2AceWithComputedConsensusQualities()
			throws IOException, DataStoreException {
		File contigFile = resources.getFile("files/flu_644151.contig");
		File seqFile = resources.getFile("files/flu_644151.seq");
		File qualFile = resources.getFile("files/flu_644151.qual");

		final Date phdDate = new Date(0L);
		NucleotideSequenceDataStore nucleotideDataStore = NucleotideFastaFileDataStore.fromFile(seqFile).asSequenceDataStore();
		final QualityFastaDataStore qualityFastaDataStore = new QualityFastaFileDataStoreBuilder(
				qualFile).build();

		PhdDataStore phdDataStore = new ArtificalPhdDataStore(
				nucleotideDataStore, qualityFastaDataStore.asSequenceDataStore(), phdDate);

		AceFileDataStore aceDataStore = AceAdapterContigFileDataStore
				.create(qualityFastaDataStore, phdDate, contigFile,true);

		File outputFile = folder.newFile();

		AceFileWriter sut = new AceFileWriterBuilder(outputFile, phdDataStore)
				.tmpDir(tmpDir).build();
		writeContigs(aceDataStore, sut);
		sut.close();

		final Map<String, QualitySequence> actualConsensusQualities = new HashMap<String, QualitySequence>();

		AceFileVisitor visitor = new AbstractAceFileVisitor() {

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, final String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				return new AbstractAceContigVisitor() {

					@Override
					public void visitConsensusQualities(
							QualitySequence ungappedConsensusQualities) {
						actualConsensusQualities.put(contigId,
								ungappedConsensusQualities);
					}

				};
			}

		};

		AceFileParser.create(outputFile).parse(visitor);

		Map<String, QualitySequence> expectedConsensusQualities = getExpectedConsensusQualities();
		assertEquals(expectedConsensusQualities, actualConsensusQualities);

		AceFileDataStore reparsedAceDataStore = DefaultAceFileDataStore
				.create(outputFile);
		assertContigsAreEqual(aceDataStore, reparsedAceDataStore);
	}

	private Map<String, QualitySequence> getExpectedConsensusQualities()
			throws IOException, DataStoreException {
		File qualFile =resources.getFile("files/expectedConsensus.qual");
		QualityFastaDataStore datastore =new QualityFastaFileDataStoreBuilder(qualFile)
					.hint(DataStoreProviderHint.ITERATION_ONLY)
					.build();
		Map<String, QualitySequence> map = new HashMap<String, QualitySequence>();
		StreamingIterator<QualityFastaRecord> iter =null;
		try{
			iter = datastore.iterator();
			while(iter.hasNext()){
				QualityFastaRecord next = iter.next();
				map.put(next.getId(), next.getSequence());
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter, datastore);
		}
		return map;
	}

	@Test
	public void convertCtg2AceWithBaseSegments() throws IOException,
			DataStoreException {
		File contigFile = resources.getFile("files/flu_644151.contig");
		File seqFile = resources.getFile("files/flu_644151.seq");
		File qualFile = resources.getFile("files/flu_644151.qual");

		final Date phdDate = new Date(0L);
		NucleotideSequenceDataStore nucleotideDataStore = 
						NucleotideFastaFileDataStore.fromFile(seqFile).asSequenceDataStore();
		final QualityFastaDataStore qualityFastaDataStore = new QualityFastaFileDataStoreBuilder(
				qualFile).build();

		PhdDataStore phdDataStore = new ArtificalPhdDataStore(
				nucleotideDataStore, qualityFastaDataStore.asSequenceDataStore(), phdDate);

		AceFileDataStore aceDataStore = AceAdapterContigFileDataStore
				.create(qualityFastaDataStore, phdDate, contigFile);

		File outputFile = folder.newFile();

		AceFileWriter sut = new AceFileWriterBuilder(outputFile, phdDataStore)
				.tmpDir(tmpDir).includeBaseSegments().build();
		// writeContigs(aceDataStore, sut);
		// can't write out all contigs because some have ambiguities
		sut.write(aceDataStore.get("98"));
		sut.write(aceDataStore.get("97"));
		sut.write(aceDataStore.get("96"));
		sut.write(aceDataStore.get("95"));
		sut.close();

		AceFileDataStore reparsedAceDataStore = DefaultAceFileDataStore
				.create(outputFile);
		assertContigHasSameRecords(aceDataStore.get("98"),
				reparsedAceDataStore.get("98"));
		assertContigHasSameRecords(aceDataStore.get("97"),
				reparsedAceDataStore.get("97"));
		assertContigHasSameRecords(aceDataStore.get("96"),
				reparsedAceDataStore.get("96"));
		assertContigHasSameRecords(aceDataStore.get("95"),
				reparsedAceDataStore.get("95"));
	}

	private void assertContigsAreEqual(AceFileDataStore aceDataStore,
			AceFileDataStore reparsedAceDataStore)
			throws DataStoreException {
		assertEquals("# contigs", aceDataStore.getNumberOfRecords(),
				reparsedAceDataStore.getNumberOfRecords());

		StreamingIterator<AceContig> contigIter = aceDataStore.iterator();
		try {
			while (contigIter.hasNext()) {
				AceContig expectedContig = contigIter.next();
				AceContig actualContig = reparsedAceDataStore
						.get(expectedContig.getId());
				assertContigHasSameRecords(expectedContig, actualContig);
			}
		} finally {
			IOUtil.closeAndIgnoreErrors(contigIter);
		}
	}

	private void assertContigHasSameRecords(AceContig expectedContig,
			AceContig actualContig) {
		assertEquals("consensus", expectedContig.getConsensusSequence(),
				actualContig.getConsensusSequence());
		assertEquals("# reads", expectedContig.getNumberOfReads(),
				actualContig.getNumberOfReads());
		StreamingIterator<AceAssembledRead> readIter = null;
		try {
			readIter = expectedContig.getReadIterator();
			while (readIter.hasNext()) {
				AceAssembledRead expectedRead = readIter.next();
				String id = expectedRead.getId();
				AceAssembledRead actualRead = actualContig.getRead(expectedRead
						.getId());
				assertEquals(id + " basecalls",
						expectedRead.getNucleotideSequence(),
						actualRead.getNucleotideSequence());
				assertEquals(id + " offset",
						expectedRead.getGappedStartOffset(),
						actualRead.getGappedStartOffset());
				assertEquals(id + " validRange", expectedRead.getReadInfo()
						.getValidRange(), actualRead.getReadInfo()
						.getValidRange());
				assertEquals(id + " dir", expectedRead.getDirection(),
						actualRead.getDirection());

			}
		} finally {
			IOUtil.closeAndIgnoreErrors(readIter);
		}
	}

	private void writeContigs(AceFileDataStore aceDataStore,
			AceFileWriter sut) throws DataStoreException, IOException {
		StreamingIterator<AceContig> iter = aceDataStore.iterator();
		try {
			while (iter.hasNext()) {
				AceContig next = iter.next();
				sut.write(next);
			}
		} finally {
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}

	@Test
	public void rewritingAceShouldBeSimilar() throws IOException,
			DataStoreException {
		File originalAce = resources.getFile("files/sample.ace");

		PhdDataStore phdDataStore = HighLowAceContigPhdDatastore
				.create(originalAce);

		File outputFile = folder.newFile();

		AceFileWriter sut = new AceFileWriterBuilder(outputFile, phdDataStore)
				.tmpDir(tmpDir).build();
		AceFileDataStore datastore = DefaultAceFileDataStore
				.create(originalAce);

		// lets write out the tags first to make sure they get put at the end
		// correctly
		writeReadTags(datastore, sut);
		writeWholeAssemblyTags(datastore, sut);
		writeConsensusTags(datastore, sut);

		writeContigs(datastore, sut);

		sut.close();
		AceFileDataStore reparsedAceDataStore = DefaultAceFileDataStore
				.create(outputFile);
		assertContigsAreEqual(datastore, reparsedAceDataStore);

		assertWholeReadTagsAreEqual(datastore, reparsedAceDataStore);
		assertReadTagsAreEqual(datastore, reparsedAceDataStore);
		assertConsensusTagsAreEqual(datastore, reparsedAceDataStore);

	}

	private void assertConsensusTagsAreEqual(AceFileDataStore datastore,
			AceFileDataStore reparsedAceDataStore)
			throws DataStoreException {
		StreamingIterator<ConsensusAceTag> expected = datastore
				.getConsensusTagIterator();
		StreamingIterator<ConsensusAceTag> actual = datastore
				.getConsensusTagIterator();

		while (expected.hasNext()) {
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());

	}

	private void assertReadTagsAreEqual(AceFileDataStore datastore,
			AceFileDataStore reparsedAceDataStore)
			throws DataStoreException {
		StreamingIterator<ReadAceTag> expected = datastore.getReadTagIterator();
		StreamingIterator<ReadAceTag> actual = datastore.getReadTagIterator();

		while (expected.hasNext()) {
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());

	}

	private void assertWholeReadTagsAreEqual(AceFileDataStore datastore,
			AceFileDataStore reparsedAceDataStore)
			throws DataStoreException {
		StreamingIterator<WholeAssemblyAceTag> expected = datastore
				.getWholeAssemblyTagIterator();
		StreamingIterator<WholeAssemblyAceTag> actual = datastore
				.getWholeAssemblyTagIterator();

		while (expected.hasNext()) {
			assertTrue(actual.hasNext());
			assertEquals(expected.next(), actual.next());
		}
		assertFalse(actual.hasNext());
	}

	private void writeReadTags(AceFileDataStore datastore,
			AceFileWriter sut) throws IOException, DataStoreException {
		StreamingIterator<ReadAceTag> iter = datastore.getReadTagIterator();
		try {
			while (iter.hasNext()) {
				sut.write(iter.next());
			}
		} finally {
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}

	private void writeWholeAssemblyTags(AceFileDataStore datastore,
			AceFileWriter sut) throws IOException, DataStoreException {
		StreamingIterator<WholeAssemblyAceTag> iter = datastore
				.getWholeAssemblyTagIterator();
		try {
			while (iter.hasNext()) {
				sut.write(iter.next());
			}
		} finally {
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}

	private void writeConsensusTags(AceFileDataStore datastore,
			AceFileWriter sut) throws IOException, DataStoreException {
		StreamingIterator<ConsensusAceTag> iter = datastore
				.getConsensusTagIterator();
		try {
			while (iter.hasNext()) {
				sut.write(iter.next());
			}
		} finally {
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
}
