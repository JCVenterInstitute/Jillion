package org.jcvi.jillion.vcf;

import java.util.List;
import java.util.Map;

public interface VcfVisitor {

	interface VcfVisitorCallback{
		
		boolean canCreateMemento();
		
		void haltParsing();
		
		VcfMemento createMemento();
	}
	
	interface VcfMemento{
		
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
			VcfValueType type, VcfNumber numberTypeAndValue,
			String description,
			Map<String, String> parameters);
	
	void visitFormat(VcfVisitorCallback callback, String id, VcfValueType infoType,
			VcfNumber numberTypeAndValue,
			String description,
			Map<String, String> parameters);
	
	void visitContigInfo(VcfVisitorCallback callback, String contigId, Long length, Map<String, String> parameters);
	
	void visitHeader(VcfVisitorCallback callback, List<String> extraColumns);
	
	void visitData(VcfVisitorCallback callback, String chromId, int position, String id, String refBase, String altBase,
			int quality, String filter, String info, String format, List<String> extraFields);
}
