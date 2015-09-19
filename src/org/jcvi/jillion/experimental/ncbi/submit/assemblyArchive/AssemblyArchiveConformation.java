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
package org.jcvi.jillion.experimental.ncbi.submit.assemblyArchive;

/**
 * {@code ContigConformation} denotes whether the contig is linear or circular.
 * @author dkatzel
 */
public enum AssemblyArchiveConformation{
	 /**
     * Contig is linear.
     */
    LINEAR,
    /**
     * Contig is circular.  Contigs that are circular use 
     * the the TIGR convention to
     * have trace coordinates which are negative to indicate they wrap around.
     */
    CIRCULAR
}
