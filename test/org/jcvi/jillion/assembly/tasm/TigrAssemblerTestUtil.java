package org.jcvi.jillion.assembly.tasm;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.tasm.TasmContig;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public final class TigrAssemblerTestUtil {

	private TigrAssemblerTestUtil(){}
	
	public static void assertAllReadsCorrectlyPlaced(Contig<AssembledRead> expected, TasmContig actual){
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
