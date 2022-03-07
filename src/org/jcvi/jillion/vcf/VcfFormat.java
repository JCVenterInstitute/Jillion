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
public class VcfFormat {

	private String id;
	private InfoType type;
	private InfoNumberTypeAndValue numberTypeAndValue;
	private String description;
	@Singular
	private Map<String, String> parameters;
}
