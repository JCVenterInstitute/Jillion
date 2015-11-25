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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;
import org.junit.Before;
import org.junit.Test;
public class TestFastqParser extends EasyMockSupport{

	ResourceHelper helper = new ResourceHelper(TestFastqParser.class);
	File fastqFile;
	
	
	
	
	@Before
	public void setup() throws IOException{
		fastqFile = helper.getFile("files/sanger.fastq");
	}
	
	@Test
	public void visitFile() throws IOException{
		FastqParser sut = createSut(fastqFile);
		
		assertTrue(sut.canParse());
		FastqVisitor visitor = createMockFullFileVisitor();
		replayAll();
		sut.parse(visitor);
		verifyAll();
	}
	@Test
	public void useMementoToSkipFirstRecord() throws IOException{
		FastqParser sut = createSut(fastqFile);
		
		List<FastqVisitorMemento> mementos = new ArrayList<>();
		assertTrue(sut.canParse());
		FastqVisitor visitor = createMockFullFileVisitor(mementos);
		replayAll();
		sut.parse(visitor);

		
		FastqVisitor secondVisitor = createMockSecondVisitorOnly();
		replay(secondVisitor);
		sut.parse(secondVisitor, mementos.get(1));
		verify(secondVisitor);
	}
	
	private FastqVisitor createMockSecondVisitorOnly() {
		FastqVisitor visitor = createMock(FastqVisitor.class);
		
		
		
		FastqRecordVisitor secondRecordVisitor = createSecondRecordVisitor();
		
		expect(visitor.visitDefline(isA(FastqVisitorCallback.class), eq("SOLEXA1_0007:2:13:163:254#GATCAG/2"), isNull()))
		.andReturn(secondRecordVisitor);
		
		visitor.visitEnd();
		
		return visitor;
	}

	protected FastqParser createSut(File fastqFile) throws IOException{
		return FastqFileParser.create(InputStreamSupplier.forFile(fastqFile), true,true, true);
	}
	
	
	
	private FastqVisitor createMockFullFileVisitor(List<FastqVisitorMemento> mementos){
		FastqVisitor visitor = createMock(FastqVisitor.class);
		
		FastqRecordVisitor firstRecordVisitor = firstRecordVisitor();
		
		expect(visitor.visitDefline(isA(FastqVisitorCallback.class), eq("SOLEXA1_0007:1:13:1658:1080#GGCTAC/2"), isNull()))
						.andAnswer(() ->{
							FastqVisitorCallback callback = (FastqVisitorCallback)getCurrentArguments()[0];
							mementos.add(callback.createMemento());
							return firstRecordVisitor;
						});
		
		
		FastqRecordVisitor secondRecordVisitor = createSecondRecordVisitor();
		
		expect(visitor.visitDefline(isA(FastqVisitorCallback.class), eq("SOLEXA1_0007:2:13:163:254#GATCAG/2"), isNull()))
								.andAnswer(() ->{
									FastqVisitorCallback callback = (FastqVisitorCallback)getCurrentArguments()[0];
									mementos.add(callback.createMemento());
									return secondRecordVisitor;
								});
		
		visitor.visitEnd();
		
		return visitor;
	}
	
	private FastqVisitor createMockFullFileVisitor(){
		FastqVisitor visitor = createMock(FastqVisitor.class);
		
		FastqRecordVisitor firstRecordVisitor = firstRecordVisitor();
		
		expect(visitor.visitDefline(isA(FastqVisitorCallback.class), eq("SOLEXA1_0007:1:13:1658:1080#GGCTAC/2"), isNull()))
						.andReturn(firstRecordVisitor);
		
		
		FastqRecordVisitor secondRecordVisitor = createSecondRecordVisitor();
		
		expect(visitor.visitDefline(isA(FastqVisitorCallback.class), eq("SOLEXA1_0007:2:13:163:254#GATCAG/2"), isNull()))
		.andReturn(secondRecordVisitor);
		
		visitor.visitEnd();
		
		return visitor;
	}

	private FastqRecordVisitor createSecondRecordVisitor() {
		FastqRecordVisitor secondRecordVisitor = createMock(FastqRecordVisitor.class);
		
		secondRecordVisitor.visitNucleotides("CGTAGTACGATATACGCGCGTGTACTGCTACGTCTCACTTTCGCAAGATTGCTCAGCTCATTGATGCTCAATGCTGGGCCATATCTCTTTTCTTTTTTTC");
		secondRecordVisitor.visitEncodedQualities("HHHHGHHEHHHHHE=HAHCEGEGHAG>CHH>EG5@>5*ECE+>AEEECGG72B&A*)569B+03B72>5.A>+*A>E+7A@G<CAD?@############");
		secondRecordVisitor.visitEnd();
		return secondRecordVisitor;
	}

	private FastqRecordVisitor firstRecordVisitor() {
		FastqRecordVisitor firstRecordVisitor = createMock(FastqRecordVisitor.class);
		
		firstRecordVisitor.visitNucleotides("CGTAGTACGATATACGCGCGTGTGTACTGCTACGTCTCACTTCTTTTTCCCCACGGGATGTTATTTCCCTTTTAAGCTTCCTGTACAGTTTTGCCGGGCT");
		firstRecordVisitor.visitEncodedQualities("@;7C9;A)565A;4..9;2;45,?@###########################################################################");
		
		firstRecordVisitor.visitEnd();
		return firstRecordVisitor;
	}
	
	
}
