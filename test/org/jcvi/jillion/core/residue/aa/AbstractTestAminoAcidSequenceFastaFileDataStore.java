package org.jcvi.jillion.core.residue.aa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecordBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.AfterClass;
import org.junit.Test;
public abstract class AbstractTestAminoAcidSequenceFastaFileDataStore {

	private static AminoAcidSequenceFastaDataStore sut;
	private static File fastaFile;
	private static DataStoreProviderHint hint;
	
	private final AminoAcidSequenceFastaRecord last = new AminoAcidSequenceFastaRecordBuilder("read4","SEDDEHIKFTW")
																.build();
	private final AminoAcidSequenceFastaRecord second = new AminoAcidSequenceFastaRecordBuilder("read2","IKFTWMKAILSEDDEH")
																.comment("comment")
																.build();
	private final AminoAcidSequenceFastaRecord first = new AminoAcidSequenceFastaRecordBuilder("read1","IKFTW")
																	.build();
	public AbstractTestAminoAcidSequenceFastaFileDataStore(DataStoreProviderHint hint) throws IOException{
		ResourceHelper helper = new ResourceHelper(AbstractTestAminoAcidSequenceFastaFileDataStore.class);
		fastaFile = helper.getFile("files/aa.fasta");
		AbstractTestAminoAcidSequenceFastaFileDataStore.hint = hint;
		
	}
	
	@AfterClass
	public static void closeDataStore() throws IOException{
		sut.close();
		sut=null;
	}
	
	private static AminoAcidSequenceFastaDataStore getSut() throws IOException{
		if(sut ==null){
			sut = new AminoAcidSequenceFastaFileDataStoreBuilder(fastaFile)
			.hint(hint)
			.filter(DataStoreFilters.newExcludeFilter(Arrays.asList("read3")))
			.build();
		}
		return sut;
	}
	@Test
	public void size() throws DataStoreException, IOException{
		assertEquals(3, getSut().getNumberOfRecords());
	}
	
	@Test
	public void getFirst() throws DataStoreException, IOException{		
		assertEquals(first, getSut().get("read1"));
	}
	@Test
	public void getSecond() throws DataStoreException, IOException{		
		assertEquals(second, getSut().get("read2"));
	}
	@Test
	public void getLast() throws DataStoreException, IOException{		
		assertEquals(last, getSut().get("read4"));
	}
	
	@Test
	public void doesNotContainFilteredRead() throws DataStoreException, IOException{
		assertFalse(getSut().contains("read3"));
	}
	
	@Test
	public void iterator() throws DataStoreException, IOException{
		Iterator<AminoAcidSequenceFastaRecord> expected = Arrays.asList(first,second,last).iterator();
		
		StreamingIterator<AminoAcidSequenceFastaRecord> actual = null;
		try{
			actual = getSut().iterator();
			while(expected.hasNext()){
				assertTrue(actual.hasNext());
				assertEquals(expected.next(), actual.next());
			}
			assertFalse(actual.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(actual);
		}
	}
	@Test
	public void idIterator() throws DataStoreException, IOException{
		Iterator<String> expected = Arrays.asList(first.getId(),
															second.getId(),
															last.getId())
																.iterator();
		
		StreamingIterator<String> actual = null;
		try{
			actual = getSut().idIterator();
			while(expected.hasNext()){
				assertTrue(actual.hasNext());
				assertEquals(expected.next(), actual.next());
			}
			assertFalse(actual.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(actual);
		}
	}
	
}
