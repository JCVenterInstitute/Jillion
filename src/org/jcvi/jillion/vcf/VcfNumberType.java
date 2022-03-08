package org.jcvi.jillion.vcf;

public enum VcfNumberType{
		NUMBER,
		/**
		 * The field has one value per alternate allele. The values must be in the same order as listed in the ALT
column (described in section 1.6).
		 */
		A,
		/**
		 * The field has one value for each possible allele, including the reference. The order of the values must be the
reference allele first, then the alternate alleles as listed in the ALT column.
		 */
		R,
		/**
		 * he field has one value for each possible genotype. The values must be in the same order as prescribed in
section 1.6.2 (see Genotype Ordering).
		 */
		G,
		/**
		 * The number of possible values varies, is unknown or unbounded.
		 */
		DOT;
		
		
	}