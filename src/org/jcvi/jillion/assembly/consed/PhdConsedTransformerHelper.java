package org.jcvi.jillion.assembly.consed;

import java.io.IOException;
import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.ace.WholeAssemblyAceTag;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;

interface PhdConsedTransformerHelper {

	PhdInfo writePhd(Phd phd, Date phdDate) throws IOException;
	
	PhdDataStore createDataStore()  throws IOException ;

	WholeAssemblyAceTag createPhdBallWholeAssemblyTag();
}
