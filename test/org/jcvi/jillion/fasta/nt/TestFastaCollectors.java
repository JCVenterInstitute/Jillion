package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.fasta.FastaCollectors;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import lombok.Data;

import static org.junit.Assert.*;
public class TestFastaCollectors {

	private static ResourceHelper RESOURCES = new ResourceHelper(TestFastaCollectors.class);
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	private static NucleotideFastaDataStore expected;
	
	@BeforeClass
	public static void parseActual() throws IOException {
		expected = new NucleotideFastaFileDataStoreBuilder(RESOURCES.getFile("files/19150.fasta")).build();
		
	}
	
	@AfterClass
	public static void closeActual() throws IOException {
		expected.close();
	}
	@Test
	public void collectToInMemoryDataStore() throws IOException {
		
		NucleotideFastaDataStore actual = expected.records()
												.collect(FastaCollectors.toDataStore(NucleotideFastaDataStore.class));
		
		assertMatchesExpected(actual);
	}

	private void assertMatchesExpected(NucleotideFastaDataStore actual) throws DataStoreException {
		assertEquals(expected.getNumberOfRecords(), actual.getNumberOfRecords());
		try(StreamingIterator<NucleotideFastaRecord> iter = actual.iterator()){
			while(iter.hasNext()) {
				NucleotideFastaRecord a = iter.next();
				
				assertEquals(expected.get(a.getId()), a);
			}
		}
	}
	
	@Test
	public void writeToFile() throws IOException {
		File output = tmpDir.newFile();
		try(Stream<NucleotideFastaRecord> stream = expected.records()){
				NucleotideFastaWriter writer = new NucleotideFastaWriterBuilder(output).build();
				stream.collect(FastaCollectors.writeAndClose(writer));
				assertClosed(writer);
		}
		
		assertMatchesExpected(new NucleotideFastaFileDataStoreBuilder(output).build());
		
	}
	@Test
	public void collectMultipleTimes() throws IOException {
		File output = tmpDir.newFile();
		try(NucleotideFastaWriter writer = new NucleotideFastaWriterBuilder(output).build()){
			try(Stream<NucleotideFastaRecord> stream = expected.records()){
					
					stream.filter(r-> Integer.parseInt(r.getId()) < 5)
					.collect(FastaCollectors.write(writer));
					
			}
			try(Stream<NucleotideFastaRecord> stream = expected.records()){
				
				stream.filter(r-> Integer.parseInt(r.getId()) >= 5)
				.collect(FastaCollectors.write(writer));
				
			}
		}
		
		assertMatchesExpected(new NucleotideFastaFileDataStoreBuilder(output).build());
		
	}
	@Test
	public void writeToFileMapped() throws IOException {
		File output = tmpDir.newFile();
		try(Stream<NucleotideFastaRecord> stream = expected.records()){
			//the reverse complement is just so we have some kind of mapping to do...
				NucleotideFastaWriter writer = new NucleotideFastaWriterBuilder(output).build();
				ThrowingBiConsumer<NucleotideFastaWriter, IdAndSeqAndComment, IOException> consumer = (w, r) -> w.write(r.getId(), r.getSeq().reverseComplement(), r.getComment());
				stream.map(f-> new IdAndSeqAndComment(f.getId(), 
						f.getSequence().reverseComplement(),
						f.getComment()))
				
				.collect(FastaCollectors.writeAndClose(writer, 
						consumer));
				assertClosed(writer);
		}
		
		assertMatchesExpected(new NucleotideFastaFileDataStoreBuilder(output).build());
		
	}
	@Test
	public void writeToFileMappedMultipleTimes() throws IOException {
		File output = tmpDir.newFile();
		try(NucleotideFastaWriter writer = new NucleotideFastaWriterBuilder(output).build()){
			try(Stream<NucleotideFastaRecord> stream = expected.records()){
				//the reverse complement is just so we have some kind of mapping to do...
					
					stream
					.filter(r-> Integer.parseInt(r.getId()) < 5)
					.map(f-> new IdAndSeqAndComment(f.getId(), 
							f.getSequence().reverseComplement(),
							f.getComment()))
					
					.collect(FastaCollectors.write(writer, 
							r -> new NucleotideFastaRecordBuilder(r.getId(), r.getSeq().reverseComplement())
														.comment( r.getComment())
														.build()));
					
			}
			try(Stream<NucleotideFastaRecord> stream = expected.records()){
				//the reverse complement is just so we have some kind of mapping to do...
					
					stream
					.filter(r-> Integer.parseInt(r.getId()) >= 5)
					.map(f-> new IdAndSeqAndComment(f.getId(), 
							f.getSequence().reverseComplement(),
							f.getComment()))
				
					.collect(FastaCollectors.write(writer, 
							r -> new NucleotideFastaRecordBuilder(r.getId(), r.getSeq().reverseComplement())
							.comment( r.getComment())
							.build()));
			}
		}
		
		assertMatchesExpected(new NucleotideFastaFileDataStoreBuilder(output).build());
		
	}
	private static void assertClosed(NucleotideFastaWriter writer) {
		try{
			writer.write("foo", NucleotideSequence.of("ACGT"));
			fail("should throw IOException when closed");
		}catch(IOException t) {
			//pass
		}
	}
	
	@Data
	private static class IdAndSeqAndComment{
		private final String id;
		private final NucleotideSequence seq;
		private final String comment;
	}
}
