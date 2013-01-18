/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
package org.jcvi.jillion.internal.trace.sanger.chromat.abi.tag;


public interface TaggedDataRecord<T,D> {
	
	TaggedDataName getTagName();
	long getTagNumber();
	TaggedDataType getDataType();
	int getElementLength();
	long getNumberOfElements();
	long getRecordLength();
	long getDataRecord();
	long getCrypticValue();
	
	Class<D> getParsedDataType();
	
	Class<T> getType();
	
	D parseDataRecordFrom(byte[] ab1DataBlock);
}