package org.jcvi.jillion.vcf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	
	public static VcfHeaderBuilder builder() {
		//this is a terrible way to do this but is the cleanest
		//way I can think of to set all fields in builder to default to empty
		//instead of nulls.
		//Note: version still null
		return new VcfHeaderBuilder()
					.properties(new LinkedHashMap<>())
					.filters(new ArrayList<>())
					.formats(new ArrayList<>())
					.infos(new ArrayList<>())
					.contigInfos(new ArrayList<>())
					.extraColumns(new ArrayList<>())
					;
					
	}
}
