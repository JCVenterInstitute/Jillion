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
public class VcfFormat {
	@NonNull
	private String id;
	@NonNull
	private VcfValueType type;
	@NonNull
	private VcfNumber number;
	private String description;
	@Singular
	private Map<@NonNull String, @NonNull String> parameters;
	
	public static VcfFormatBuilder builder() {
		//this makes parameters default to an empty list
		return new VcfFormatBuilder()
				.parameters(new LinkedHashMap<>())
				;
	}
}
