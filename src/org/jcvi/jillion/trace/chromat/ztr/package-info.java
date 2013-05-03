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
 * ZTR Format can store sanger trace data more efficiently than the SCFv3 format.
 * ZTR format is used by the NCBI Trace Archive.
 * 
 *@author dkatzel
 *@see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *@see <a href="http://bioinformatics.oxfordjournals.org/cgi/content/abstract/18/1/3">
 *Bonfield, J.K. and Staden, R. (2002) ZTR: a new format for DNA sequence trace data.
 *Bioinformatics Vol. 18 no. 1 3-10</a>
 */
package org.jcvi.jillion.trace.chromat.ztr;
