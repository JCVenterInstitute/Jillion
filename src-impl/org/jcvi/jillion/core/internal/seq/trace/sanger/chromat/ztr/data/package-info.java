/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * The data package contains all the classes needed to decode the different
 * data formats in the ZTR specification.  
 * <p/>
 * The actual chromatogram data in a ZTR file
 * is compressed and/or encoded.  There are many different methods
 * and it is common for different Data encodings to be chained together
 * to make the data even more compact.
 * 
 *@author dkatzel
 *@see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 */
package org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.ztr.data;
