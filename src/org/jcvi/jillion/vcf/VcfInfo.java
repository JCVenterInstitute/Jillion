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
public class VcfInfo {
	/*
	 * AA 1 String Ancestral allele
AC A Integer Allele count in genotypes, for each ALT allele, in the same order as
listed
AD R Integer Total read depth for each allele
ADF R Integer Read depth for each allele on the forward strand
ADR R Integer Read depth for each allele on the reverse strand
AF A Float Allele frequency for each ALT allele in the same order as listed
(estimated from primary data, not called genotypes)
AN 1 Integer Total number of alleles in called genotypes
BQ 1 Float RMS base quality
CIGAR A String Cigar string describing how to align an alternate allele to the reference
allele
	 */
	/**
	 * String Ancestral allele.
	 */
	public static VcfInfo RESERVED_AA =  VcfInfo.builder()
														.id("AA").description("Ancestral allele")
														.number(1)
														.type(VcfValueType.String)
														.build();
	/**
	 * Integer Allele count in genotypes, for each ALT allele, in the same order as listed.
	 */
	public static VcfInfo RESERVED_AC =  VcfInfo.builder()
			.id("AC").description("Allele count in genotypes, for each ALT allele, in the same order as listed")
			.number(VcfNumber.A)
			.type(VcfValueType.Integer)
			.build();
	/**
	 * Integer Total read depth for each allele.
	 */
	public static VcfInfo RESERVED_AD =  VcfInfo.builder()
			.id("AD").description("Total read depth for each allele")
			.number(VcfNumber.R)
			.type(VcfValueType.Integer)
			.build();
	/**
	 * Integer Read depth for each allele on the forward strand
	 */
	public static VcfInfo RESERVED_ADF =  VcfInfo.builder()
			.id("ADF").description("Total read depth for each allele on the forward strand")
			.number(VcfNumber.R)
			.type(VcfValueType.Integer)
			.build();
	
	/**
	 * Integer Read depth for each allele on the reverse strand
	 */
	public static VcfInfo RESERVED_ADR =  VcfInfo.builder()
			.id("ADR").description("Total read depth for each allele on the reverse strand")
			.number(VcfNumber.R)
			.type(VcfValueType.Integer)
			.build();
	/**
	 * Float Allele frequency for each ALT allele in the same order as listed
	 * (estimated from primary data, not called genotypes)
	 */
	public static VcfInfo RESERVED_AF =  VcfInfo.builder()
			.id("AF").description("Allele frequency for each ALT allele in the same order as listed (estimated from primary data, not called genotypes)")
			.number(VcfNumber.A)
			.type(VcfValueType.Float)
			.build();
	/**
	 * Integer Total number of alleles in called genotype.
	 */
	public static VcfInfo RESERVED_AN =  VcfInfo.builder()
			.id("AN").description("Total number of alleles in called genotype")
			.number(1)
			.type(VcfValueType.Integer)
			.build();
	
	/**
	 * Float RMS base quality.
	 */
	public static VcfInfo RESERVED_BQ =  VcfInfo.builder()
			.id("BQ").description("RMS base quality")
			.number(1)
			.type(VcfValueType.Float)
			.build();
	/**
	 * Cigar string describing how to align an alternate allele to the reference allele.
	 */
	public static VcfInfo RESERVED_CIGAR =  VcfInfo.builder()
			.id("CIGAR").description("Cigar string describing how to align an alternate allele to the reference allele")
			.number(VcfNumber.A)
			.type(VcfValueType.String)
			.build();
	
	/*
	 * DB 0 Flag dbSNP membership
DP 1 Integer Combined depth across samples
END 1 Integer End position on CHROM (used with symbolic alleles; see below)
H2 0 Flag HapMap2 membership
H3 0 Flag HapMap3 membership
MQ 1 Float RMS mapping quality
MQ0 1 Integer Number of MAPQ == 0 reads
NS 1 Integer Number of samples with data
SB 4 Integer Strand bias
SOMATIC 0 Flag Somatic mutation (for cancer genomics)
VALIDATED 0 Flag Validated by follow-up experiment
1000G 0 Flag 1000 Genomes membership
	 */
	
