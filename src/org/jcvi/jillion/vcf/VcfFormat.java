package org.jcvi.jillion.vcf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VcfFormat {
	
	
	/*
	 * AD R Integer Read depth for each allele
ADF R Integer Read depth for each allele on the forward strand
ADR R Integer Read depth for each allele on the reverse strand
DP 1 Integer Read depth
EC A Integer Expected alternate allele counts
FT 1 String Filter indicating if this genotype was “called”
GL G Float Genotype likelihoods
GP G Float Genotype posterior probabilities
GQ 1 Integer Conditional genotype quality
GT 1 String Genotype
HQ 2 Integer Haplotype quality
MQ 1 Integer RMS mapping quality
PL G Integer Phred-scaled genotype likelihoods rounded to the closest integer
	 */
	
	/**
	 * Integer Read depth for each allele.
	 */
	public static final VcfFormat RESERVED_AD = VcfFormat.builder()
															.id("AD")
															.description("Read depth for each allele")
															.number(VcfNumber.R)
															.type(VcfValueType.Integer)
															.build();
	/**
	 * Integer Read depth for each allele on the forward strand.
	 */
	public static final VcfFormat RESERVED_ADF = VcfFormat.builder()
															.id("ADF")
															.description("Read depth for each allele on the forward strand")
															.number(VcfNumber.R)
															.type(VcfValueType.Integer)
															.build();
	/**
	 * Integer Read depth for each allele on the reverse strand.
	 */
	public static final VcfFormat RESERVED_ADR = VcfFormat.builder()
															.id("ADR")
															.description("Read depth for each allele on the reverse strand")
															.number(VcfNumber.R)
															.type(VcfValueType.Integer)
															.build();
	/**
	 * Integer Read depth.
	 */
	public static final VcfFormat RESERVED_DP = VcfFormat.builder()
															.id("DP")
															.description("Read depth")
															.number(1)
															.type(VcfValueType.Integer)
															.build();
	
	/**
	 * Integer Expected alternate allele counts.
	 */
	public static final VcfFormat RESERVED_EC = VcfFormat.builder()
															.id("EC")
															.description("Integer Expected alternate allele counts")
															.number(VcfNumber.A)
															.type(VcfValueType.Integer)
															.build();
	/**
	 * String Filter indicating if this genotype was “called”.
	 */
	public static final VcfFormat RESERVED_FT = VcfFormat.builder()
															.id("FT")
															.description("Filter indicating if this genotype was \"called\"")
															.number(1)
															.type(VcfValueType.String)
															.build();
	
	/**
	 * Float Genotype likelihoods.
	 */
	public static final VcfFormat RESERVED_GL = VcfFormat.builder()
															.id("GL")
															.description("Genotype likelihoods")
															.number(VcfNumber.G)
															.type(VcfValueType.Float)
															.build();
	
	/**
	 * Float Genotype posterior probabilities.
	 */
	public static final VcfFormat RESERVED_GP = VcfFormat.builder()
															.id("GP")
															.description("Genotype posterior probabilities")
															.number(VcfNumber.G)
															.type(VcfValueType.Float)
															.build();
	/**
	 * Integer Conditional genotype quality.
	 */
	public static final VcfFormat RESERVED_GQ = VcfFormat.builder()
															.id("GQ")
															.description("Conditional genotype quality")
															.number(1)
															.type(VcfValueType.Integer)
															.build();
	/**
	 * String Genotype.
	 */
	public static final VcfFormat RESERVED_GT = VcfFormat.builder()
															.id("GT")
															.description("Genotype")
															.number(1)
															.type(VcfValueType.String)
															.build();
	
	/**
	 * Integer Haplotype quality.
	 */
	public static final VcfFormat RESERVED_HQ = VcfFormat.builder()
															.id("HQ")
															.description("Haplotype quality")
															.number(2)
															.type(VcfValueType.Integer)
															.build();
	
	/**
	 * Integer RMS mapping quality.
	 */
	public static final VcfFormat RESERVED_MQ = VcfFormat.builder()
															.id("MQ")
															.description("RMS mapping quality")
															.number(1)
															.type(VcfValueType.Integer)
															.build();
	
	/**
	 * Integer Phred-scaled genotype likelihoods rounded to the closest integer.
	 */
	public static final VcfFormat RESERVED_PL = VcfFormat.builder()
															.id("PL")
															.description("Phred-scaled genotype likelihoods rounded to the closest integer")
															.number(VcfNumber.G)
															.type(VcfValueType.Integer)
															.build();
	
	/*
	 * PP G Integer Phred-scaled genotype posterior probabilities rounded to the closest
integer
PQ 1 Integer Phasing quality
PS 1 Integer Phase set
Table 2: Reserved
	 */
	/**
	 * Integer Phred-scaled genotype posterior probabilities rounded to the closest integer.
	 */
	public static final VcfFormat RESERVED_PP = VcfFormat.builder()
															.id("PP")
															.description("Phred-scaled genotype posterior probabilities rounded to the closest integer")
															.number(VcfNumber.G)
															.type(VcfValueType.Integer)
															.build();
	/**
	 * Integer Phasing quality.
	 */
	public static final VcfFormat RESERVED_PQ = VcfFormat.builder()
															.id("PQ")
															.description("Phasing quality")
															.number(1)
															.type(VcfValueType.Integer)
															.build();
	/**
	 * Integer Phasing set.
	 */
	public static final VcfFormat RESERVED_PS = VcfFormat.builder()
															.id("PS")
															.description("Phasing set")
															.number(1)
															.type(VcfValueType.Integer)
															.build();
	
	
	@NonNull
	private String id;
	@NonNull
	private VcfValueType type;
	@NonNull
	private VcfNumber number;
	@EqualsAndHashCode.Exclude
	private String description;
	@EqualsAndHashCode.Exclude
	@Singular
	private Map<@NonNull String, @NonNull String> parameters;
	
	public static VcfFormatBuilder builder() {
		//this makes parameters default to an empty list
		return new VcfFormatBuilder()
				.parameters(new LinkedHashMap<>())
				;
	}
	
	//this is to make maven javadoc work... lombok will populate class for us
	public static class VcfFormatBuilder{
		
		public VcfFormatBuilder number(int i) {
			this.number = VcfNumber.valueOf(i);
			return this;
		}
		public VcfFormatBuilder number(VcfNumber number) {
			this.number = Objects.requireNonNull(number);
			return this;
		}
	}
}
