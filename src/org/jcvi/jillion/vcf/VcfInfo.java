package org.jcvi.jillion.vcf;

import java.util.Map;

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
	private VcfNumber numberTypeAndValue;
	private VcfValueType type;
	@Singular
	private Map<String, String> parameters;
	
}