	/**
	 * Flag dbSNP membership.
	 */
	public static VcfInfo RESERVED_DB =  VcfInfo.builder()
			.id("DB").description("dbSNP membership")
			.number(0)
			.type(VcfValueType.Flag)
			.build();
	/**
	 * Flag HapMap2 membership.
	 */
	public static VcfInfo RESERVED_H2 =  VcfInfo.builder()
			.id("H2").description("HapMap2 membership")
			.number(0)
			.type(VcfValueType.Flag)
			.build();
	/**
	 * Flag HapMap3 membership.
	 */
	public static VcfInfo RESERVED_H3 =  VcfInfo.builder()
			.id("H3").description("HapMap3 membership")
			.number(0)
			.type(VcfValueType.Flag)
			.build();
	/**
	 * Flag Somatic mutation (for cancer genomics).
	 */
	public static VcfInfo RESERVED_SOMATIC =  VcfInfo.builder()
			.id("SOMATIC").description("Somatic mutation (for cancer genomics)")
			.number(0)
			.type(VcfValueType.Flag)
			.build();
	/**
	 * Flag Validated by follow-up experiment.
	 */
	public static VcfInfo RESERVED_VALIDATED =  VcfInfo.builder()
			.id("VALIDATED").description("Validated by follow-up experiment")
			.number(0)
			.type(VcfValueType.Flag)
			.build();
	
	/**
	 * Flag 1000 Genomes membership.
	 */
	public static VcfInfo RESERVED_1000G =  VcfInfo.builder()
			.id("1000G").description("1000 Genomes membership")
			.number(0)
			.type(VcfValueType.Flag)
			.build();
	/**
	 * Integer Total Depth.
	 */
	public static VcfInfo RESERVED_DP =  VcfInfo.builder()
			.id("DP").description("Combined depth across samples")
			.number(1)
			.type(VcfValueType.Integer)
			.build();
	/**
	 * Integer (4) Strand bias.
	 */
	public static VcfInfo RESERVED_SB =  VcfInfo.builder()
			.id("SB").description("Integer Strand bias")
			.number(4)
			.type(VcfValueType.Integer)
			.build();
	
	/**
	 * Float RMS mapping quality.
	 */
	public static VcfInfo RESERVED_MQ =  VcfInfo.builder()
			.id("MQ").description("RMS mapping quality")
			.number(1)
			.type(VcfValueType.Float)
			.build();
	/**
	 * Integer Number of MAPQ == 0 reads.
	 */
	public static VcfInfo RESERVED_MQ0 =  VcfInfo.builder()
			.id("MQ0").description("Number of MAPQ == 0 reads")
			.number(1)
			.type(VcfValueType.Integer)
			.build();
	/**
	 * Integer Number of samples with data.
	 */
	public static VcfInfo RESERVED_NS =  VcfInfo.builder()
			.id("NS").description("Number of samples with data")
			.number(1)
			.type(VcfValueType.Integer)
			.build();
	/**
	 * Integer End position on CHROM.
	 * <p>
	 * END: End reference position (1-based), indicating the variant spans positions POS–END on reference/contig
	 * CHROM. Normally this is the position of the last base in the REF allele, so it can be derived from POS and
	 * the length of REF, and no END INFO field is needed. However when symbolic alleles are used, e.g. in gVCF or
	 * structural variants, an explicit END INFO field provides variant span information that is otherwise unknown.
	 * This field is used to compute BCF’s rlen field (see 6.3.1) and is important when indexing VCF/BCF files to
	 * enable random access and querying by position.
	 * </p>
	 */
	public static VcfInfo RESERVED_END =  VcfInfo.builder()
			.id("END").description("End position on CHROM")
			.number(1)
			.type(VcfValueType.Integer)
			.build();
	
	@NonNull
	private String id;
	//equals and hash exclude because the description can be different in vcf files or slightly re-worded in vcf spec versions.
	@EqualsAndHashCode.Exclude
	private String description;
	@NonNull
	private VcfNumber number;
	@NonNull
	private VcfValueType type;
	
	@EqualsAndHashCode.Exclude
	@Singular
	private Map<@NonNull String, @NonNull String> parameters;
	
	public static VcfInfoBuilder builder() {
		return new VcfInfoBuilder()
				.parameters(new LinkedHashMap<>());
	}
	
	//this is to make maven javadoc work... lombok will populate class for us
	public static class VcfInfoBuilder {
		
		public VcfInfoBuilder number(int i) {
			this.number = VcfNumber.valueOf(i);
			return this;
		}
		public VcfInfoBuilder number(VcfNumber number) {
			this.number = Objects.requireNonNull(number);
			return this;
		}
	}
}
