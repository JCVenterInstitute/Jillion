package org.jcvi.common.core.assembly.clc.cas.var;

import java.util.SortedMap;

import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;

public class DefaultReferenceVariations implements ReferenceVariations{

	private final String referenceId;
	private final SortedMap<Long, Variation> map;
	public DefaultReferenceVariations(String referenceId,
			SortedMap<Long, Variation> map) {
		this.referenceId = referenceId;
		this.map = map;
	}
	@Override
	public String getReferenceId() {
		return referenceId;
	}
	@Override
	public CloseableIterator<Variation> getVariationIterator() {
		return CloseableIteratorAdapter.adapt(map.values().iterator());
	}
	
	
	
}
