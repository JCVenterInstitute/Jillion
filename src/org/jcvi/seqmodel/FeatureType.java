package org.jcvi.seqmodel;

/**
 * {@code FeatureType} enumeration can be used to enforce a controlled
 * vocabulary on typing features. 
 * 
 * @author naxelrod
 */

public enum FeatureType {
	ASSEMBLY,
	CDS,
	PEPTIDE,
	THREE_PRIME_UTR,
	FIVE_PRIME_UTR,
	GENERIC;
	
	private static final String DEFAULT_TYPE = "ASSEMBLY";
	
	public static FeatureType getFeatureType(String type) {
		type = (type == null) ? DEFAULT_TYPE : type.toUpperCase();
		if (type.equals("CDS")) {
			return FeatureType.CDS;
		} 
		else if (type.equals("PEPTIDE")) {
			return FeatureType.PEPTIDE;
		}
		else if (type.equals("THREE_PRIME_UTR")) {
			return FeatureType.THREE_PRIME_UTR;
		}
		else if (type.equals("FIVE_PRIME_UTR")) {
			return FeatureType.FIVE_PRIME_UTR;
		}
		else if (type.equals("ASSEMBLY")) {
			return FeatureType.ASSEMBLY;
		}
		else if (type.equals("GENERIC")) {
			return FeatureType.GENERIC;
		}
		throw new AssertionError("Unknown Feature Type: " + type);
	}
	
	public boolean hasFeatureType(String type) {
		return (getFeatureType(type) != null);
	}
}