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
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;

public interface AceContigReadVisitor {

	/**
     * Visit quality line of currently visited read.
     * @param qualLeft left position(1-based)  of clear range.
     * @param qualRight right position(1-based) of clear range.
     * @param alignLeft left alignment(1-based) position. 
     * @param alignRight right alignment(1-based) position.
     */
    void visitQualityLine(int qualLeft, int qualRight, int alignLeft, int alignRight);
    /**
     * Visit Trace Description line of currently visited read.
     * @param traceName name of trace file corresponding
     * to currently visited read.
     * @param phdName name of phd file.
     * @param date date phd file created.
     */
    void visitTraceDescriptionLine(String traceName, String phdName, Date date);
    /**
     * Visit a line of basecalls of currently visited read. A read 
     * probably has several lines of basecalls.  The characters in the bases
     * could be mixed case.  Consed differentiates high quality basecalls
     * vs low quality basecalls by using upper and lowercase letters respectively.
     * 
     * @param mixedCaseBasecalls (some of) the basecalls of the currently visited read
     * or consensus which might have both upper and lower case letters to denote
     * high vs low quality.
     * 
     */
    void visitBasesLine(String mixedCaseBasecalls);
    

    void visitEnd();
    
    void halted();
}
