package org.jcvi.jillion.vcf;

import java.util.Objects;
import java.util.Optional;

public class VcfNumber{
	private final VcfNumberType type;
	private final Integer value;
	
	public final static VcfNumber DOT = new VcfNumber(VcfNumberType.DOT, null);
	
	public final static VcfNumber G = new VcfNumber(VcfNumberType.G, null);
	
	public final static VcfNumber A = new VcfNumber(VcfNumberType.A, null);
	
	public final static VcfNumber R = new VcfNumber(VcfNumberType.R, null);
	
	private final static VcfNumber[] CACHE = new VcfNumber[100];
	
	static {
		for(int i=0; i< 100; i++) {
			CACHE[i] = new VcfNumber(VcfNumberType.NUMBER, Integer.valueOf(i));
		}
	}
	
	public static VcfNumber parse(String number ) {
		if(number ==null) {
			return DOT;
		}
		
		if(".".equals(number)) {
			return DOT;
		}else if("G".equals(number)) {
			return G;
		}else if("A".equals(number)) {
			return A;
		}else if("R".equals(number)) {
			return R;
		}
		
		return valueOf(Integer.parseInt(number));
	}
	
	public static VcfNumber valueOf(int number) {
		if(number >=0 && number < CACHE.length) {
			return CACHE[number];
		}
		return new VcfNumber(VcfNumberType.NUMBER, number);
	}
	public VcfNumber(VcfNumberType type, Integer value) {
		this.type = type;
		this.value = value;
	}

	public VcfNumberType getType() {
		return type;
	}

	public Integer getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VcfNumber other = (VcfNumber) obj;
		return type == other.type && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "InfoNumberTypeAndValue [type=" + type + ", value=" + value + "]";
	}
	
	public String toEncodedString() {
		if(type ==VcfNumberType.NUMBER) {
			return Integer.toString(value);
		}
		if(type == VcfNumberType.DOT) {
			return ".";
		}
		return type.name();
	}
	
	public Optional<VcfNumber> merge(VcfNumber other) {
		Objects.requireNonNull(other);
		
		if(type == other.type) {
			return Optional.of(mergeSameType(other));
		}
		//type is different... is that OK?
		return Optional.empty();
	}

	private VcfNumber mergeSameType(VcfNumber other) {
		if(type!=VcfNumberType.NUMBER) {
			return this;
		}
		//both are numbers now need to check which number it is
		if(value.intValue() == other.getValue().intValue()) {
			//same value
			return this;
		}
		//if we're here it's a different value
		return VcfNumber.DOT;
	}
	
	
}