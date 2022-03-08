package org.jcvi.jillion.vcf;

import java.util.List;
import java.util.Map;

public abstract class AbtractVcfVisitor implements VcfVisitor {

	private VcfHeader.VcfHeaderBuilder headerBuilder = VcfHeader.builder();
	
	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void halted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitMetaInfo(VcfVisitorCallback callback, String key, String value) {
		headerBuilder.property(key, value);

	}

	@Override
	public void visitFilter(VcfVisitorCallback callback, String key, String description) {
		headerBuilder.filter(new VcfFilter(key, description));

	}

	@Override
	public void visitInfo(VcfVisitorCallback callback, String id, VcfValueType type,
			VcfNumber numberTypeAndValue, String description, Map<String, String> parameters) {
		headerBuilder.info(new VcfInfo(id, description, numberTypeAndValue, type, parameters));

	}

	@Override
	public void visitFormat(VcfVisitorCallback callback, String id, VcfValueType infoType,
			VcfNumber numberTypeAndValue, String description, Map<String, String> parameters) {
		headerBuilder.format(new VcfFormat(id, infoType, numberTypeAndValue, description, parameters));

	}

	@Override
	public void visitContigInfo(VcfVisitorCallback callback, String contigId, Long length,Map<String,String> parameters) {
		headerBuilder.contigInfo( new VcfContigInfo(contigId, length, parameters));

	}

	@Override
	public void visitHeader(VcfVisitorCallback callback, List<String> extraColumns) {
		headerBuilder.extraColumns(extraColumns);
		visitHeader(callback, headerBuilder.build());

	}

	protected abstract void visitHeader(VcfVisitorCallback callback, VcfHeader build);

	

}
