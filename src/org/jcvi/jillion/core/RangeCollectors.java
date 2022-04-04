package org.jcvi.jillion.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

public final class RangeCollectors {

	
	public static Collector<Range, ?, List<Range>> mergeRanges(){
		return Collector.of( ()-> new ArrayList<Range>(),
				(l, r)-> l.add(r),
				(a, b) -> {a.addAll(b); return a;},
				l-> Ranges.merge(l));
				
	}
	
	public static Collector<Range, ?, List<Range>> mergeRanges(int maxDistance){
		return Collector.of( ()-> new ArrayList<Range>(),
				(l, r)-> l.add(r),
				(a, b) -> {a.addAll(b); return a;},
				l-> Ranges.merge(l, maxDistance ));
				
	}
}
