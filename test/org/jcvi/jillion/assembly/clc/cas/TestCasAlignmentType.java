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
 * Created on Jan 20, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.assembly.clc.cas.CasAlignmentType;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCasAlignmentType {

    @Test
    public void valueOf(){
        assertSame(CasAlignmentType.LOCAL, CasAlignmentType.valueOf((byte)0));
        assertSame(CasAlignmentType.SEMI_LOCAL, CasAlignmentType.valueOf((byte)1));
        assertSame(CasAlignmentType.REVERSE_SEMI_LOCAL, CasAlignmentType.valueOf((byte)2));
        assertSame(CasAlignmentType.GLOBAL, CasAlignmentType.valueOf((byte)3));
    }
}
