package org.jcvi.jillion.trace.chromat;


import java.io.OutputStream;

import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramWriterBuilder;

public class TestZtr2ScfVersion3 extends AbstractTestConvertZtr2Scf{
	@Override
	protected  ChromatogramWriter2 createScfWriter(OutputStream out) {
		ChromatogramWriter2 writer = new ScfChromatogramWriterBuilder(out)
        								.build();
		return writer;
	}
}
