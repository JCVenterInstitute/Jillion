package org.jcvi.jillion.assembly.consed.phd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdFileDataStoreBuilder;
import org.jcvi.jillion.assembly.consed.phd.SinglePhdWriter;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestSinglePhdWriter extends AbstractTestPhd {

	@Test
	public void writeOnePhdWithRealPositions() throws IOException, DataStoreException{
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		SinglePhdWriter sut = new SinglePhdWriter(out);
		
		Phd expected = new PhdBuilder(expectedId, 
				new NucleotideSequenceBuilder(expectedBasecalls).build(), expectedQualities)
				.peaks(expectedPositions)
				.comments(expectedProperties)
				.build();
				
		sut.write(expected);
		sut.close();
		
		Phd actual = new PhdFileDataStoreBuilder(new ByteArrayInputStream(out.toByteArray()))
								.build()
								.get(expectedId);
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void writeOnePhdWithFakePositions() throws IOException, DataStoreException{
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		SinglePhdWriter sut = new SinglePhdWriter(out);
		
		Phd expected = new PhdBuilder(expectedId, 
				new NucleotideSequenceBuilder(expectedBasecalls).build(), expectedQualities)
				.comments(expectedProperties)
				.build();
				
		sut.write(expected);
		sut.close();
		
		Phd actual = new PhdFileDataStoreBuilder(new ByteArrayInputStream(out.toByteArray()))
								.build()
								.get(expectedId);
		
		assertEquals(expected, actual);		
	}
	
	@Test
	public void writingMoreThanOnePhdShouldThrowIOException() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		SinglePhdWriter sut = new SinglePhdWriter(out);
		try{
			Phd expected = new PhdBuilder(expectedId, 
					new NucleotideSequenceBuilder(expectedBasecalls).build(), expectedQualities)
					.comments(expectedProperties)
					.build();
					
			sut.write(expected);
			try{
				sut.write(expected);
				fail("should not be allowed to write 2 phds");
			}catch(IOException ignore){
				//ignore
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
	}
}
