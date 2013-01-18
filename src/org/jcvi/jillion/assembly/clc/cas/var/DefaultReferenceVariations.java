package org.jcvi.jillion.assembly.clc.cas.var;

import java.util.SortedMap;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

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
	public StreamingIterator<Variation> getVariationIterator() {
		return IteratorUtil.createStreamingIterator(map.values().iterator());
	}
	
	
	
}