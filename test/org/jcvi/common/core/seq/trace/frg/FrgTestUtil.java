package org.jcvi.common.core.seq.trace.frg;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;

public final class FrgTestUtil {

	private FrgTestUtil(){
		//can not instantiate
	}
	
	public static final int ENCODING_ORIGIN = 0x30;
    public static  QualitySequence decodeQualitySequence(String encodedValues){
    	QualitySequenceBuilder builder = new QualitySequenceBuilder(encodedValues.length());
    	for(int i=0; i<encodedValues.length(); i++){
    		builder.append(encodedValues.charAt(i) - ENCODING_ORIGIN);
    	}
    	
    	return builder.build();
    }
}
