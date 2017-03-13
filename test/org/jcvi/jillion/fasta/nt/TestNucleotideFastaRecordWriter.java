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
package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Test;
public class TestNucleotideFastaRecordWriter {
	private final NucleotideFastaRecord record1 = 
			new NucleotideFastaRecordBuilder("id_1", "ACGTACGT")
						.comment("a comment")
						.build();
		
	private final NucleotideFastaRecord record2 = 
			new NucleotideFastaRecordBuilder("id_2","AAAACCCCGGGGTTTT").build();
	
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new NucleotideFastaWriterBuilder((OutputStream)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws IOException{
		new NucleotideFastaWriterBuilder((File)null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativeBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new NucleotideFastaWriterBuilder(out)
			.numberPerLine(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new NucleotideFastaWriterBuilder(out)
			.numberPerLine(0);
	}
	@Test(expected = IllegalArgumentException.class)
	public void emptyEOLShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new NucleotideFastaWriterBuilder(out)
			.lineSeparator("");
	}
	@Test
	public void writeFastasWithDefaultOptions() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
													.build();
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGTACGT\n"+
							">id_2\n"+
							"AAAACCCCGGGGTTTT\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void nullEOLShouldUseDefault() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
													.lineSeparator(null)
													.build();
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGTACGT\n"+
							">id_2\n"+
							"AAAACCCCGGGGTTTT\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void multiLineFastas() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
								.numberPerLine(5)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGTA\nCGT\n"+
							">id_2\n"+
							"AAAAC\nCCCGG\nGGTTT\nT\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void differentEOL() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
								.numberPerLine(5)
								.lineSeparator("\r\n")
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\r\n"+
							"ACGTA\r\nCGT\r\n"+
							">id_2\r\n"+
							"AAAAC\r\nCCCGG\r\nGGTTT\r\nT\r\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	
	@Test
	public void allOnOneLine() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
								.allBasesOnOneLine()											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		char[] seq = new char[1000];
		Arrays.fill(seq, 'G');
		
		sut.write("long", new NucleotideSequenceBuilder(new String(seq)).build());
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGTACGT\n"+
							">id_2\n"+
							"AAAACCCCGGGGTTTT\n" +
							">long\n"+
							new String(seq)+"\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void sequenceEndsAtEndOfLineExactly() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
								.numberPerLine(4)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGT\nACGT\n"+
							">id_2\n"+
							"AAAA\nCCCC\nGGGG\nTTTT\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	
	@Test
	public void differentCharSet() throws IOException{
		Charset charSet = Charset.forName("UTF-16");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
								.numberPerLine(5)	
								.charset(charSet)
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGTA\nCGT\n"+
							">id_2\n"+
							"AAAAC\nCCCGG\nGGTTT\nT\n";
		byte[] expectedBytes = expected.getBytes(charSet);
		assertArrayEquals(expectedBytes, out.toByteArray());
	}
	
	@Test
	public void testInMemorySortedFasta() throws IOException, DataStoreException{
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
            try(NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
                                                    .sortInMemoryOnly((a,b)-> b.getId().compareTo(a.getId()))
                                                    .build();
                    
                    ){
                sut.write(record1);
                sut.write(record2);
            }
            
            try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(new ByteArrayInputStream(out.toByteArray()))
                                                                .build();
            
                    StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
            ){
                assertEquals(record2, iter.next());
                assertEquals(record1, iter.next());
            }
	}
	
	
	@Test
	public void testSortedFastaButDontWriteToTempFile() throws IOException, DataStoreException{
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
            try(NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
                                                    .sort((a,b)-> b.getId().compareTo(a.getId()), 3)
                                                    .build();
                    
                    ){
                sut.write(record1);
                sut.write(record2);
            }
            
            try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(new ByteArrayInputStream(out.toByteArray()))
                                                                .build();
            
                    StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
            ){
                assertEquals(record2, iter.next());
                assertEquals(record1, iter.next());
            }
	}
	
	@Test
        public void testSortedFastaButWriteToTempFile() throws IOException, DataStoreException{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try(NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out)
                                                    .sort((a,b)-> b.getId().compareTo(a.getId()), 1)
                                                    .build();
                    
                    ){
                sut.write(record1);
                sut.write(record2);
            }
            
            try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(new ByteArrayInputStream(out.toByteArray()))
                                                                .build();
            
                    StreamingIterator<NucleotideFastaRecord> iter = datastore.iterator();
            ){
                assertEquals(record2, iter.next());
                assertEquals(record1, iter.next());
            }
        }
	
	
	@Test
	public void multiThreadWriter() throws Exception{
	    //use ExecutorService to reuse the same threads over and over again
	    //for better performance.
	    ExecutorService es = Executors.newFixedThreadPool(2);
	    //run this test many times 
	    //don't think this is really necessary since
	    //we use latches to run the writes at as close to exactly 
	    //the same time as possible but you never know... couldn't hurt.
	    for(int i=0; i< 200; i++){
    	        ByteArrayOutputStream out = new ByteArrayOutputStream();
                try(NucleotideFastaWriter sut = new NucleotideFastaWriterBuilder(out).build()){
                    
                    CountDownLatch latch = new CountDownLatch(2);
                    CountDownLatch doneLatch = new CountDownLatch(2);
                    
                    es.submit(()-> {
                        try{
                            latch.countDown();
                            latch.await();
                            sut.write(record1);                        
                        }catch(Exception e){
                            throw new RuntimeException(e);
                        }finally{
                            doneLatch.countDown();
                        }
                    });
                    
                    es.submit( ()-> {
                        try{
                            latch.countDown();
                            latch.await();
                            sut.write(record2);
                        }catch(Exception e){
                            throw new RuntimeException(e);
                        }finally{
                            doneLatch.countDown();
                        }
                    });
                    
                    doneLatch.await();
                }
                
                
                //verify
                try(NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(new ByteArrayInputStream(out.toByteArray()))
                        .build();
                        ){
                    assertEquals(record1, datastore.get(record1.getId()));
                    assertEquals(record2, datastore.get(record2.getId()));
                }
                
                
    	}
	}
}
