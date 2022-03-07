package org.jcvi.jillion.vcf;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jcvi.jillion.vcf.VcfVisitor.InfoNumberType;

public interface VcfVisitor {

	interface VcfVisitorCallback{
		
		
		void haltParsing();
	}
	
	enum InfoType{
		Integer, Float, Flag, Character, String
	}
	
	enum InfoNumberType{
		NUMBER,
		/**
		 * The field has one value per alternate allele. The values must be in the same order as listed in the ALT
column (described in section 1.6).
		 */
		A,
		/**
		 * The field has one value for each possible allele, including the reference. The order of the values must be the
reference allele first, then the alternate alleles as listed in the ALT column.
		 */
		R,
		/**
		 * he field has one value for each possible genotype. The values must be in the same order as prescribed in
section 1.6.2 (see Genotype Ordering).
		 */
		G,
		/**
		 * The number of possible values varies, is unknown or unbounded.
		 */
		DOT;
		
		
	}
	
	public static class InfoNumberTypeAndValue{
		private final InfoNumberType type;
		private final Integer value;
		
		public static InfoNumberTypeAndValue DOT = new InfoNumberTypeAndValue(InfoNumberType.DOT, null);
		
		public static InfoNumberTypeAndValue G = new InfoNumberTypeAndValue(InfoNumberType.G, null);
		
		public static InfoNumberTypeAndValue A = new InfoNumberTypeAndValue(InfoNumberType.A, null);
		
		public static InfoNumberTypeAndValue R = new InfoNumberTypeAndValue(InfoNumberType.R, null);
		
		private static InfoNumberTypeAndValue[] CACHE = new InfoNumberTypeAndValue[100];
		
		static {
			for(int i=0; i< 100; i++) {
				CACHE[i] = new InfoNumberTypeAndValue(InfoNumberType.NUMBER, Integer.valueOf(i));
			}
		}
		
		public static InfoNumberTypeAndValue parse(String number ) {
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
		
		public static InfoNumberTypeAndValue valueOf(int number) {
			if(number >=0 && number < CACHE.length) {
				return CACHE[number];
			}
			return new InfoNumberTypeAndValue(InfoNumberType.NUMBER, number);
		}
		public InfoNumberTypeAndValue(InfoNumberType type, Integer value) {
			this.type = type;
			this.value = value;
		}

		public InfoNumberType getType() {
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
			InfoNumberTypeAndValue other = (InfoNumberTypeAndValue) obj;
			return type == other.type && Objects.equals(value, other.value);
		}

		@Override
		public String toString() {
			return "InfoNumberTypeAndValue [type=" + type + ", value=" + value + "]";
		}
		
		public String toEncodedString() {
			if(type ==InfoNumberType.NUMBER) {
				return Integer.toString(value);
			}
			if(type == InfoNumberType.DOT) {
				return ".";
			}
			return type.name();
		}
		
		
	}
	/**
	 * Reached the end of the VCF encoded file.
	 */
	void visitEnd();
	/**
	 * Stopped parsing the VCF file usually because {@link VcfVisitorCallback#haltParsing()}
	 * was called.
	 */
	void halted();
	
	void visitMetaInfo(VcfVisitorCallback callback, String key, String value);
	void visitFilter(VcfVisitorCallback callback, String key, String description);
	
	void visitInfo(VcfVisitorCallback callback, String id,
			InfoType type, InfoNumberTypeAndValue numberTypeAndValue,
			String description,
			Map<String, String> parameters);
	
	void visitFormat(VcfVisitorCallback callback, String id, InfoType infoType,
			InfoNumberTypeAndValue numberTypeAndValue,
			String description,
			Map<String, String> parameters);
	
	void visitContigInfo(VcfVisitorCallback callback, String contigId, Long length, Map<String, String> parameters);
	
	void visitHeader(VcfVisitorCallback callback, List<String> extraColumns);
	
	void visitData(VcfVisitorCallback callback, String chromId, int position, String id, String refBase, String altBase,
			int quality, String filter, String info, String format, List<String> extraFields);
}
