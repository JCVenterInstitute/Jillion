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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class TestTasmWriterBuilderFile extends AbstractTestTasmWriterBuilder{

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();
	
	private File outputTasm;
	
	@Override
	protected TasmWriter createTasmWriterFor(File inputTasm){
		try {
			outputTasm =temp.newFile();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return new TasmFileWriterBuilder(outputTasm).build();
	}

	@Override
	protected byte[] getWrittenBytes() {
		
		try {
			return IOUtil.toByteArray(outputTasm);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
