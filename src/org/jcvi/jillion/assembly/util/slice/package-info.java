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
/**
 * This package is a Java Representation of assembly slice data.
 * The Slice concept was originally created at The Institute for Genomic
 * Research (TIGR) which has since merged with other affiliated
 * organizations to form JCVI.
 * 
 * A Slice is a one base wide cut of an assembly from zero or more reads. 
 * Each read in the slice contributes a base, quality value, and direction. 
 * Operations 
 * that are difficult to perform within a contig representation may trivial 
 * to perform with a slice representation. 
 * <p>
 * If you consider the traditional contig view of an assembly 
 * to be "horizontal", meaning the assembly is oriented towards 
 * the rows of bases in reads and the consensus, 
 * then the slice view of an assembly is "vertical", 
 * meaning the assembly is oriented towards the tiling 
 * at each assembly position. With this picture in mind it is 
 * easy to understand that the contig view and the slice view 
 * is purely a shift in representation, and there is no change in information.
 *  
 * @author dkatzel
 * @see <a href="http://slicetools.sourceforge.net">Slice Tools</a>
 */
package org.jcvi.jillion.assembly.util.slice;
