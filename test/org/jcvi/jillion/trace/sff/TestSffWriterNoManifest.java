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
package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class TestSffWriterNoManifest extends AbstractTestSffWriter{

	@Override
	protected String getPathToFile() {
		return "files/5readExample_noIndex_noXML.sff";
	}

	@Override
	protected SffFileDataStore createDataStore(File inputSff) throws IOException {
		return new SffFileDataStoreBuilder(inputSff)
									.build();
	}

	@Override
	protected SffWriter createWriter(File outputFile,
			NucleotideSequence keySequence, NucleotideSequence flowSequence) throws IOException {
		return new SffWriterBuilder(outputFile, keySequence, flowSequence)
					.build();
	}

}
