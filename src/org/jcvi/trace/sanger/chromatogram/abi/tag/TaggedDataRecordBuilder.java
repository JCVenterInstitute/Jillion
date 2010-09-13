package org.jcvi.trace.sanger.chromatogram.abi.tag;


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
			case CHAR :
					return new DefaultAsciiTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
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
				
			
			default:
				return new DefaultTaggedDataRecord(name, number, dataType, elementLength, numberOfElements, recordLength, dataRecord, crypticValue);
		}
	}


}
