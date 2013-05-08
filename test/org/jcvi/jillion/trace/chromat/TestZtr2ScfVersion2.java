package org.jcvi.jillion.trace.chromat;

import java.io.OutputStream;

import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramWriterBuilder;

public class TestZtr2ScfVersion2 extends AbstractTestConvertZtr2Scf{
	@Override
	protected  ChromatogramWriter createScfWriter(OutputStream out) {
		ChromatogramWriter writer = new ScfChromatogramWriterBuilder(out)
        								.useVersion2Encoding()
        								.build();
		return writer;
	}

}
