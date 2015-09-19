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
/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import java.math.BigInteger;
import java.util.List;
/**
 * {@code CasFileInfo} contains all the information
 * known by a .cas file about a group of similar files.
 * @author dkatzel
 *
 *
 */
public interface CasFileInfo {
    /**
     * Total number of sequences contained in the entire group
     * of files.
     * @return the number of sequences; always {@code >=0}.
     */
    long getNumberOfSequences();
    /**
     * Get the total number of residues in the entire group
     * of files.
     * @return a {@link BigInteger} of all total count of residues;
     *  never null and always {@code >=0}.
     */
    BigInteger getNumberOfResidues();
    /**
     * Get the list of File paths for all the files in this FileInfo object.
     * These paths may be absolute file paths or relative file paths or a mix of
     * both.
     * @return a List of file paths; never null.
     */
    List<String> getFileNames();
}
