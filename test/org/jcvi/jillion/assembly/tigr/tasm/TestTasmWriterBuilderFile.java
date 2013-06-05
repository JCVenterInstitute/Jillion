package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class TestTasmWriterBuilderFile extends AbstractTestTasmWriterBuilder{

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();
	
	private File outputTasm;
	
	@Override
	protected TasmWriter createTasmWriterFor(File inputTasm){
		try {
			outputTasm =temp.newFile();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return new TasmFileWriterBuilder(outputTasm).build();
	}

	@Override
	protected byte[] getWrittenBytes() {
		
		try {
			return IOUtil.toByteArray(outputTasm);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
