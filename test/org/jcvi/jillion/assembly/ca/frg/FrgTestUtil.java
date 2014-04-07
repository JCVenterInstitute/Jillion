/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.assembly.ca.frg;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;

public final class FrgTestUtil {

	private FrgTestUtil(){
		//can not instantiate
	}
	
	public static final int ENCODING_ORIGIN = 0x30;
    public static  QualitySequence decodeQualitySequence(String encodedValues){
    	QualitySequenceBuilder builder = new QualitySequenceBuilder(encodedValues.length());
    	for(int i=0; i<encodedValues.length(); i++){
    		builder.append(encodedValues.charAt(i) - ENCODING_ORIGIN);
    	}
    	
    	return builder.build();
    }
}
