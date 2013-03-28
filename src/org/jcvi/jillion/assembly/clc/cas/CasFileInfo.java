/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
