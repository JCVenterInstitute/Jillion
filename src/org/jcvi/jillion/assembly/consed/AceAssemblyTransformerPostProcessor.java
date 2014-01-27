package org.jcvi.jillion.assembly.consed;

import java.util.Map;

import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;

public interface AceAssemblyTransformerPostProcessor {

	Map<String, AceContigBuilder> postProcess(Map<String, AceContigBuilder> builderMap);
}
