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
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;
/**
 * {@code FileVisitor} is an base interface
 * which uses the "push approach" to walk
 * over complicated File structures. 
 * This is similar to the Event and Visitor Design 
 * Patterns where each method follows
 * the format visitXXX.  It is up
 * to the implementor of this interface
 * to determine what to do during
 * each visitXXX call.
 * 
 * @author dkatzel
 *
 *
 */
public interface FileVisitor {
    /**
     * Visiting a new File.
     */
    void visitFile();
   
    /**
     * The File has been completely visited.
     */
    void visitEndOfFile();
}
