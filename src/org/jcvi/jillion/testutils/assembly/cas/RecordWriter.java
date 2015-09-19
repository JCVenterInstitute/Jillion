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
package org.jcvi.jillion.testutils.assembly.cas;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface RecordWriter extends Closeable{
	
	static final PhredQuality DEFAULT_QV  = PhredQuality.valueOf(30);
	
	void write(String id, NucleotideSequence seq) throws IOException;
	boolean canWriteAnotherRecord();
	
	File getFile();
}
