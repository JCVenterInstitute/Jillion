package org.jcvi.jillion.assembly.clc.cas;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.easymock.EasyMockSupport;
import org.jcvi.jillion.assembly.clc.cas.CasFileVisitor.CasVisitorCallback;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecordBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriterBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
/**
 * Regression tests to make sure we don't
 * re-introduce bug found in VHTNGS-603
 * if no reads mapped to the a reference then a NullPointer was thrown
 * when we tried to build the gapped sequence datastore.
 * @author dkatzel
 *
 */
public class TestGappedReferenceDataStoreBuilderNoReadsMaptoReference extends EasyMockSupport{

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	private CasGappedReferenceDataStoreBuilderVisitor sut;
	
	private NucleotideFastaRecord ref1, ref2;
	
	private CasFileInfo refFileInfo;
	
	@Before
	public void createInputData() throws IOException{
		ref1 = new NucleotideSequenceFastaRecordBuilder("ref1", 
				new NucleotideSequenceBuilder("ACGTACGT")
					.build())
			.build();

		ref2 = new NucleotideSequenceFastaRecordBuilder("ref2", 
							new NucleotideSequenceBuilder("AAAAAAAAAAAA")
								.build())
						.build();
		
		File fastaFile = createTempFastaFile(ref1, ref2);
		
		refFileInfo = createMock(CasFileInfo.class);
		expect(refFileInfo.getFileNames()).andReturn(Collections.singletonList(fastaFile.getName()));
		
		
		sut = new CasGappedReferenceDataStoreBuilderVisitor(folder.getRoot());

	}
	
	@Test
	public void noReadsMappedShouldUseUnchangedReferenceSequence() throws DataStoreException{
		replayAll();
		
		sut.visitReferenceFileInfo(refFileInfo);		
		sut.visitEnd();		
		CasGappedReferenceDataStore datastore = sut.build();
		
		assertEquals(2, datastore.getNumberOfRecords());
		assertEquals(ref1.getSequence(), datastore.get(ref1.getId()));
		assertEquals(ref2.getSequence(), datastore.get(ref2.getId()));
	
	}
	
	@Test
	public void readsOnlyMappedToOneOfTheReferencesShouldUseUnchangedReferenceSequenceForOther() throws DataStoreException{
		
		CasMatch match = createMatch(new DefaultCasAlignment.Builder(1, 0, false)
										.addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 4)
										.addRegion(CasAlignmentRegionType.INSERT, 1)
										.addRegion(CasAlignmentRegionType.MATCH_MISMATCH, 1)
										.build());
		replayAll();
		
		sut.visitReferenceFileInfo(refFileInfo);	
		
		CasMatchVisitor matchVisitor = sut.visitMatches(createMock(CasVisitorCallback.class));
		
		matchVisitor.visitMatch(match);
		matchVisitor.visitEnd();
		
		sut.visitEnd();		
		CasGappedReferenceDataStore datastore = sut.build();
		
		assertEquals(2, datastore.getNumberOfRecords());
		assertEquals(ref1.getSequence(), datastore.get(ref1.getId()));
		assertEquals(new NucleotideSequenceBuilder(ref2.getSequence())
								.insert(4, Nucleotide.Gap)
								.build()
					, datastore.get(ref2.getId()));
	
	}

	private CasMatch createMatch(CasAlignment alignment){
		CasMatch match = createMock(CasMatch.class);
		expect(match.matchReported()).andReturn(true);
		
		expect(match.getChosenAlignment()).andReturn(alignment);
		return match;
	}


	private File createTempFastaFile(NucleotideFastaRecord...fastaRecords ) throws IOException {
		File fasta = folder.newFile("ref.fasta");
		NucleotideFastaRecordWriter writer = new NucleotideFastaRecordWriterBuilder(fasta).build();
		for(NucleotideFastaRecord r : fastaRecords){
			writer.write(r);
		}
		writer.close();
		return fasta;
	}
}
