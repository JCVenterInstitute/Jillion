package org.jcvi.jillion.vcf;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VcfInfo {
	@NonNull
	private String id;
	private String description;
	@NonNull
	private VcfNumber number;
	@NonNull
	private VcfValueType type;
	@Singular
	private Map<@NonNull String, @NonNull String> parameters;
	
	public static VcfInfoBuilder builder() {
		return new VcfInfoBuilder()
				.parameters(new LinkedHashMap<>());
	}
	
}
