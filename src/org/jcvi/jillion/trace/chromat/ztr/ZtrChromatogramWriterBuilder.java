package org.jcvi.jillion.trace.chromat.ztr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramWriter2;

public final class ZtrChromatogramWriterBuilder implements Builder<ChromatogramWriter2>{

	private final File ztrFile; 
	private final OutputStream out;
	public ZtrChromatogramWriterBuilder(File ztrFile){
		if(ztrFile ==null){
			throw new NullPointerException("output file can not be null");
		}
		this.ztrFile = ztrFile;
		this.out =null;
	}
	
	public ZtrChromatogramWriterBuilder(OutputStream out){
		if(out ==null){
			throw new NullPointerException("output stream can not be null");
		}
		this.ztrFile = null;
		this.out =out;
	}

	@Override
	public ChromatogramWriter2 build() {
		try {
			if(out==null){
				return new ZtrChromatogramWriterImpl(ztrFile);
			}
			return new ZtrChromatogramWriterImpl(out);
		} catch (IOException e) {
			throw new IllegalStateException("error creating ztr output file");
		}
	}
	
	private static class ZtrChromatogramWriterImpl implements ChromatogramWriter2{
		private final OutputStream out;
		private volatile boolean closed=false;
		public ZtrChromatogramWriterImpl(File ztrFile) throws IOException{
			IOUtil.mkdirs(ztrFile.getParentFile());
			out = new BufferedOutputStream(new FileOutputStream(ztrFile));
		}
		public ZtrChromatogramWriterImpl(OutputStream out) throws IOException{
			this.out = out;;
		}
		@Override
		public void close() throws IOException {
			out.close();
			closed=false;
			
		}
		@Override
		public void write(Chromatogram c) throws IOException {
			if(closed){
				throw new IllegalStateException("can only write one ztr");
			}
			IOLibLikeZtrChromatogramWriter.INSTANCE.write(c, out);
			close();
		}
		
		
	}
}
