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
package org.jcvi.jillion.experimental.trace.archive2;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.experimental.trace.archive2.TraceArchiveInfo;
import org.jcvi.jillion.experimental.trace.archive2.TraceArchiveRecord;
import org.jcvi.jillion.experimental.trace.archive2.TraceInfoField;
import org.jcvi.jillion.experimental.trace.archive2.XmlTraceArchiveInfoFactory;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestXmlTraceInfoFactory {

	private final TraceArchiveInfo sut;
	private final File rootDir;
	public TestXmlTraceInfoFactory() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestXmlTraceInfoFactory.class);
		sut = XmlTraceArchiveInfoFactory.create(resources.getFile("files/exampleTraceArchive/TRACEINFO.xml"));
		rootDir = resources.getFile("files/exampleTraceArchive");
	}
	
	@Test
	public void numRecords(){
		assertEquals(4,sut.getRecordList().size());
	}
	
	@Test
	public void fastaRecordsExist(){
		for(TraceArchiveRecord record : sut.getRecordList()){
			assertTrue("missing seq fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.BASE_FILE))
						.exists());
			assertTrue("missing qual fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.QUAL_FILE))
						.exists());
			assertTrue("missing pos fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.PEAK_FILE))
						.exists());
			assertTrue("missing pos fasta",
					new File(rootDir,record.getAttribute(TraceInfoField.PEAK_FILE))
						.exists());
		}
	}
}
