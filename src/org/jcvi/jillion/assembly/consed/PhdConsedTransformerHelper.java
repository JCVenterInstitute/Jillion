package org.jcvi.jillion.assembly.consed;

import java.io.IOException;

import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;

interface PhdConsedTransformerHelper {

	PhdInfo writePhd(Phd phd) throws IOException;
	
	PhdDataStore createDataStore()  throws IOException ;
}
