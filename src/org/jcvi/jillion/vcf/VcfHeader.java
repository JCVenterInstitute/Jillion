package org.jcvi.jillion.vcf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
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
	private Map<@NonNull String, @NonNull String> properties;
	@Singular
	private List<@NonNull VcfFilter> filters;
	@Singular
	private List<@NonNull VcfFormat> formats;
	@Singular
	private List<@NonNull VcfInfo> infos;
	@Singular
	private List<@NonNull VcfContigInfo> contigInfos;
	@Singular
	private List<@NonNull String> extraColumns;
	
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
	//this is to make maven javadoc work... lombok will populate class for us
	public static class VcfHeaderBuilder{}
}
