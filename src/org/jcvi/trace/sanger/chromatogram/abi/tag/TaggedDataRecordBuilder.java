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
package org.jcvi.trace.sanger.chromatogram.abi.tag;

import org.jcvi.trace.sanger.chromatogram.abi.tag.rate.DefaultScanRateTaggedDataType;


public class TaggedDataRecordBuilder implements org.jcvi.Builder<TaggedDataRecord>{
	private final TaggedDataName name;
	private final long number;
	private TaggedDataType dataType;
	private int elementLength;
	private long numberOfElements;
	private long recordLength;
	private long dataRecord;
	private long crypticValue;
	
	
	
	public TaggedDataRecordBuilder(TaggedDataName name, long number) {
		if(name ==null){
			throw new NullPointerException("name can not be null");
		}
		if(number<0){
			throw new IllegalArgumentException("tag number must be >=0");
		}
		this.name = name;
		this.number = number;
	}


	public TaggedDataRecordBuilder setDataType(TaggedDataType dataType, int elementLength){
		if(dataType ==null){
			throw new NullPointerException("dataType can not be null");
		}
		if(elementLength<1){
			throw new IllegalArgumentException("elementLength must be >0");
		}
		this.dataType= dataType;
		this.elementLength = elementLength;
		return this;
	}
	public TaggedDataRecordBuilder setRecordLength(long recordLength){
		if(recordLength<1){
			throw new IllegalArgumentException("recordLength must be >0");
		}
		this.recordLength = recordLength;
		return this;
	}
	public TaggedDataRecordBuilder setNumberOfElements(long numberOfElements){
		if(numberOfElements<1){
			throw new IllegalArgumentException("numberOfElements must be >0");
		}
		this.numberOfElements = numberOfElements;
		return this;
	}
	public TaggedDataRecordBuilder setDataRecord(long dataRecord){			
		this.dataRecord = dataRecord;
		return this;
	}
	public TaggedDataRecordBuilder setCrypticValue(long crypticValue){			
		this.crypticValue = crypticValue;
		return this;
	}
	@Override
	public TaggedDataRecord build() {
		if(numberOfElements * elementLength != recordLength){
			throw new IllegalStateException(
					String.format("invalid record length: expected(%d) but was %d",
							recordLength,
							numberOfElements * elementLength
							));
		}
		
		switch(dataType){
			case FLOAT:
					return new DefaultFloatTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
			case PASCAL_STRING:
					return new DefaultPascalStringTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
			case TIME:
					return new DefaultTimeTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
			case DATE:
					return new DefaultDateTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
			case INTEGER:
				if(elementLength ==2){
					return new DefaultShortArrayTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
				}
				return new DefaultIntegerArrayTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
				
			case USER_DEFINED:
			    if(name == TaggedDataName.Rate){
			        return new DefaultScanRateTaggedDataType(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
		        }
			    return new DefaultUserDefinedTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);

			default:
			  //special case for known null-terminated strings
		        if(name == TaggedDataName.APrN ||name == TaggedDataName.BASECALLS || 
		                name == TaggedDataName.CT_ID || name == TaggedDataName.CT_NAME || name == TaggedDataName.CT_OWNER ||
		                name == TaggedDataName.FILTER_WHEEL_ORDER|| name == TaggedDataName.INSTRUMENT_INFORMATION || name == TaggedDataName.PLATE_TYPE
		                || name == TaggedDataName.RESULTS_GROUP_NAME || name ==TaggedDataName.RMdN || name == TaggedDataName.RMdX
		                || name == TaggedDataName.RPrN || name == TaggedDataName.JTC_RUN_NAME
		                || name == TaggedDataName.JTC_PROTOCOL_VERSION || name == TaggedDataName.MODEL){
		            return new DefaultAsciiTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
		             
		        }
				return new DefaultTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
		}
	}


}
