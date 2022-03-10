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
public class VcfContigInfo {

	@NonNull
	private String id;
	private Long length;
	@Singular
	private Map<@NonNull String, @NonNull String> parameters;
	
	public static VcfContigInfoBuilder builder() {
		//this makes an parameters default to an empty map instead of null
		return new VcfContigInfoBuilder()
					.parameters(new LinkedHashMap<>())
					;
	}
	public static class VcfContigInfoBuilder{
		

		public VcfContigInfoBuilder length(Long length) {
			this.length = length;
			return this;
		}
		public VcfContigInfoBuilder length(int length) {
			return length((long)length);
		}
		
	}
}
