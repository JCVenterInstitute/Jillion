/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.header;

public interface SamProgram {

    /**
     * Get the unique ID. The value of ID is used in the
     * {@link org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys#PROGRAM}
     * tag.
     * 
     * @return a String; will never be null.
     */
    String getId();

    /**
     * Get the program name.
     * @return the name of this program as a String;
     * may be {@code null} if this information is not provided.
     */
    String getName();

    /**
     * Version of the program.
     * @return the version as a String;
     * may be {@code null} if not provided.
     */
    String getVersion();

    /**
     * Description of the program.
     * @return the description of what this program
     * does as a String;
     * may be {@code null} if not provided.
     */
    String getDescription();

    /**
     * Get the Commandline invocation of the program.
     * @return the commandline used to invoke this program
     * does as a String;
     * may be {@code null} if not provided.
     */
    String getCommandLine();

    /**
     * Get the Id of the previous  {@link SamProgram}
     * that operated on this SAM file.
     * @return an Id String or
     * {@code null} if this is the last
     * (or only) program in the chain.
     */
    String getPreviousProgramId();

}
