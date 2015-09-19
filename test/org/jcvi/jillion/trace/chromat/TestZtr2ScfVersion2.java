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
package org.jcvi.jillion.trace.chromat;

import java.io.OutputStream;

import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramWriterBuilder;

public class TestZtr2ScfVersion2 extends AbstractTestConvertZtr2Scf{
	@Override
	protected  ChromatogramWriter createScfWriter(OutputStream out) {
		ChromatogramWriter writer = new ScfChromatogramWriterBuilder(out)
        								.useVersion2Encoding()
        								.build();
		return writer;
	}

}
