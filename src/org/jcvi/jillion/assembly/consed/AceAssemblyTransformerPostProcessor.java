package org.jcvi.jillion.assembly.consed;

import java.util.Map;

import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;

public interface AceAssemblyTransformerPostProcessor {

	Map<String, AceContigBuilder> postProcess(Map<String, AceContigBuilder> builderMap, PhdDataStore phdDataStore);

	void postProcess(AceContig contig);
}
