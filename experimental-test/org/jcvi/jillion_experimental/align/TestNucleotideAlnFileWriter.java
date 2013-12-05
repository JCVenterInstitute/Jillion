package org.jcvi.jillion_experimental.align;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestNucleotideAlnFileWriter {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	private File out;
	private NucleotideSequenceDataStore datastore;
	@Before
	public void setup() throws IOException{
		out = tmpDir.newFile();
		File alnIn =new ResourceHelper(TestNucleotideAlnFileWriter.class).getFile("files/example.aln");
	
		datastore = GappedNucleotideAlignmentDataStore.createFromAlnFile(alnIn);
	}
	
	@Test
	public void writeDefaultNumberOfResidues() throws IOException, DataStoreException{		
		
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																	.build();
		
		writeAndAssertDataWrittenCorrectly(writer);
	}
	
	@Test
	public void differentEOL() throws IOException, DataStoreException{		
		
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																		.eol("\r\n")
																			.build();
		
		writeAndAssertDataWrittenCorrectly(writer);
	}
	
	@Test
	public void differentNumberOfGroups() throws IOException, DataStoreException{		
		
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																		.setNumResiduesPerGroup(10)
																			.build();
		
		writeAndAssertDataWrittenCorrectly(writer);
	}
	
	@Test
	public void forceOnly1Group() throws IOException, DataStoreException{		
		
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																		.forceOneGroupOnly()
																			.build();
		
		writeAndAssertDataWrittenCorrectly(writer);
	}

	@Test(expected = NullPointerException.class)
	public void nullIdShouldThrowNPE() throws IOException{
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																.build();
		try{
			writer.write(null, new NucleotideSequenceBuilder("ACGT").build());
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}

	}
	@Test(expected = IllegalArgumentException.class)
	public void nullSequenceShouldThrowNPE() throws IOException{
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																.build();
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGT").build();
		try{
			writer.write("id", seq);
			writer.write("id", seq);
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}

	}
	
	@Test(expected = NullPointerException.class)
	public void writingSameIdTwiceShouldThrowException() throws IOException{
		AlnFileWriter<Nucleotide, NucleotideSequence> writer = AlnFileWriter.createNucleotideWriterBuilder(out)
																.build();
		
		try{
			writer.write("id", null);
		}finally{
			IOUtil.closeAndIgnoreErrors(writer);
		}

	}
	
	
	private void writeAndAssertDataWrittenCorrectly(
			AlnFileWriter<Nucleotide, NucleotideSequence> writer)
			throws DataStoreException, IOException {
		StreamingIterator<String> idIter = datastore.idIterator();
		try{
			while(idIter.hasNext()){
				String id =idIter.next();
				writer.write(id,  datastore.get(id));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(idIter, writer);
		}
		NucleotideSequenceDataStore actual = GappedNucleotideAlignmentDataStore.createFromAlnFile(out);
		assertEquals(datastore.getNumberOfRecords(), actual.getNumberOfRecords());
		StreamingIterator<String> iter = datastore.idIterator();
		try{
			while(iter.hasNext()){
				String id = iter.next();
				assertEquals(datastore.get(id), actual.get(id));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter, actual);
		}
	}

}
