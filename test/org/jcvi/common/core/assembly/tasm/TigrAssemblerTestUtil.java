package org.jcvi.common.core.assembly.tasm;

import static org.junit.Assert.assertEquals;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;

public final class TigrAssemblerTestUtil {

	private TigrAssemblerTestUtil(){}
	
	public static void assertAllReadsCorrectlyPlaced(Contig<AssembledRead> expected, TigrAssemblerContig actual){
		StreamingIterator<? extends AssembledRead> iter=null;
		try{
			iter = expected.getReadIterator();
			while(iter.hasNext()){
				AssembledRead expectedRead = iter.next();
				assertEquals(expectedRead, actual.getRead(expectedRead.getId()));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
}
