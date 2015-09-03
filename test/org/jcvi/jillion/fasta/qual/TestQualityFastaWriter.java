/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.qual;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.fasta.qual.LargeQualityFastaFileDataStore;
import org.junit.Test;

public class TestQualityFastaWriter {
	private final QualityFastaRecord record1 = new QualityFastaRecordBuilder("id_1", 
						new QualitySequenceBuilder(new byte[]{8,9,10,11,12,13,14,15}).build())
						.comment("a comment")
						.build();
	private final QualityFastaRecord record2 = 
			new QualityFastaRecordBuilder("id_2", 
									new QualitySequenceBuilder(new byte[]{20,20,20,20,30,30,30,30,40,40,40,40})
											.build())
			.build();
	
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new QualityFastaWriterBuilder((OutputStream)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws IOException{
		new QualityFastaWriterBuilder((File)null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativeBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new QualityFastaWriterBuilder(out)
			.numberPerLine(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new QualityFastaWriterBuilder(out)
			.numberPerLine(0);
	}
	@Test
	public void writeFastasWithDefaultOptions() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualityFastaWriter sut = new QualityFastaWriterBuilder(out)
													.build();
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"08 09 10 11 12 13 14 15\n"+
							">id_2\n"+
							"20 20 20 20 30 30 30 30 40 40 40 40\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void multiLineFastas() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualityFastaWriter sut = new QualityFastaWriterBuilder(out)
								.numberPerLine(5)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"08 09 10 11 12\n13 14 15\n"+
							">id_2\n"+
							"20 20 20 20 30\n30 30 30 40 40\n40 40\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void sequenceEndsAtEndOfLineExactly() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualityFastaWriter sut = new QualityFastaWriterBuilder(out)
								.numberPerLine(4)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"08 09 10 11\n12 13 14 15\n"+
							">id_2\n"+
							"20 20 20 20\n30 30 30 30\n40 40 40 40\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	
	@Test
	public void differentCharSet() throws IOException{
		Charset charSet = Charset.forName("UTF-16");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualityFastaWriter sut = new QualityFastaWriterBuilder(out)
								.numberPerLine(5)	
								.charset(charSet)
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"08 09 10 11 12\n13 14 15\n"+
							">id_2\n"+
							"20 20 20 20 30\n30 30 30 40 40\n40 40\n";
		byte[] expectedBytes = expected.getBytes(charSet);
		assertArrayEquals(expectedBytes, out.toByteArray());
	}
	
	@Test
	public void parseAndWriteShouldMatch() throws IOException, DataStoreException{
		ResourceHelper resources = new ResourceHelper(TestQualityFastaWriter.class);
		File expectedFasta = resources.getFile("files/19150.qual");
		QualityFastaDataStore datastore = LargeQualityFastaFileDataStore.create(expectedFasta);
		
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		QualityFastaWriter sut = new QualityFastaWriterBuilder(out).build();
		StreamingIterator<QualityFastaRecord> iter=null;
		
		try{
			iter = datastore.iterator();
			while(iter.hasNext()){
				sut.write(iter.next());
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		sut.close();
		InputStream in = new FileInputStream(expectedFasta);
		byte[] expectedBytes;
		try{
			expectedBytes = IOUtil.toByteArray(in);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
		assertArrayEquals(expectedBytes, out.toByteArray());
	}
	@Test
        public void testInMemorySorting() throws IOException, DataStoreException{
	    testSorting((builder, comparator)-> builder.sortInMemoryOnly(comparator));
	}
	
	@Test
        public void testTmpDirSortingAllInMemory() throws IOException, DataStoreException{
            testSorting((builder, comparator)-> builder.sort(comparator, 1_000_000));
        }
	@Test
        public void testTmpDirSorting() throws IOException, DataStoreException{
            testSorting((builder, comparator)-> builder.sort(comparator, 5));
        }

    private void testSorting(BiConsumer<QualityFastaWriterBuilder, Comparator<QualityFastaRecord>> consumer)
            throws IOException, DataStoreException {

        ResourceHelper resources = new ResourceHelper(
                TestQualityFastaWriter.class);
        File expectedFasta = resources.getFile("files/19150.qual");
        Comparator<QualityFastaRecord> comparator = (a, b) -> b.getId()
                .compareTo(a.getId());

        List<QualityFastaRecord> list = new ArrayList<QualityFastaRecord>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        QualityFastaWriterBuilder builder = new QualityFastaWriterBuilder(out);
        consumer.accept(builder, comparator);
        try (

        QualityFastaDataStore datastore = new QualityFastaFileDataStoreBuilder(
                expectedFasta).build();
                StreamingIterator<QualityFastaRecord> iter = datastore
                        .iterator();

                QualityFastaWriter sut = builder.build()) {
            while (iter.hasNext()) {
                QualityFastaRecord record = iter.next();
                list.add(record);
                sut.write(record);
            }

        }
        Collections.sort(list, comparator);
        try (QualityFastaDataStore actual = new QualityFastaFileDataStoreBuilder(
                new ByteArrayInputStream(out.toByteArray())).build();
                StreamingIterator<QualityFastaRecord> actualIter = actual
                        .iterator();) {
            List<QualityFastaRecord> actualList = new ArrayList<>();
            while (actualIter.hasNext()) {

                actualList.add(actualIter.next());
            }

            assertEquals(list, actualList);
        }
    }
}
