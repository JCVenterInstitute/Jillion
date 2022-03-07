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
public class VcfContigInfo {

	private String id;
	private Long length;
	@Singular
	private Map<String, String> parameters;
	
}
