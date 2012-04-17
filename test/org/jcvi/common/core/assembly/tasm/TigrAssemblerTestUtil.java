package org.jcvi.common.core.assembly.tasm;

import static org.junit.Assert.assertEquals;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;

public final class TigrAssemblerTestUtil {

	private TigrAssemblerTestUtil(){}
	
	public static void assertAllReadsCorrectlyPlaced(Contig<PlacedRead> expected, TigrAssemblerContig actual){
		CloseableIterator<? extends PlacedRead> iter=null;
		try{
			iter = expected.getReadIterator();
			while(iter.hasNext()){
				PlacedRead expectedRead = iter.next();
				assertEquals(expectedRead, actual.getRead(expectedRead.getId()));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
}
