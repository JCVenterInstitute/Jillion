package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.fasta.DefaultFastaIndex;
import org.jcvi.jillion.internal.fasta.FastaIndex;
import org.jcvi.jillion.internal.fasta.FastaUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestFaiWriter {

	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();
	
	
	@Test
	public void createFaiFile() throws IOException{
		ResourceHelper helper = new ResourceHelper(getClass());
		File fasta = helper.getFile("files/no_extra_on_defline.XXXXX.combo2.i.contigs");
		
		File fai = tmp.newFile();
		writeFai(fasta, fai);
		
		FastaIndex actualIndex = DefaultFastaIndex.parse(fai);
		
		FastaIndex expectedIndex = DefaultFastaIndex.parse(helper.getFile("files/no_extra_on_defline.XXXXX.combo2.i.contigs.fai"));
		
		for(String id : Arrays.asList("MAINa", "MAINb")){
			assertEquals(id, expectedIndex.getIndexFor(id), actualIndex.getIndexFor(id));
		}
	}

	private void writeFai(File fasta, File fai) throws IOException, FileNotFoundException {
		try(PrintWriter writer = new PrintWriter(fai)){
			FastaUtil.createIndex(fasta, writer, (line)->(int) new NucleotideSequenceBuilder(line).getLength());
		}
	}
	
	@Test
	public void autoCreateFaiFromBuilder() throws IOException, DataStoreException{
		ResourceHelper helper = new ResourceHelper(getClass());
		
		File fasta = helper.getFile("files/no_extra_on_defline.XXXXX.combo2.i.contigs");
		
		File outputFasta = tmp.newFile("output.fasta");
		File expectedFai = tmp.newFile("expected.fasta.fai");
		
		try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(fasta).build();
			StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
			NucleotideFastaWriter writer = new NucleotideFastaWriterBuilder(outputFasta)
												.createIndex(true)
												.numberPerLine(25)
												.build();
				){
			while(iter.hasNext()){
				writer.write(iter.next());
			}
		}
		
		writeFai(outputFasta, expectedFai);
		
		assertTrue(expectedFai.length() > 0);
		
		File actual = new File(outputFasta.getParentFile(), outputFasta.getName() +".fai");
		TestUtil.assertContentsAreEqual(expectedFai, actual);
	}
}
