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
/**
 * This package can decode Applied Biosystems AB1 formatted
 * trace files into {@link org.jcvi.jillion.trace.sanger.chromat.Chromatogram} objects.
 * Most of the code to parse this format 
 * was based on information from the Clark Tibbetts paper
 * "Raw Data File Formats and the Digital and Analog Raw Data Streams
 * of the ABI PRISM 377 DNA Sequencer" which paritally reverse engineered
 * the Applied Biosystems 377 DNA Sequencer AB1 file format.
 * @author dkatzel
 * @see <a href = "http://www-2.cs.cmu.edu/afs/cs/project/genome/WWW/Papers/clark.html">
  Raw Data File Formats and the Digital and Analog Raw Data Streams
  of the ABI PRISM 377 DNA Sequencer</a>
 */
package org.jcvi.jillion.trace.chromat.abi;
