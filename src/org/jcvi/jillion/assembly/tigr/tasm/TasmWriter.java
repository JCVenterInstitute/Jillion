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

import java.io.Closeable;
import java.io.IOException;
/**
 * {@code TasmWriter} writes out TIGR Assembler
 * formated files (.tasm).  This assembly format 
 * probably does not have much use outside of 
 * JCVI since the format is specially tailored to the 
 * legacy TIGR Project Database.
 * @author dkatzel
 *
 */
public interface TasmWriter extends Closeable{
	/**
	 * Write the given {@link TasmContig}.
	 * 
	 * @param contig the contig to write;
	 * can not be null.
	 * @throws IOException if there is a problem
	 * writing out this contig.
	 * @throws NullPointerException if contig is null.
	 */
	void write(TasmContig contig) throws IOException;
}
