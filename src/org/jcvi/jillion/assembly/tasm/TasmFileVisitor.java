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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tasm;

import org.jcvi.jillion.assembly.ctg.ContigFileVisitor;

/**
 * @author dkatzel
 *
 *
 */
public interface TasmFileVisitor extends ContigFileVisitor{

    void visitContigAttribute(String key, String value);
    void visitReadAttribute(String key, String value);
    
    void visitBeginContigBlock();
    void visitEndContigBlock();
    
    void visitBeginReadBlock();
    void visitEndReadBlock();
}
