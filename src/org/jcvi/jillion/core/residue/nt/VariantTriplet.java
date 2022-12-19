package org.jcvi.jillion.core.residue.nt;

import lombok.Data;

@Data
public class VariantTriplet {

	private final Triplet triplet;
	private final double percent;
	private final int offset1, offset2, offset3;
}
