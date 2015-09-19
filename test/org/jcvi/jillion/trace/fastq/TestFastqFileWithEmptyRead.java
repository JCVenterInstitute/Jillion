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
package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestFastqFileWithEmptyRead {

	ResourceHelper helper = new ResourceHelper(TestFastqFileWithEmptyRead.class);
	
	private final FastqDataStore sut;
	@Parameters
    public static Collection<?> data(){
		List<Object[]> data = new ArrayList<Object[]>();
		data.add(new Object[]{DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED});
		data.add(new Object[]{DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY});
		data.add(new Object[]{DataStoreProviderHint.ITERATION_ONLY});
		return data;
	}
	
	public TestFastqFileWithEmptyRead(DataStoreProviderHint hint) throws IOException{
		sut = new FastqFileDataStoreBuilder(helper.getFile("files/sangerWithEmptyRead.fastq"))
							.hint(hint)
							.build();
	}
	
	@Test
	public void numberOfRecords() throws DataStoreException{
		assertEquals(3, sut.getNumberOfRecords());
	}
	@Test
	public void emptyRead() throws DataStoreException{
		FastqRecord empty = sut.get("SOLEXA1_0007:1:9:610:1983#GATCAG/2");
		assertEquals(0,empty.getNucleotideSequence().getLength());
		assertEquals(0,empty.getQualitySequence().getLength());		
	}
	@Test
	public void readIsInSangerFormat() throws DataStoreException{
        FastqRecord actual = sut.get("SOLEXA1_0007:2:13:163:254#GATCAG/2");
        assertEquals("CGTAGTACGATATACGCGCGTGTACTGCTACGTCTCACTTTCGCAAGATTGCTCAGCTCATTGATGCTCAATGCTGGGCCATATCTCTTTTCTTTTTTTC",
                actual.getNucleotideSequence().toString());
        assertEquals(FastqQualityCodec.SANGER.decode("HHHHGHHEHHHHHE=HAHCEGEGHAG>CHH>EG5@>5*ECE+>AEEECGG72B&A*)569B+03B72>5.A>+*A>E+7A@G<CAD?@############"),
                actual.getQualitySequence());
	}
}
