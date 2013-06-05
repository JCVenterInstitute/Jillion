package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.ByteArrayOutputStream;
import java.io.File;
public class TestTasmWriterBuilderOutputStream extends AbstractTestTasmWriterBuilder{

	
	ByteArrayOutputStream out;
	@Override
	protected TasmWriter createTasmWriterFor(File inputTasm) {
		out = new ByteArrayOutputStream((int)inputTasm.length());
		return new TasmFileWriterBuilder(out)
							.build();
	}

	@Override
	protected byte[] getWrittenBytes() {
		return out.toByteArray();
	}
	

}
