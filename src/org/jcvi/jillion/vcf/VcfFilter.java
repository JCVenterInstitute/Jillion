package org.jcvi.jillion.vcf;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class VcfFilter {
	@NonNull
	private String id;
	@NonNull
	private String description;
	
}
