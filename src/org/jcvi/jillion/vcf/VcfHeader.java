package org.jcvi.jillion.vcf;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class VcfHeader {
	/*
	 * Following BCF encoding the header is every line until the #CHROM
	 */
	
	
	private String version;
	//TODO can normal properties have duplicate keys?
	@Singular
	private Map<String, String> properties;
	@Singular
	private List<VcfFilter> filters;
	@Singular
	private List<VcfFormat> formats;
	@Singular
	private List<VcfInfo> infos;
	@Singular
	private List<VcfContigInfo> contigInfos;
	
	private List<String> extraColumns;
}
