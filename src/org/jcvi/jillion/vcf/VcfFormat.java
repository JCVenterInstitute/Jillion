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
public class VcfFormat {

	private String id;
	private VcfValueType type;
	private VcfNumber numberTypeAndValue;
	private String description;
	@Singular
	private Map<String, String> parameters;
}
