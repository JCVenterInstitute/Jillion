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
package org.jcvi.jillion_experimental.align.blast;

import java.util.List;

public interface BlastHit {

	/**
	 * Get the Id of the Query sequence in this Hsp.
	 * @return a String; will never be null.
	 */
	String getQueryId();
	/**
	 * Get the Id of the Subject sequence in this Hsp.
	 * @return a String; will never be null.
	 */
    String getSubjectId();
    /**
	 * Get the defline of the Subject in this Hsp.
	 * @return a String; may be null if not specified.
	 */
    String getSubjectDefinition();
    
    Integer getQueryLength();
    
    Integer getSubjectLength();
    
    List<Hsp> getHsps();
    
    String getBlastDbName();
    
    String getBlastProgramName();
}
