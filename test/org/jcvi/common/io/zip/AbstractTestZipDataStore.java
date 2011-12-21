package org.jcvi.common.io.zip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Abstract class of common tests of {@link ZipDataStore}.  Concrete
 * subclasses just need to implement {@link #createZipDataStore(File)}
 * to return the implementation to be tested.
 * @author dkatzel
 *
 */
public abstract class AbstractTestZipDataStore {

	private final ResourceFileServer resources = new ResourceFileServer(AbstractTestZipDataStore.class);

	private ZipDataStore sut;
	
	@Before
	public void setup() throws IOException{
		sut = createZipDataStore(resources.getFile("example.zip"));
	}
	
	@After
	public void closeWhenComplete() throws IOException{
		sut.close();
	}
	/**
	 * Create the {@link ZipDataStore} instance to test.
	 * @param file a zip file.
	 * @return
	 */
	protected abstract ZipDataStore createZipDataStore(File file) throws IOException;
	
	@Test
	public void size() throws DataStoreException{
		assertEquals(2, sut.size());
	}
	
	@Test
	public void ids() throws DataStoreException{
		Set<String> expectedIds = new HashSet<String>();
		expectedIds.add("file1.txt");
		expectedIds.add("file2.txt");
		
		Iterator<String> idIter = sut.getIds();
		assertTrue(idIter.hasNext());
		while(idIter.hasNext()){
			assertTrue(expectedIds.contains(idIter.next()));
		}
		assertFalse(idIter.hasNext());
	}
	
	@Test
	public void contains() throws DataStoreException{
		assertTrue(sut.contains("file1.txt"));
		assertTrue(sut.contains("file2.txt"));
	}
	@Test
	public void notClosed() throws DataStoreException{
		assertFalse(sut.isClosed());
	}
	@Test
	public void doesNotContain() throws DataStoreException{
		assertFalse(sut.contains("somethingCompletelyDifferent"));
	}
	@Test
	public void get() throws DataStoreException, IOException{
		byte[] expected = getBytesForFile1();		
		InputStream in = sut.get("file1.txt");
		byte[] actual = new byte[expected.length];
		int bytesRead =in.read(actual);
		in.close();
		assertEquals(expected.length, bytesRead);
		assertArrayEquals(expected, actual);
	}
	private byte[] getBytesForFile1() {
		return "This is file 1\n\n".getBytes(IOUtil.UTF_8);
	}
	private byte[] getBytesForFile2() {
		return "And here is file 2\nblah blah\n".getBytes(IOUtil.UTF_8);
	}
	
	@Test
	public void close() throws IOException, DataStoreException{
		sut.close();
		assertTrue(sut.isClosed());
		
		try{
			sut.contains("something");
			fail("contains() should throw Exception if already closed");
		}catch(IllegalStateException expected){
			
		}
		
		try{
			sut.get("something");
			fail("get() should throw Exception if already closed");
		}catch(IllegalStateException expected){
			
		}
	}
	
	@Test
	public void iterator() throws IOException{
		Iterator<InputStream> iter = sut.iterator();
		assertTrue(iter.hasNext());
		boolean hasFile1=false;
		boolean hasFile2=false;
		boolean hasOther=false;
		String file1Text = new String(getBytesForFile1(), IOUtil.UTF_8);
		String file2Text = new String(getBytesForFile2(), IOUtil.UTF_8);
		while(iter.hasNext()){
			InputStream next = iter.next();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[30];
			int bytesRead;
			while((bytesRead=next.read(buf))>0){
				out.write(buf, 0, bytesRead);
			}
			String actualString = new String(out.toByteArray(), IOUtil.UTF_8);
			if(file1Text.equals(actualString)){
				hasFile1=true;
			}else if(file2Text.equals(actualString)){
				hasFile2=true;
			}else{
				hasOther=true;
			}
		}
		assertFalse(iter.hasNext());
		assertTrue(hasFile1);
		assertTrue(hasFile2);
		assertFalse(hasOther);
		
	}
	
}
