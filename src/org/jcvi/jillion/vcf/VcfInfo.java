package org.jcvi.jillion.vcf;

import java.util.Map;

import org.jcvi.jillion.vcf.VcfVisitor.InfoNumberTypeAndValue;
import org.jcvi.jillion.vcf.VcfVisitor.InfoType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VcfInfo {

	private String id;
	private String description;
	private InfoNumberTypeAndValue numberTypeAndValue;
	private InfoType type;
	@Singular
	private Map<String, String> parameters;
	
}
